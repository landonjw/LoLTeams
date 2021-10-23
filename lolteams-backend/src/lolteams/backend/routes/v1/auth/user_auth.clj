(ns lolteams.backend.routes.v1.auth.user-auth
  (:require [buddy.core.keys :as keys]
            [clojure.java.io :as io]
            [buddy.sign.jwt :as jwt]
            [buddy.sign.jwt]
            [clojure.string :refer [lower-case]]
            [clj-time.core :as time]
            [ring.util.http-response :refer [unauthorized]]))

(defn public-key [public-key-filename]
  (keys/public-key (io/resource (str "keys/" public-key-filename))))

(defn private-key [private-key-filename passphrase]
  (keys/private-key (io/resource (str "keys/" private-key-filename)) passphrase))

(defn create-auth-token
  ([auth-config username]
   (create-auth-token auth-config username (time/plus (time/now) (time/days 1))))
  ([auth-config username expiration]
   (let [private-key (private-key (:private-key-filename auth-config) (:passphrase auth-config))]
     (jwt/sign {:username (lower-case username)}
               private-key
               {:alg :rs256 :exp expiration}))))

(defn decode-token [auth-config token]
  (let [public-key (public-key (:public-key-filename auth-config))]
    (jwt/unsign token public-key {:alg :rs256})))

(defn decode-for-buddy [auth-conf _ token]
  (when-let [result (decode-token auth-conf token)]
    (assoc result :token token)))

(defn authorized? [request]
  (boolean (:identity request)))

(defn wrap-authentication [request fn]
  "Wraps a function in an authorization check.
  If the user is not authorized"
  (if (authorized? request)
    (fn)
    (unauthorized)))