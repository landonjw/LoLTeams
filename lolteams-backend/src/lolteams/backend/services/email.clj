(ns lolteams.backend.services.email
  (:require [postal.core :as postal]))

(defn create-email-sender [config]
  (fn [target-email subject body]
    (postal/send-message {:host "smtp.gmail.com"
                          :ssl true
                          :user (get-in config [:email-service :user])
                          :pass (get-in config [:email-service :password])}
                         {:from (get-in config [:email-service :sender])
                          :to target-email
                          :subject subject
                          :body body})))

(defn send-forgot-password-email [config]
  ((create-email-sender config) "landonjwdev@gmail.com" "Hello World" "Hello World"))