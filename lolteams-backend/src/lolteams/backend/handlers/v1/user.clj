(ns lolteams.backend.handlers.v1.user
  (:require [ring.util.http-response :refer :all]
            [lolteams.backend.services.authentication :as auth-service]
            [lolteams.backend.models.user-account :as user-model]))

(defn get-user-by-username [db]
  (fn [{params :params :as request}]
    (auth-service/if-authenticated request
      #(->> (get params "username")
            (user-model/get-by-username db)
            (user-model/entity->dto)
            (ok)))))