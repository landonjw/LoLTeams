(ns lolteams.frontend.views.elements.authentication
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]
    [lolteams.frontend.views.util.forms :refer [input
                                                input->value
                                                full-width-button
                                                select]]))

(defn email-input [on-change-fn]
  (input "Email" {:icon       "fa fa-envelope"
                  :input-type "email"
                  :on-change  on-change-fn}))

(defn username-input [on-change-fn]
  (input "Username" {:icon "fa fa-user"
                     :on-change on-change-fn}))

(defn login-panel []
  [:div.container.login-panel
   [:form.box
    (input "Username" {:icon      "fa fa-user"
                       :on-change #(dispatch [::events/login-input :username (input->value %)])})
    (input "Password" {:icon       "fa fa-lock"
                       :input-type "password"
                       :on-change  #(dispatch [::events/login-input :password (input->value %)])})
    (full-width-button "Login" #(dispatch [::events/attempt-login]))
    [:div.center-text
     [:a {:on-click #(dispatch [::events/change-page :forgot-password])} "Forgot your password? Reset here."]]
    [:div.center-text
     [:a {:on-click #(dispatch [::events/change-page :register])} "New? Register here!"]]]])

(defn forgot-password []
  [:div.container.forgot-password-panel
   [:form.box
    [:p.center-text.forgot-password-header "Please enter your email to get a verification code"]
    (email-input #(dispatch [::events/forgot-password-form (input->value %)]))
    (full-width-button "Submit" #(dispatch [::events/attempt-forgot-password]))]])

(defn register-panel []
  (let [available-servers (subscribe [::subs/available-server-names])]
    [:div.container.register-panel
     [:form.box
      (email-input #(dispatch [::events/register-form :email (input->value %)]))
      (username-input #(dispatch [::events/register-form :username (input->value %)]))
      [:div.columns.is-1
       [:div.column.is-one-third
        (select @available-servers #(dispatch [::events/register-form :server (input->value %)]))]
       [:div.column.is-two-thirds
        (input "In-Game Name" {:on-change #(dispatch [::events/register-form :in-game-name (input->value %)])})]]
      (input "Password" {:icon       "fa fa-lock"
                         :input-type "password"
                         :on-change  #(dispatch [::events/register-form :password (input->value %)])})
      (input "Confirm Password" {:icon       "fa fa-lock"
                                 :input-type "password"
                                 :on-change  #(dispatch [::events/register-form :confirm-password (input->value %)])})
      (full-width-button "Register" #(dispatch [::events/attempt-register]))]]))