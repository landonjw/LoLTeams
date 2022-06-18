(ns lolteams.backend.core
  (:require [org.httpkit.server :refer [run-server]]
            [lolteams.backend.config :as config]
            [reitit.ring :as ring]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-response-middleware
                                                     format-request-middleware]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [muuntaja.core :as muuntaja]
            [lolteams.backend.handlers.v1.auth :as auth-handler]
            [lolteams.backend.handlers.v1.game-server :as game-server-handler]
            [lolteams.backend.handlers.v1.debug :as debug-handler]
            [lolteams.backend.handlers.v1.champion :as champion-handler]
            [lolteams.backend.handlers.v1.user :as user-handler]
            [lolteams.backend.middleware.auth :refer [jwt-auth-middleware]]
            [lolteams.backend.middleware.cors :refer [cors-middleware]]
            [lolteams.backend.services.datadragon :as datadragon-service]
            [next.jdbc :as jdbc]
            [reitit.ring.coercion :as coercion]))

(defonce server (atom nil))
(defonce routes (atom nil))
(defonce config (atom nil))
(defonce database (atom nil))
(defonce data-dragon (atom nil))

(defn create-routes [db config data-dragon]
  (println "Creating routes...")
  (ring/ring-handler
    (ring/router
      [["/api/v1" {:middleware [cors-middleware]}
        ["/debug"
         ["/ping"
          ["/auth" {:get        debug-handler/ping-with-auth
                    :middleware [#(jwt-auth-middleware config %)]}]
          ["/noauth" {:get debug-handler/ping}]]
         ["/postexample" {:post debug-handler/post-example}]
         ["/getexample" {:get debug-handler/get-example}]]
        ["/auth"
         ["/login" {:post (auth-handler/login-user db config)}]
         ["/register" {:post (auth-handler/register-user db config)}]
         ["/forgotpassword" {:post (auth-handler/send-password-reset-email db config)}]]
        ["/gameserver"
         ["/all" {:get (game-server-handler/get-all-servers db)}]]
        ["/user"
         ["/username" {:post (user-handler/get-user-by-username db)
                       :middleware [#(jwt-auth-middleware config %)]}]]
        ["/champion"
         ["/portrait" {:get (champion-handler/get-portrait-uri data-dragon)}]]]]
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

(defn create-datasource! [config]
  (println "Connecting to database...")
  (jdbc/get-datasource (:database-properties config)))

(defn start-server! [config routes]
  (println "Starting server...")
  (let [port (:server-port config)
        server (run-server routes {:port port})]
    (println "Server running on port " port ".")
    server))

(defn stop-server! []
  (println "Stopping server...")
  (@server :timeout 100)
  (reset! server nil))

(defn reset-data-dragon! []
  (println "Resetting data dragon...")
  (reset! data-dragon (datadragon-service/fetch-data!)))

(defn restart-server! []
  (println "Restarting server...")
  (stop-server!)
  (let [new-server (start-server! @config @routes)]
    (reset! server new-server)))

(defn reset-routes-and-server! []
  (println "Resetting routes and server...")
  (stop-server!)
  (let [new-routes (create-routes @database @config @data-dragon)
        new-server (start-server! @config new-routes)]
    (reset! routes new-routes)
    (reset! server new-server)))

(defn -main []
  (let [new-config (config/create-config!)
        new-data-dragon (datadragon-service/fetch-data!)
        new-database (create-datasource! new-config)
        new-routes (create-routes new-database new-config new-data-dragon)
        new-server (start-server! new-config new-routes)]
    (reset! config new-config)
    (reset! data-dragon new-data-dragon)
    (reset! routes new-routes)
    (reset! database new-database)
    (reset! server new-server)))