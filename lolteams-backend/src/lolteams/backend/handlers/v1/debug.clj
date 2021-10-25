(ns lolteams.backend.handlers.v1.debug
  (:require [lolteams.backend.auth.authenticator :as authenticator]
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
  (authenticator/if-authorized request #(ok "Pong")))