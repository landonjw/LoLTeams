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

(defn main-panel []
  [:div
   [response]
   [:button.button {:on-click #(re-frame/dispatch [::events/ping])} "Ping"]])