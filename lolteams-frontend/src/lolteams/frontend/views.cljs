(ns lolteams.frontend.views
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]
    [lolteams.frontend.debug :as debug]
    [lolteams.frontend.views.elements.authentication :as auth]))

(defn response []
  (let [res (subscribe [::subs/ping-response])]
    [:div
     [:h1 "Response"]
     [:p (or @res "N/A")]]))

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

(defn ping-debug []
  (let [ping-success? (subscribe [::subs/ping-success?])]
    [:div
     (cond
       (= @ping-success? true) [:p "Success!"]
       (= @ping-success? false) [:p "Failure :("])
     [:button.button.is-primary {:type "button"
                                 :on-click #(dispatch [::events/send-ping])}
      "Send Ping"]]))

(defn navbar []
  [:nav.navbar {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    [:a.navbar-item
     [:img {:src "https://bulma.io/images/bulma-logo.png" :width 112 :height 28}]]
    [:a.navbar-burger {:role "button"
                       :aria-label "menu"
                       :aria-expanded false
                       :data-target "navbarBasicExample"}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]]
   [:div.navbar-menu {:id "navbarBasicExample"}
    [:div.navbar-start
     [:a.navbar-item "Team Viewer"]
     [:a.navbar-item "Player Viewer"]
     [:div.control.is-loading
      [:input.input {:placeholder "Search for a team or player" :style {:width "400px"}}]]]]
   [:div.navbar-end
    [:div.navbar-item.has-dropdown.is-hoverable
     [:a.navbar-link "Sample User"]
     [:div.navbar-dropdown
      [:a.navbar-item "Sample User"]
      [:a.navbar-item "Sample Team"]
      [:hr.navbar-divider]
      [:a.navbar-item "Create new team"]
      [:a.navbar-item "Admin panel"]
      [:hr.navbar-divider]
      [:a.navbar-item "Log out"]]]]])

(defmulti page identity)

(defmethod page :login []
  [auth/login-panel])

(defmethod page :forgot-password []
  [auth/forgot-password])

(defmethod page :register []
  [auth/register-panel])

(defmethod page :ping-debug []
  [ping-debug])

(defmethod page :navbar []
  [navbar])

(defmethod page :default []
  [:div "No page"])

(defn main-panel []
  (let [active-page (subscribe [::subs/active-page])
        debug-mode? (subscribe [::subs/debug-mode?])]
    [:div
     (if @debug-mode?
       [debug/page-navigation-bar])
     [page @active-page]]))