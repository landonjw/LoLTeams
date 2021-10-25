(ns lolteams.backend.handlers.v1.champion
  (:require [struct.core :as st]
            [lolteams.backend.models.champion-assets :as champ-asset-model]
            [ring.util.http-response :refer :all]))

(def portrait-request-schema
  "
  Schema for the portrait request model.

  Rules:
    Champion is present and is a string.
  "
  {"champion" [st/required st/string]})

(defn get-portrait-uri [data-dragon]
  "
  Creates the endpoint for getting a champion's portrait uri.

  Endpoint Type: GET

  Request Model:
    champion => The name of the champion to get portrait uri for.

  Response(s):
    200:
      Request has succeeded.
      Body will contain the URI to the champion.
    400:
      The request contains a bad data model.
      Body will include any errors caught in data model.
    404:
      There is no champion portrait for the given input.
  "
  (fn [{params :params}]
    (let [errors (first (st/validate params portrait-request-schema))]
      (if errors
        (bad-request errors)
        (let [champion-name (get params "champion")
              portrait-uri (champ-asset-model/champion-name->portrait-uri data-dragon champion-name)]
          (if portrait-uri
            (ok portrait-uri)
            (not-found)))))))