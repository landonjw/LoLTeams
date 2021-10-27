(ns lolteams.backend.models.game-server
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn get-all-servers [db]
  (jdbc/execute! db
                 ["SELECT * FROM GameServer;"]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn id->game-server [db id]
  (-> (jdbc/execute! db
                     ["SELECT * FROM GameServer WHERE Id = ?;" id]
                     {:builder-fn rs/as-unqualified-lower-maps})
      (first)))

(defn abbreviation->game-server [db abbreviation]
  (-> (jdbc/execute! db
                     ["SELECT * FROM GameServer WHERE Abbreviation = ?;" abbreviation]
                     {:builder-fn rs/as-unqualified-lower-maps})
      (first)))

(defn entity->dto [server]
  (dissoc server :id))