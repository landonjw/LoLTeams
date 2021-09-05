(ns lolteams.frontend.events
  (:require
    [re-frame.core :as re-frame]
    [lolteams.frontend.db :as db]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [day8.re-frame.http-fx]
    [ajax.core :as ajax]))

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
             db/app-db))

(re-frame/reg-event-fx
  ::ping
  (fn [db]
    {:db db
     :http-xhrio {:method :get
                  :uri "http://localhost:4000/api/v1/ping"
                  :timeout 8000
                  :response-format (ajax/detect-response-format)
                  :on-success [::good-http-result]
                  :on-failure [::bad-http-result]}}))

(re-frame/reg-event-db
  ::good-http-result
  (fn [db [_ result]]
    (assoc db :ping result)))

(re-frame/reg-event-db
  ::bad-http-result
  (fn [db _]
    (assoc db :ping "Request could not be sent")))