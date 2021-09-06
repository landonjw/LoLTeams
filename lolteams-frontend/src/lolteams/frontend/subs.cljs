(ns lolteams.frontend.subs
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::ping-response
  (fn [db]
    (get db :ping)))

(re-frame/reg-sub
  ::champion-portrait
  (fn [db]
    (get db :champion-portrait)))

(re-frame/reg-sub
  ::champion-input
  (fn [db]
    (get db :selected-champion)))