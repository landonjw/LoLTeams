(ns lolteams.backend.test-utils
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-seeder]))

(defn create-test-user
  ([]
   (create-test-user 1))
  ([index]
   {:id 0
    :username (str "testaccount" index)
    :password "testpass123"
    :email (str "example" index "@example.com")
    :server-id 1
    :in-game-name (str "testaccount" index)}))

(defmacro with-test-db [& body]
  `(next.jdbc/with-transaction [transaction# (lolteams.backend.test-seeder/test-database) {:rollback-only true}]
     (as-> transaction# ~'transaction (do ~@body))))