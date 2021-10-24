(ns lolteams.backend.models.game-server
  (:require [next.jdbc :as jdbc]))

(defn get-all-servers [db]
  (jdbc/execute! db ["SELECT * FROM GameServer;"]))

(defn id->game-server [db id]
  (-> (jdbc/execute! db ["SELECT * FROM GameServer WHERE Id = ?;" id])
      (first)))

(defn abbreviation->game-server [db abbreviation]
  (-> (jdbc/execute! db ["SELECT * FROM GameServer WHERE Abbreviation = ?;" abbreviation])
      (first)))

(defn game-server->dto [server]
  {:name (:gameserver/name server)
   :abbreviation (:gameserver/abbreviation server)})