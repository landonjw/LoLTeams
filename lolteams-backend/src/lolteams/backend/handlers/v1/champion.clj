(ns lolteams.backend.handlers.v1.champion
  (:require [struct.core :as st]
            [lolteams.backend.models.champion-assets :as champ-asset-model]
            [ring.util.http-response :refer :all]))

(def portrait-request-schema
  {:champion [st/required st/string]})

(defn get-portrait-uri [data-dragon]
  (fn [{params :params}]
    (let [errors (first (st/validate params portrait-request-schema))]
      (if errors
        (bad-request errors)
        (let [champion-name (get params "champion")
              portrait-uri (champ-asset-model/champion-name->portrait-uri data-dragon champion-name)]
          (if portrait-uri
            (ok portrait-uri)
            (not-found)))))))