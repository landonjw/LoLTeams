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

(re-frame/reg-sub
  ::login-credentials
  (fn [db]
    (get db :login-credentials {:email "" :password ""})))

(re-frame/reg-sub
  ::ping-response
  (fn [db]
    (get db :ping)))

(re-frame/reg-sub
  ::active-page
  (fn [db]
    (get db :active-page)))

(re-frame/reg-sub
  ::debug-mode?
  (fn [db]
    (get db :debug-mode? false)))

(re-frame/reg-sub
  ::ping-success?
  (fn [db]
    (:ping-success? db)))

(re-frame/reg-sub
  ::available-server-names
  (fn [db]
    (map :abbreviation (:game-servers db))))