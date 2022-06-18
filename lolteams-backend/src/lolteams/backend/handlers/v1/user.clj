(ns lolteams.backend.handlers.v1.user
  (:require [ring.util.http-response :refer :all]
            [lolteams.backend.services.authentication :as auth-service]
            [lolteams.backend.models.user-account :as user-model]))

(defn get-user-by-username [db]
  (fn [{params :body-params :as request}]
    (auth-service/if-authenticated request
      #(->> (:username params)
            (user-model/get-by-username db)
            (user-model/entity->dto)
            (ok)))))