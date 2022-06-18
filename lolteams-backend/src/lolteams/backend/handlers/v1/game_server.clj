(ns lolteams.backend.handlers.v1.game-server
  (:require [ring.util.http-response :refer :all]
            [lolteams.backend.models.game-server :as model]))

(defn get-all-servers [db]
  "
  Creates the endpoint for getting the set of supported League of Legends servers.

  Endpoint Type: GET

  Request Model:
    N/A

  Response Model:
    200:
      Body will contain a set of all supported League of Legends servers.
  "
  (fn [_]
    (ok (->> (model/get-all-servers db)
             (into [])))))