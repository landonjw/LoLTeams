(ns lolteams.backend.controllers.ping)

(defn ping-get [_]
  {:status 200
   :body "Pong"})