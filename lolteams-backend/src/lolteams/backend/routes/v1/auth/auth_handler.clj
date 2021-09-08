(ns lolteams.backend.routes.v1.auth.auth-handler
  (:require [lolteams.backend.routes.v1.auth.user :as user]
            [ring.util.http-response :refer :all]))

(defn login-post [{params :body-params}]
  (let [username (:username params)
        password (:password params)]
    (if (user/authenticates? username password)
      (ok (comment "TODO: JWT"))
      (unauthorized "Authentication error"))))

(defn register-post [{params :body-params}]
  (println params)
  (let [{:keys [username email password]} params
        create-result (user/create-user! username password email)]
    (if (:success? create-result)
      (created "/" (comment "TODO: JWT"))
      (bad-request (:message create-result)))))