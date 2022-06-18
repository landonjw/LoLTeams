(ns lolteams.backend.models.user-account
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashers]
            [lolteams.backend.models.game-server :as game-server]))

(defn record->entity [record]
  (if record
    {:id           (:useraccount/id record)
     :username     (:useraccount/username record)
     :password     (:useraccount/password record)
     :email        (:useraccount/email record)
     :in-game-name (:useraccount/ingamename record)
     :game-server  (game-server/record->entity record)}))

(defn entity->dto [entity]
  (dissoc entity :password))

(defn get-by-username [db username]
  (-> (jdbc/execute! db
                     ["SELECT UserAccount.Id,
                       UserAccount.Username,
                       UserAccount.Password,
                       UserAccount.Email,
                       UserAccount.InGameName,
                       GameServer.Id,
                       GameServer.Name,
                       GameServer.Abbreviation
                       FROM UserAccount
                       JOIN GameServer ON UserAccount.GameServerId = GameServer.Id
                       WHERE UserAccount.Username = ?;" username])
      (first)
      (record->entity)))

(defn get-by-email [db email]
  (-> (jdbc/execute! db
                     ["SELECT UserAccount.Id,
                       UserAccount.Username,
                       UserAccount.Password,
                       UserAccount.Email,
                       UserAccount.InGameName,
                       GameServer.Id,
                       GameServer.Name,
                       GameServer.Abbreviation
                       FROM UserAccount
                       JOIN GameServer ON UserAccount.GameServerId = GameServer.Id
                       WHERE UserAccount.Email = ?;" email])
      (first)
      (record->entity)))

(defn get-by-in-game-name [db in-game-name]
  (-> (jdbc/execute! db
                     ["SELECT UserAccount.Id,
                       UserAccount.Username,
                       UserAccount.Password,
                       UserAccount.Email,
                       UserAccount.InGameName,
                       GameServer.Id,
                       GameServer.Name,
                       GameServer.Abbreviation
                       FROM UserAccount
                       JOIN GameServer ON UserAccount.GameServerId = GameServer.Id
                       WHERE UserAccount.InGameName = ?;" in-game-name])
      (first)
      (record->entity)))

(defn create-user! [db username password email server-id in-game-name]
  (jdbc/execute! db
                 ["INSERT INTO UserAccount (Username, Password, Email, GameServerId, InGameName)
                   VALUES (?, ?, ?, ?, ?);" username (hashers/derive password) email server-id in-game-name]))

(defn update-user! [db user]
  (let [id (:id user)
        username (:username user)
        password (:password user)
        email (:email user)
        game-server-id (get-in user [:game-server :id])
        in-game-name (:in-game-name user)]
    (if (and (not (nil? id)) (not (= id 0)))
      (jdbc/execute! db
                     ["UPDATE UserAccount
                       SET Username = ?,
                       Password = ?,
                       Email = ?,
                       GameServerId = ?,
                       InGameName = ?
                       WHERE Id = ?;" username password email game-server-id in-game-name]))))