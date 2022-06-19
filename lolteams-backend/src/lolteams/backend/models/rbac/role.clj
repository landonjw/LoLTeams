(ns lolteams.backend.models.rbac.role
  (:require [next.jdbc :as jdbc]
            [lolteams.backend.models.rbac.permission :refer [get-permissions-for-role]]))

(defn record->lite-entity [record]
  (if record
    {:id       (:rbac_role/id record)
     :name     (:rbac_role/name record)
     :comments (:rbac_role/comments record)}))

(defn lite-entity->entity [db lite-entity]
  (if lite-entity
    (let [permissions (get-permissions-for-role db (:id lite-entity))]
      (conj lite-entity {:permissions permissions}))))

(defn record->entity [record permissions]
  (if record
    (-> (record->lite-entity record)
        (conj {:permissions permissions}))))

(defn get-by-id [db id]
  (->> (jdbc/execute! db ["SELECT RBAC_Role.Id,
                          RBAC_Role.Name,
                          RBAC_Role.Comments
                          FROM RBAC_Role
                          WHERE RBAC_Role.Id = ?;" id])
       (first)
       (record->lite-entity)
       (lite-entity->entity db)))

(defn get-roles-for-user [db user-id]
  (-> (jdbc/execute! db ["SELECT RBAC_Role.Id,
                          RBAC_Role.Name,
                          RBAC_Role.Comments,
                          FROM RBAC_Role
                          JOIN RBAC_UserRole ON RBAC_Role.Id = RBAC_UserRole.RoleId
                          WHERE RBAC_UserRole.UserAccountId = ?;" user-id])))

(defn create-role! [db role]
  (->> (jdbc/execute! db ["INSERT INTO RBAC_Role (Name, Comments)
                           VALUES (?, ?);" (:name role) (:comments role)]
                      {:return-keys true})
       (first)
       (:rbac_role/id)
       (assoc role :id)))