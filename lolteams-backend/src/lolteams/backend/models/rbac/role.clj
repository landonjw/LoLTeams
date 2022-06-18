(ns lolteams.backend.models.rbac.role
  (:require [next.jdbc :as jdbc]
            [lolteams.backend.models.rbac.permission :refer [get-permissions-for-role]]))

(defn record->entity [record permissions]
  (if record
    {:id (:rbac_role/id record)
     :name (:rbac_role/name record)
     :comments (:rbac_role/comments record)
     :permissions permissions}))

(defn get-by-id [db id]
  (let [role-record (first ((jdbc/execute! db ["SELECT RBAC_Role.Id,
                           RBAC_Role.Name,
                           RBAC_Role.Comments
                           FROM RBAC_Role
                           WHERE RBAC_Role.Id = ?" id])))]
    (if role-record
      (->> (get-permissions-for-role db (:rbac_role/id role-record))
           (record->entity role-record)))))