(ns lolteams.backend.routes.v1.datadragon.core
  (:require [org.httpkit.client :as http]
            [lolteams.backend.config :refer [config]]
            [clojure.data.json :as json]
            [chime.core :refer [chime-at periodic-seq]])
  (:import (java.time Instant)
           (java.time Duration)))

(def root-uri "http://ddragon.leagueoflegends.com")

(defn cdn-uri [version]
  "Builds a URI to the data dragon CDN for a given version.

  **Example Usage**:
  (cdn-uri \"11.17.1\")
  ; => http://ddragon.leagueoflegends.com/cdn/11.17.1/
  "
  (str root-uri "/cdn/" version "/"))

(defn fetch-data-dragon-version! []
  "Fetches the latest version of data dragon from the versions endpoint.

  **Example Usage**:
      #!clj
      (data-dragon-version!)
      ; => \"11.17.1\"
  "
  (-> @(http/get (str root-uri "/api/versions.json"))
      (:body)
      (json/read-str)
      (first)))

(defn fetch-champion-data! [version]
  "Fetches the champion data from data dragon CDN for a given version.

  Returns a map containing all data for champions on the supplied version.
  "
  (-> (str (cdn-uri version) "data/en_US/champion.json")
      (http/get)
      (deref)
      (:body)
      (json/read-str :key-fn keyword)
      (:data)))

(defn champion-uri-name-pair [champion-entry]
  (let [champion-name (first champion-entry)
        champion-uri-name (:name (second champion-entry))]
    [champion-name champion-uri-name]))

(defn champion-uri-names [champion-data]
  "Given a map of champion data, this extracts the URI and human-friendly champion names into a new map.

  **Example Usage**:
      #!clj
      (champion-uri-names {:MonkeyKing {:name \"Wukong\"} :Gangplank {:name \"Gangplank\"}})
      => {:MonkeyKing \"Wukong\" :Gangplank \"Gangplank\"}
  "
  (->> (map champion-uri-name-pair champion-data)
       (reduce #(assoc %1 (first %2) (second %2)) {})))

(defn fetch-data-dragon-data! [version]
  "Fetches data from the data dragon CDN and populates a map containing the state used in this application."
  (-> {}
      (assoc :version version)
      (assoc :champions (fetch-champion-data! version))
      (#(assoc % :champion-uri-names (champion-uri-names (:champions %))))))

; Stores the data dragon data used in the application.
; This gets reset on a repeated basis to keep items up to date with current league version.
(def data-dragon-state (atom (fetch-data-dragon-data! (fetch-data-dragon-version!))))

(defn version-changed? [current new]
  (not (= current new)))

(defn refresh-data-dragon-data! []
  "Refreshes the data dragon data used in the application if the newest version has changed.
  The purpose of this is to keep all data up to date in the case of League of Legends patch updates.
  "
  (let [latest-version (fetch-data-dragon-version!)]
    (if (version-changed? (:version @data-dragon-state) latest-version)
      (fetch-data-dragon-data! latest-version))))

(defn schedule-version-lookup [refresh-rate-minutes]
  "
  Starts a schedule that refreshes the data dragon data at a supplied interval.
  The purpose of this is to keep all data up to date in the case of League of Legends patch updates.
  "
  (chime-at
    (periodic-seq (Instant/now) (Duration/ofMinutes refresh-rate-minutes))
    (fn [_]
      (refresh-data-dragon-data!))))

(schedule-version-lookup (get-in config [:data-dragon :version-refresh-rate-minutes]))