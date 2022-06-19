(ns lolteams.backend.handlers.v1.rbac
  (:require [ring.util.http-response :refer :all]
            [lolteams.backend.util.validation.handler :refer [endpoint]]
            [lolteams.backend.models.rbac.permission :as permission-model]
            [lolteams.backend.models.user-account :as account-model]))

(defn get-permissions-for-username [db username]
  (if username
    (let [account (account-model/get-by-username db username)]
      (if account
        (permission-model/get-permissions-for-user db (:id account))))))

(defn get-permissions-for-user [db]
  (endpoint [request] {:requires-authentication? true
                       :required-permissions ["RBAC-View"]}
    (let [params (:params request)
          username (get params "username")
          permissions (get-permissions-for-username db username)]
      (if (nil? username)
        (bad-request {:username "this field is mandatory"})
        (if permissions
          (ok permissions)
          (bad-request {:username "no user exists with this username"}))))))