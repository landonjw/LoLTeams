(ns lolteams.frontend.views
  (:require
    [re-frame.core :as re-frame]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]))

(defn response []
  (let [res (re-frame/subscribe [::subs/ping-response])]
    [:div
     [:h1 "Response"]
     [:p (or @res "N/A")]]))

(defn login-panel []
  [:form.box.container.login-panel
   [:div.field
    [:label.label "Email"]
    [:div.control
     [:input.input {:type "email" :placeholder "eg. landonjwdev@gmail.com"}]]]
   [:div.field
    [:label.label "Password"]
    [:div.control
     [:input.input {:type "password" :placeholder "********"}]]]
   [:button.button.is-primary "Sign In"]])

(defn champion-input []
  [:form.box.container.login-panel
   [:div.field
    [:label.label "Champion"
     [:div.control
      [:input.input {:placeholder "eg. Gangplank"
                     :on-change   #(re-frame/dispatch [::events/champion-input (-> % .-target .-value)])}]]]]
   [:button.button.is-primary {:type     "button"
                               :on-click #(re-frame/dispatch [::events/get-champion-uri])} "Select"]])

(defn champion-modal []
  (let [portrait-uri (re-frame/subscribe [::subs/champion-portrait])]
    (if @portrait-uri
      [:div.modal.is-active
       [:div.modal-background]
       [:div.modal-content
        [:p.image]
        [:img {:src   @portrait-uri
               :alt   "Foobar"
               :style {:width        "50%"
                       :margin-left  "auto"
                       :margin-right "auto"
                       :display      "block"}}]]
       [:button.modal-close.is-large {:type "button"
                                      :on-click #(re-frame/dispatch [::events/clear-champion-portrait])}]])))

(defn main-panel []
  [:div
   [champion-modal]
   [champion-input]])