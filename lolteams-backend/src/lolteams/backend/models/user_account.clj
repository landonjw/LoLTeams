(ns lolteams.backend.models.user-account
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defn username->user-account [db username]
  (-> (jdbc/execute! db ["SELECT * FROM UserAccount WHERE Username = ?;" username])
      (first)))

(defn email->user-account [db email]
  (-> (jdbc/execute! db ["SELECT * FROM UserAccount WHERE Email = ?;" email])
      (first)))

(defn in-game-name->user-account [db in-game-name]
  (-> (jdbc/execute! db ["SELECT * FROM UserAccount WHERE InGameName = ?;" in-game-name])
      (first)))

(defn create-user [db username password email server-id in-game-name]
  (jdbc/execute! db ["
  INSERT INTO UserAccount (Username, Password, Email, GameServerId, InGameName)
  VALUES (?, ?, ?, ?, ?);
  " username (hashers/derive password) email server-id in-game-name]))

(defn user->dto [user-account]
  {:username (:useraccount/username user-account)
   :in-game-name (:useraccount/in-game-name user-account)
   :server (:useraccount/server user-account)})