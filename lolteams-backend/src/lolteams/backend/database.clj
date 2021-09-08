(ns lolteams.backend.database
  (:require [next.jdbc :as jdbc]))

(defonce datasource (atom nil))

(defn database-properties [config]
  (assoc (:database-properties config) :dbtype "postgres"))

(defn create-datasource! [db-props]
  (println "Connecting to database.")
  (reset! datasource (jdbc/get-datasource db-props)))