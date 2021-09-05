(ns lolteams.frontend.subs
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::ping-response
  (fn [db]
    (get db :ping)))