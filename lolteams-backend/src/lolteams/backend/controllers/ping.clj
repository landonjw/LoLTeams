(ns lolteams.backend.controllers.ping)

(defn ping-get-handler [_]
  {:status 200
   :body "Pong"})