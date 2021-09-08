(ns lolteams.backend.core
  (:require [org.httpkit.server :refer [run-server]]
            [lolteams.backend.config :refer [config]]
            [lolteams.backend.database :as database]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-response-middleware
                                                     format-request-middleware]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [muuntaja.core :as muuntaja]
            [lolteams.backend.routes.v1.ping-handler :as ping-handler]
            [lolteams.backend.routes.v1.datadragon.champion-handler :as champion-handler]
            [lolteams.backend.routes.v1.auth.auth-handler :as auth-handler]
            [chime.core :refer [chime-at periodic-seq]]))

(defonce server (atom nil))

(def app
  (ring/ring-handler
    (ring/router
      [["/api/v1"
        ["/ping" {:get ping-handler/ping-get}]
        ["/datadragon"
         ["/champion"
          ["portrait" {:get champion-handler/portrait-get}]]]
        ["/auth"
         ["/login" {:post auth-handler/login-post}]
         ["/register" {:post auth-handler/register-post}]]]]
      {:data {:muuntaja   muuntaja/instance
              :middleware [format-negotiate-middleware
                           parameters-middleware
                           format-response-middleware
                           exception-middleware
                           format-request-middleware]}})
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler {:status 404
                                    :body   "Route not found"}))))

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
  (database/create-datasource! (database/database-properties config))
  (start-server! (:server-port config)))