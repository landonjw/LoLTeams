(ns lolteams.backend.models.rbac.permission
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn record->entity [record]
  (if record
    {:id (:rbac_permission/id record)
     :name (:rbac_permission/name record)
     :comments (:rbac_permission/comments record)}))

(defn get-all-permissions [db]
  (->> (jdbc/execute! db ["SELECT * FROM RBAC_Permission;"])
       (map record->entity)
       (into [])))

(defn get-by-id [db id]
  (-> (jdbc/execute! db ["SELECT * FROM RBAC_Permission WHERE Id = ?;" id])
      (first)
      (record->entity)))

(defn get-permissions-for-role [db role-id]
  (->> (jdbc/execute! db ["SELECT RBAC_Permission.Id,
                           RBAC_Permission.Name,
                           RBAC_Permission.Comments
                           FROM RBAC_Permission
                           JOIN RBAC_RolePermission ON RBAC_Permission.Id = RBAC_RolePermission.PermissionId
                           WHERE RBAC_RoleId = ?;" role-id])
       (map record->entity)
       (into [])))

(defn get-permissions-for-user [db user-id]
  (->> (jdbc/execute! db ["SELECT RBAC_Permission.Id,
                           RBAC_Permission.Name,
                           RBAC_Permission.Comment
                           FROM RBAC_Permission
                           LEFT JOIN RBAC_UserPermission ON RBAC_Permission.Id = RBAC_UserPermission.PermissionId
                           LEFT JOIN RBAC_RolePermission ON RBAC_Permission.Id = RBAC_RolePermission.PermissionId
                           LEFT JOIN RBAC_Role ON RBAC_RolePermission.RoleId = RBAC_Role.Id
                           LEFT JOIN RBAC_UserRole ON RBAC_Role.Id = RBAC_UserRole.RoleId
                           LEFT JOIN UserAccount ON RBAC_UserRole.UserAccountId = UserAccount.Id OR RBAC_UserPermission.UserAccountId = UserAccount.Id
                           WHERE UserAccount.Id = ?;" user-id])
       (map record->entity)
       (into [])))