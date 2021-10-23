(ns lolteams.backend.routes.v1.ping-handler
  (:require [lolteams.backend.routes.v1.auth.user-auth :as auth]
            [ring.util.http-response :refer :all]))

(defn ping-get [request]
  (auth/wrap-authentication request #(ok "Pong")))