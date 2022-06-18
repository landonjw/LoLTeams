(ns lolteams.backend.util.validation.handler
  (:require [struct.core :refer [validate]]
            [ring.util.http-response :refer [bad-request unauthorized]]
            [lolteams.backend.services.authentication :refer [authenticated?]]))

(defn not-authorized? []
  false)

(defn get-schema-errors [request schema]
  (first (validate (:body-params request) schema)))

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

;(defmacro endpoint
;  ([[request] body]
;   `(endpoint [request] {} body))
;  ([[request] options body]
;   `(fn [~'request]
;      (cond
;        (and (:requires-authentication? ~options) (is-not-authenticated?)) (unauthorized)
;        (and (:required-roles ~options) (not-authorized?)) (unauthorized)
;        (and (:schema ~options) (schema-invalid?)) (bad-request)
;        :else ~body))))

;(def example-endpoint-schema
;  {:foo [st/required st/string]
;   :bar [st/required st/integer]})
;

;(def example-endpoint
;  (endpoint [request] {:requires-authentication? true
;                       :required-roles           ["ExampleAdmin1" "ExampleAdmin2"]
;                       :schema                   example-endpoint-schema}
;            (ok (+ 1 (get-in request [:params :bar])))))