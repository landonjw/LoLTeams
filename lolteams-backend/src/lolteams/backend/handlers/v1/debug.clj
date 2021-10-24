(ns lolteams.backend.handlers.v1.debug
  (:require [lolteams.backend.auth.authenticator :as authenticator]
            [ring.util.http-response :refer :all]))

(defn ping [_]
  (ok "Pong"))

(defn ping-with-auth [request]
  (authenticator/if-authorized request #(ok "Pong")))