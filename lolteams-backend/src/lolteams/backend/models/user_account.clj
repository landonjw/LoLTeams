(ns lolteams.backend.models.user-account
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [buddy.hashers :as hashers]
            [lolteams.backend.models.game-server :as server-model]))

(defn record->entity [record]
  (if record
    {:id           (:useraccountid record)
     :username     (:username record)
     :password     (:password record)
     :email        (:email record)
     :in-game-name (:ingamename record)
     :game-server  {:id           (:gameserverid record)
                    :name         (:gameservername record)
                    :abbreviation (:gameserverabbreviation record)}}))

(defn username->user-account [db username]
  (-> (jdbc/execute! db
                     ["SELECT UserAccount.Id AS UserAccountId,
                       UserAccount.Username,
                       UserAccount.Password,
                       UserAccount.Email,
                       UserAccount.InGameName,
                       GameServer.Id AS GameServerId,
                       GameServer.Name AS GameServerName,
                       GameServer.Abbreviation AS GameServerAbbreviation
                       FROM UserAccount
                       JOIN GameServer ON UserAccount.GameServerId = GameServer.Id
                       WHERE UserAccount.Username = ?;" username]
                     {:builder-fn rs/as-unqualified-lower-maps})
      (first)
      (record->entity)))

(defn email->user-account [db email]
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
                       WHERE UserAccount.Email = ?;" email]
                     {:builder-fn rs/as-unqualified-lower-maps})
      (first)
      (record->entity)))

(defn in-game-name->user-account [db in-game-name]
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
                       WHERE UserAccount.InGameName = ?;" in-game-name]
                     {:builder-fn rs/as-unqualified-lower-maps})
      (first)
      (record->entity)))

(defn create-user [db username password email server-id in-game-name]
  (jdbc/execute! db
                 ["INSERT INTO UserAccount (Username, Password, Email, GameServerId, InGameName)
                   VALUES (?, ?, ?, ?, ?);" username (hashers/derive password) email server-id in-game-name]))

(defn update-user [db user]
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

(defn entity->dto [user-account]
  (-> user-account
      (dissoc :id)
      (dissoc :password)
      (assoc :game-server (server-model/entity->dto (:game-server user-account)))))