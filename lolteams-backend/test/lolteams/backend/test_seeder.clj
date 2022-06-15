(ns lolteams.backend.test-seeder
  (:require [clojure.test :refer :all]
            [lolteams.backend.core :as core]
            [lolteams.backend.config :refer [load-extra-config-info]]))

(def config
  (load-extra-config-info
    {:server-port         4000
     :database-properties {:host     "127.0.0.1"
                           :dbname   "lolteamstest"
                           :user     "postgres"
                           :password "postgres"
                           :port     5432}
     :auth                {:public-key-filename  "dev_public_key.pem"
                           :private-key-filename "dev_private_key.pem"
                           :passphrase           nil}}))

(def data-dragon {:version            "11.21.1"
                  :cdn-uri            "http://ddragon.leagueoflegends.com/cdn/11.21.1/"
                  :champion-uri-names {:Gangplank   "Gangplank"
                                       :MonkeyKing  "Wukong"
                                       :AurelionSol "Aurelion Sol"}})

(defonce database (atom nil))

(defonce routes (atom nil))

(defn seed-database! [datasource]
  "Used to seed the database with data necessary for testing")

(defn init-test-env []
  (reset! database
          (let [datasource (core/create-datasource! config)]
            (seed-database! datasource)
            datasource))
  (reset! routes (core/create-routes @database config data-dragon)))

(defn test-config []
  (if (and @database @routes)
    config
    (do
      (init-test-env)
      config)))

(defn test-data-dragon []
  (if (and @database @routes)
    data-dragon
    (do
      (init-test-env)
      data-dragon)))

(defn test-database []
  (if (and @database @routes)
    @database
    (do
      (init-test-env)
      @database)))

(defn test-routes []
  (if (and @database @routes)
    @routes
    (do
      (init-test-env)
      @routes)))