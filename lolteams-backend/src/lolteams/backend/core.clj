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
            [reitit.middleware :as middleware]
            [lolteams.backend.routes.v1.ping-handler :as ping-handler]
            [lolteams.backend.routes.v1.datadragon.champion-handler :as champion-handler]
            [lolteams.backend.routes.v1.auth.auth-handler :as auth-handler]
            [chime.core :refer [chime-at periodic-seq]]
            [lolteams.backend.middleware :refer [jwt-auth-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defonce server (atom nil))

(defn add-cors [handler]
  (wrap-cors handler
             :access-control-allow-origin [#".*"]
             :access-control-allow-methods [:get :post]))

(def app
  (ring/ring-handler
    (ring/router
      [["/api/v1" {:middleware [add-cors]}
        ["/ping" {:get        ping-handler/ping-get
                  :middleware [jwt-auth-middleware]}]
        ["/datadragon"
         ["/champion"
          ["portrait" {:get champion-handler/portrait-get}]]]
        ["/auth"
         ["/login" {:post auth-handler/login-post}]
         ["/register" {:post auth-handler/register-post}]]]]
      {:data                 {:muuntaja   muuntaja/instance
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

(defn test-endpoint []
  (app {:request-method :post
        :uri            "/api/v1/auth/login"
        :body-params    {:username "landonjw123"
                         :password "foobar123"}}))