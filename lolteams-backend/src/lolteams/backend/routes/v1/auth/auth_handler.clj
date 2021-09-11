(ns lolteams.backend.routes.v1.auth.auth-handler
  (:require [lolteams.backend.routes.v1.auth.user :as user]
            [ring.util.http-response :refer :all]
            [clojure.string :refer [lower-case]]
            [lolteams.backend.routes.v1.auth.user-auth :as user-auth]
            [lolteams.backend.config :refer [config]]))

(defn login-post [{params :body-params}]
  (let [username (:username params)
        password (:password params)]
    (if (user/authenticates? username password)
        (ok (user-auth/create-auth-token (:auth config) username))
        (unauthorized "Authentication error"))))

(defn register-post [{params :body-params}]
  (println params)
  (let [{:keys [username email password]} params
        create-result (user/create-user! username password email)]
    (if (:success? create-result)
      (created "/" (user-auth/create-auth-token (:auth config) username))
      (bad-request (:message create-result)))))