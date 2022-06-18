(ns lolteams.backend.handlers.v1.debug
  (:require [lolteams.backend.services.authentication :as auth-service]
            [ring.util.http-response :refer :all]))

(defn ping [_]
  "
  Creates a basic endpoint that replies 'Pong'.

  Endpoint Type: GET

  Response(s):
    200:
      Body will contain 'Pong'.
  "
  (ok "Pong"))

(defn ping-with-auth [request]
  "
  Creates a basic endpoint that replies 'Pong' if the user is authorized.

  Endpoint Type: GET

  Response(s):
    200:
      Request has succeeded.
      Body will contain 'Pong'.
    401:
      User is not authorized to use the request.
  "
  (auth-service/if-authenticated request #(ok "Pong")))

(def post-request (atom nil))
(def get-request (atom nil))

(defn post-example [request]
  (do
    (reset! post-request request)
    (ok)))

(defn get-example [request]
  (do
    (reset! get-request request)
    (ok)))