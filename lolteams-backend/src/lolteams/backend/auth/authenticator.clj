(ns lolteams.backend.auth.authenticator
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :refer [lower-case]]
            [clj-time.core :as time]
            [ring.util.http-response :refer [unauthorized]]
            [lolteams.backend.models.user-account :as user-model]
            [buddy.hashers :as hashers]))

(defn create-auth-token
  "Creates a JWT after a user has authenticated."
  ([private-key username]
   (create-auth-token private-key username (time/plus (time/now) (time/days 1))))
  ([private-key username expiration]
   (jwt/sign {:username (lower-case username)} private-key {:alg :rs256 :exp expiration})))

(defn decode-token [public-key token]
  "Decodes a JWT."
  (try
    (jwt/unsign token public-key {:alg :rs256})
    (catch Exception _ nil)))

(defn decode-for-buddy [public-key _ token]
  "Decodes a JWT. This is only used in middleware."
  (when-let [result (decode-token public-key token)]
    (assoc result :token token)))

(defn authorized? [request]
  "
  Checks if a request is authorized.
  This identity boolean is added to the request from middleware.
  "
  (boolean (:identity request)))

(defn authenticates? [db username password]
  "
  Checks if a user and password successfully authenticates a user in the database.
  "
  (if (or (empty? username) (empty? password))
    false
    (let [user (user-model/username->user-account db username)]
      (->> user
           (:password)
           (hashers/verify password)
           (:valid)))))

(defn if-authorized
  "
  Checks if a user is authorized to make the request, and executes a function if they are.
  Otherwise, if specified, it will execute another function, or return an unauthorized http response.
  "
  ([request fn]
   (if-authorized request fn nil))
  ([request fn else-fn]
   (if (authorized? request)
     (fn)
     (if (nil? else-fn)
       (unauthorized)
       (else-fn)))))

(defn generate-random-token
  ([]
     (generate-random-token "" 6))
  ([partial length]
     (if (<= length 0)
       ""
       (if (= (rem length 2) 0)
         (generate-random-token (str partial (int (rand 10))) (- 1 length))
         (generate-random-token (str partial (char (+ 65 (rand 26)))) (- 1 length))))))

(defn create-reset-token [email]
  ())