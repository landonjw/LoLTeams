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
  (fn-traced [_ _] db/app-db))

(defn json-request [db options]
  {:method          (get options :method :get)
   :uri             (endpoint (:endpoint options))
   :headers         (let [headers (get options :headers {})]
                      (if (:auth-token db)
                        (assoc headers :authorization (str "Token " (:auth-token db)))))
   :timeout         8000
   :params          (:params options)
   :format          (ajax/json-request-format)
   :response-format (ajax/detect-response-format)
   :on-success      (:on-success options)
   :on-failure      (:on-failure options)})

(re-frame/reg-event-db
  ::ping-success
  (fn [cofx _]
    (assoc (:db cofx) :ping-success? true)))

(re-frame/reg-event-db
  ::ping-failure
  (fn [cofx _]
    (assoc (:db cofx) :ping-success? false)))

(re-frame/reg-event-fx
  ::ping
  (fn [cofx _]
    {:db         cofx
     :http-xhrio (json-request (:db cofx)
                               {:method     :get
                                :endpoint   "api/v1/debug/ping/noauth"
                                :on-success [::ping-success]
                                :on-failure [::ping-failure]})}))

(re-frame/reg-event-db
  ::set-champion-portrait
  (fn [db event]
    (assoc db :champion-portrait-uri (:result event))))

(re-frame/reg-event-db
  ::bad-api-request
  (fn [db event]
    (assoc db :api-error (:result event))))

(re-frame/reg-event-fx
  ::get-champion-uri
  (fn [cofx]
    (let [selected-champion (get-in cofx [:db :selected-champion])]
      (if selected-champion
        {:db         cofx
         :http-xhrio (json-request (:db cofx)
                                   {:method     :get
                                    :endpoint   "/v1/champion/portrait"
                                    :params     {:champion selected-champion}
                                    :on-success [::set-champion-portrait]
                                    :on-failure [::bad-api-request]})}))))

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
  (fn [cofx response]
    (assoc (:db cofx) :auth-token (second response))))

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
  (fn [cofx]
    {:db         cofx
     :http-xhrio (json-request (:db cofx)
                               {:method     :post
                                :endpoint   "/v1/auth/login"
                                :params     (get-in cofx [:db :login-form])
                                :on-success [::login-success]
                                :on-failure [::login-failure]})}))

(re-frame/reg-event-fx
  ::send-ping
  (fn [cofx]
    {:db         cofx
     :http-xhrio (json-request (:db cofx)
                               {:method     :get
                                :endpoint   "/v1/debug/ping/auth"
                                :on-success [::ping-success]
                                :on-failure [::ping-failure]})}))

(re-frame/reg-event-db
  ::forgot-password-form
  (fn [db [_ email]]
    (assoc-in db [:forgot-password-form :email] email)))

(re-frame/reg-event-db
  ::show-register-form
  (fn [cofx response]
    (-> (:db cofx)
        (assoc :active-page :register)
        (assoc :game-servers (second response)))))

(re-frame/reg-event-fx
  ::load-register-form
  (fn [cofx]
    {:db         cofx
     :http-xhrio (json-request (:db cofx)
                               {:method     :get
                                :endpoint   "/v1/gameserver/all"
                                :on-success [::show-register-form]
                                :on-failure [::show-error]})}))

(re-frame/reg-event-fx
  ::attempt-register
  (fn [cofx]
    {:db         cofx
     :http-xhrio (json-request (:db cofx)
                               {:method     :post
                                :endpoint   "/v1/auth/register"
                                :params     (get-in cofx [:db :register-form])
                                :on-success [::register-success]
                                :on-failure [::register-failure]})}))

(re-frame/reg-event-db
  ::register-form
  (fn [db [_ input-type value]]
    (assoc-in db [:register-form input-type] value)))

(re-frame/reg-event-db
  ::register-success
  (fn [cofx response]
    (println response)
    (:db cofx)))

(re-frame/reg-event-db
  ::register-failure
  (fn [cofx response]
    (println response)
    (:db cofx)))