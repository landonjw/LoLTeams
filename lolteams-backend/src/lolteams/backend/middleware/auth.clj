(ns lolteams.backend.middleware.auth
  (:require [lolteams.backend.auth.authenticator :as authenticator]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(defn auth [config]
  (token-backend {:authfn #(authenticator/decode-for-buddy (get-in config [:auth :public-key]) %1 %2)}))

(defn jwt-auth-middleware [config handler]
  (wrap-authentication handler (auth config)))