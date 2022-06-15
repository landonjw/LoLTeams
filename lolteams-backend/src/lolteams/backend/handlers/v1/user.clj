(ns lolteams.backend.handlers.v1.user
  (:require [ring.util.http-response :refer :all]
            [lolteams.backend.services.authentication :as auth-service]
            [lolteams.backend.models.user-account :as user-model]))

(defn get-user-by-username [db]
  (fn [{params :body-params :as request}]
    (auth-service/if-authorized request
      #(ok
         (-> (user-model/username->user-account db (:username params))
             (user-model/entity->dto))))))