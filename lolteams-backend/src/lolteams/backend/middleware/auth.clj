(ns lolteams.backend.middleware.auth
  (:require [lolteams.backend.services.authentication :as auth-service]
            [buddy.auth.backends.token :refer [token-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(defn auth [config]
  (token-backend {:authfn #(auth-service/decode-for-buddy (get-in config [:auth :public-key]) %1 %2)}))

(defn jwt-auth-middleware [config handler]
  (wrap-authentication handler (auth config)))