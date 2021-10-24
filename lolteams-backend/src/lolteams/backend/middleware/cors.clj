(ns lolteams.backend.middleware.cors
  (:require [ring.middleware.cors :refer [wrap-cors]]))

(defn cors-middleware [handler]
  (wrap-cors handler
             :access-control-allow-origin [#".*"]
             :access-control-allow-methods [:get :post]))