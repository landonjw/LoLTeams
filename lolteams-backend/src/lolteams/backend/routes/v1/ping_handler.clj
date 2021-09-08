(ns lolteams.backend.routes.v1.ping-handler)

(defn ping-get [_]
  {:status 200
   :body "Pong"})