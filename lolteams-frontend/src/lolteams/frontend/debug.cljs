(ns lolteams.frontend.debug
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]
    [lolteams.frontend.views.util.forms :as form-utils]))

(def pages [:login
            :register
            :forgot-password
            :ping-debug])

(defn page-selection-handler [element]
  (let [page (-> element (form-utils/input->value) (keyword))]
    (cond
      (= page :register) (dispatch [::events/load-register-form])
      :else (dispatch [::events/change-page page]))))

(defn page-navigation-bar []
  (form-utils/select
    pages
    page-selection-handler))