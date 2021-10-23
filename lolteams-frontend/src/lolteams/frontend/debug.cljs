(ns lolteams.frontend.debug
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [lolteams.frontend.events :as events]
    [lolteams.frontend.subs :as subs]
    [lolteams.frontend.views.util.forms :as form-utils]))

(def pages [:login
            :register
            :forgot-password])

(defn page-navigation-bar []
  (form-utils/select
    pages
    #(dispatch [::events/change-page (-> %
                                         (form-utils/input->value)
                                         (keyword))])))