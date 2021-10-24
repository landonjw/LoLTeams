(ns lolteams.backend.routes.v1.ping-handler
  (:require [lolteams.backend.auth.authenticator :as authenticator]
            [ring.util.http-response :refer :all]))

(defn ping-get [request]
  (println "reached")
  (authenticator/if-authorized request #(ok "Pong")))