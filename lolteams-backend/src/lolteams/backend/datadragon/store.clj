(ns lolteams.backend.datadragon.store
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def root-uri "http://ddragon.leagueoflegends.com")

(defn- cdn-uri [version]
  "Builds a URI to the data dragon CDN for a given version.

  **Example Usage**:
  (cdn-uri \"11.17.1\")
  ; => http://ddragon.leagueoflegends.com/cdn/11.17.1/
  "
  (str root-uri "/cdn/" version "/"))

(defn- fetch-data-dragon-version! []
  "Fetches the latest version of data dragon from the versions endpoint.

  **Example Usage**:
      #!clj
      (data-dragon-version!)
      ; => \"11.17.1\"
  "
  (println "Fetching most recent data dragon version...")
  (-> @(http/get (str root-uri "/api/versions.json"))
      (:body)
      (json/read-str)
      (first)))

(defn- fetch-champion-data! [version]
  "Fetches the champion data from data dragon CDN for a given version.

  Returns a map containing all data for champions on the supplied version.
  "
  (-> (str (cdn-uri version) "data/en_US/champion.json")
      (http/get)
      (deref)
      (:body)
      (json/read-str :key-fn keyword)
      (:data)))

(defn- champion-uri-name-pair [champion-entry]
  (let [champion-name (first champion-entry)
        champion-uri-name (:name (second champion-entry))]
    [champion-name champion-uri-name]))

(defn- champion-uri-names [champion-data]
  "Given a map of champion data, this extracts the URI and human-friendly champion names into a new map.

  **Example Usage**:
      #!clj
      (champion-uri-names {:MonkeyKing {:name \"Wukong\"} :Gangplank {:name \"Gangplank\"}})
      => {:MonkeyKing \"Wukong\" :Gangplank \"Gangplank\"}
  "
  (->> (map champion-uri-name-pair champion-data)
       (reduce #(assoc %1 (first %2) (second %2)) {})))

(defn fetch-data-dragon-data! []
  "Fetches data from the data dragon CDN and populates a map containing the state used in this application."
  (println "Fetching most recent data dragon data...")
  (let [version (fetch-data-dragon-version!)]
    (-> {}
        (assoc :version version)
        (assoc :cdn-uri (cdn-uri version))
        (assoc :champions (fetch-champion-data! version))
        (#(assoc % :champion-uri-names (champion-uri-names (:champions %)))))))