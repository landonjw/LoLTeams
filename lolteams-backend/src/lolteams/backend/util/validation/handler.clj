(ns lolteams.backend.util.validation.handler
  (:require [struct.core :refer [validate]]
            [ring.util.http-response :refer [bad-request unauthorized]]
            [lolteams.backend.services.authentication :refer [authenticated?]]))

(defn not-authorized? []
  false)

(defn get-schema-errors [request schema]
  (first (validate (:body-params request) schema)))

; TODO: Figure out how to handle GET requests for the schema validation

(defmacro endpoint
  ([[request] body]
   `(endpoint [request] {} body))
  ([[request] options body]
   `(fn [~'request]
      (cond
        (and (:requires-authentication? ~options) (not (authenticated? ~request))) (unauthorized)
        (and (:required-roles ~options) (not-authorized?)) (unauthorized)
        (:schema ~options)
          (let [errors# (get-schema-errors ~request (:schema ~options))]
            (if errors#
              (bad-request errors#)
              ~body))
        :else ~body))))