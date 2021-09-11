(ns lolteams.backend.middleware
  (:require [buddy.auth.backends.token :refer [token-backend]]
            [lolteams.backend.routes.v1.auth.user-auth :as user-auth]
            [lolteams.backend.config :refer [config]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(def auth
  (token-backend {:authfn #(user-auth/decode-for-buddy (:auth config) %1 %2)}))

(defn jwt-auth-middleware [handler]
  (wrap-authentication handler auth))