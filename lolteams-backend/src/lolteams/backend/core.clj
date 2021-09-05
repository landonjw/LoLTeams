(ns lolteams.backend.core
  (:require [org.httpkit.server :refer [run-server]]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-response-middleware
                                                     format-request-middleware]]
            [muuntaja.core :as muuntaja]
            [next.jdbc :as jdbc]
            [lolteams.backend.controllers.ping :as ping-controller]))

(def config (clojure.edn/read-string (slurp "config.edn")))

(defonce datasource (atom nil))
(defonce server (atom nil))

(def app
  (ring/ring-handler
    (ring/router
      [["api/v1/ping" {:get ping-controller/ping-get}]]
      {:data {:muuntaja muuntaja/instance
              :middleware [format-negotiate-middleware
                           format-response-middleware
                           exception-middleware
                           format-request-middleware]}})
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler {:status 404
                                    :body   "Route not found"}))))

(defn database-properties [config]
  (assoc (:database-properties config) :dbtype "postgres"))

(defn create-datasource! [db-props]
  (println "Connecting to database.")
  (reset! datasource (jdbc/get-datasource db-props)))

(defn start-server! [port]
  (println (str "Running server on port " port "."))
  (reset! server (run-server app {:port port})))

(defn stop-server! []
  (@server :timeout 100)
  (reset! server nil))

(defn restart-server! []
  (stop-server!)
  (start-server! (:server-port @config)))

(defn -main []
  (create-datasource! (database-properties config))
  (start-server! (:server-port config)))