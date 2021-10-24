(ns lolteams.backend.utils
  (:require [clojure.test :refer :all]
            [lolteams.backend.config :as config]
            [lolteams.backend.core :as core]))

(defonce config (atom nil))
(defonce database (atom nil))
(defonce routes (atom nil))

(defn init-test-env []
  (reset! config (config/create-config!))
  (reset! database (core/create-datasource! @config))
  (reset! routes (core/create-routes @database @config)))

(defn test-config []
  (if (and @config @database @routes)
    @config
    (do
      (init-test-env)
      @config)))

(defn test-database []
  (if (and @config @database @routes)
    @database
    (do
      (init-test-env)
      @database)))

(defn test-routes []
  (if (and @config @database @routes)
    @routes
    (do
      (init-test-env)
      @routes)))