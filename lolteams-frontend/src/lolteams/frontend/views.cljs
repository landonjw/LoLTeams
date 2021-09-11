(ns lolteams.frontend.views
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]))

(defn response []
  (let [res (subscribe [::subs/ping-response])]
    [:div
     [:h1 "Response"]
     [:p (or @res "N/A")]]))

(defn login-panel []
  [:div.container.login-panel
   [:form.box
    [:div.field
     [:div.control.has-icons-left
      [:input.input {:type        "email"
                     :placeholder "Email"
                     :on-change   #(dispatch [::events/login-input :username (-> % .-target .-value)])}]
      [:span.icon.is-small.is-left
       [:i.fa.fa-envelope]]]]
    [:div.field
     [:div.control.has-icons-left
      [:input.input {:type        "password"
                     :placeholder "Password"
                     :on-change   #(dispatch [::events/login-input :password (-> % .-target .-value)])}]
      [:span.icon.is-small.is-left
       [:i.fa.fa-lock]]]]
    [:button.button.is-primary.login-button {:type     "button"
                                             :on-click #(dispatch [::events/attempt-login])} "Login"]
    [:div.center-text
     [:a "Forgot your password? Reset here."]]
    [:div.center-text
     [:a "New? Register here!"]]]])

(defn champion-input []
  [:form.box.container.login-panel
   [:div.field
    [:label.label "Champion"
     [:div.control
      [:input.input {:placeholder "eg. Gangplank"
                     :on-change   #(dispatch [::events/champion-input (-> % .-target .-value)])}]]]]
   [:button.button.is-primary {:type     "button"
                               :on-click #(dispatch [::events/get-champion-uri])} "Select"]])

(defn ping-panel []
  (let [ping-response (subscribe [::subs/ping-response])]
    [:div.container
     [:button.button.is-primary {:type "button"
                                 :on-click #(dispatch [::events/send-ping])}  "Test Authentication"]
     [:p {:style {:vertical-align "middle"}} (if @ping-response "Success!")]]))

(defn champion-modal []
  (let [portrait-uri (subscribe [::subs/champion-portrait])]
    (if @portrait-uri
      [:div.modal.is-active
       [:div.modal-background]
       [:div.modal-content
        [:p.image]
        [:img.champion-portrait {:src @portrait-uri
                                 :alt "League of Legends champion portrait"}]]
       [:button.modal-close.is-large {:type     "button"
                                      :on-click #(dispatch [::events/clear-champion-portrait])}]])))

(defn main-panel []
  [:div
   [login-panel]
   [ping-panel]])