(ns lolteams.backend.auth.authenticator
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :refer [lower-case]]
            [clj-time.core :as time]
            [ring.util.http-response :refer [unauthorized]]
            [lolteams.backend.models.user-account :as user-model]
            [buddy.hashers :as hashers]))

(defn create-auth-token
  ([private-key username]
   (create-auth-token private-key username (time/plus (time/now) (time/days 1))))
  ([private-key username expiration]
   (jwt/sign {:username (lower-case username)} private-key {:alt :rs256 :exp expiration})))

(defn decode-token [public-key token]
  (jwt/unsign token public-key {:alg :rs256}))

(defn decode-for-buddy [public-key _ token]
  (when-let [result (decode-token public-key token)]
    (assoc result :token token)))

(defn authorized? [request]
  (boolean (:identity request)))

(defn authenticates? [db username password]
  (if (or (empty? username) (empty? password))
    false
    (let [user (user-model/username->user-account db username)]
      (->> user
           (:useraccount/password)
           (hashers/verify password)
           (:valid)))))

(defn if-authorized
  ([request fn]
   (if-authorized request fn nil))
  ([request fn else-fn]
   (if (authorized? request)
     (fn)
     (if (nil? else-fn)
       (unauthorized)
       (else-fn)))))