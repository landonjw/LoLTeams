(ns lolteams.frontend.events
  (:require
    [re-frame.core :as re-frame]
    [lolteams.frontend.db :as db]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [day8.re-frame.http-fx]
    [ajax.core :as ajax]))

(def backend-uri "http://localhost:4000/api")

(defn endpoint [call]
  (str backend-uri call))

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
    (assoc db :champion-portrait result)))

(re-frame/reg-event-db
  ::bad-http-result
  (fn [db _]
    (assoc db :ping "Request could not be sent")))

(re-frame/reg-event-fx
  ::get-champion-uri
  (fn [db]
    (let [selected-champion (get-in db [:db :selected-champion])]
      (if selected-champion
        {:db         db
         :http-xhrio {:method :get
                      :uri    (endpoint "/v1/datadragon/champion/portrait")
                      :params {:champion selected-champion}
                      :timeout 8000
                      :response-format (ajax/detect-response-format)
                      :on-success [::good-http-result]
                      :on-failure [::bad-http-result]}}))))

(re-frame/reg-event-db
  ::champion-input
  (fn [db event]
    (let [value (second event)]
      (assoc db :selected-champion value))))

(re-frame/reg-event-db
  ::clear-champion-portrait
  (fn [db]
    (dissoc db :champion-portrait)))

(re-frame/reg-event-db
  ::login-email
  (fn [db [_ value]]
    (assoc db :login-email value)))

(re-frame/reg-event-db
  :api-response-failure
  (fn [db [_ request-type response]]
    (assoc db [:errors request-type] (get-in response [:response :errors]))))

(re-frame/reg-event-db
  ::login-input
  (fn [db [_ input-type value]]
    (assoc-in db [:login-form input-type] value)))

(re-frame/reg-event-db
  ::login-success
  (fn [db response]
    (assoc db :auth-token (second response))))

; TODO
(re-frame/reg-event-db
  ::login-failure
  (fn [db response]
    (println response)))

(re-frame/reg-event-db
  ::change-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(re-frame/reg-event-fx
  ::attempt-login
  (fn [state]
    {:db state
     :http-xhrio {:method :post
                  :uri (endpoint "/v1/auth/login")
                  :params (get-in state [:db :login-form])
                  :timeout 8000
                  :format (ajax/json-request-format)
                  :response-format (ajax/detect-response-format)
                  :on-success [::login-success]
                  :on-failure [::login-failure]}}))

(re-frame/reg-event-db
  ::ping-success
  (fn [db response]
    (assoc db :ping (second response))))

(re-frame/reg-event-fx
  ::send-ping
  (fn [state]
    {:db state
     :http-xhrio {:method :get
                  :uri (endpoint "/v1/ping")
                  :headers {:authorization (str "Token " (get-in state [:db :auth-token]))}
                  :timeout 8000
                  :response-format (ajax/detect-response-format)
                  :on-success [::ping-success]}}))

(re-frame/reg-event-db
  ::forgot-password-form
  (fn [db [_ email]]
    (assoc-in db [:forgot-password-form :email] email)))

(re-frame/reg-event-fx
  ::attempt-register
  (fn [state]
    {:db state
     :http-xhrio {:method :post
                  :uri (endpoint "/v1/auth/register")
                  :params (get-in state [:db :register-form])
                  :timeout 8000
                  :format (ajax/json-request-format)
                  :response-format (ajax/detect-response-format)
                  :on-success [::register-success]
                  :on-failure [::register-failure]}}))

(re-frame/reg-event-db
  ::register-form
  (fn [db input-type value]
    (assoc-in db [:register-form input-type] value)))

(re-frame/reg-event-fx
  ::register-success
  (fn [state]
    (println state)))