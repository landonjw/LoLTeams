(ns lolteams.backend.routes.v1.servers.server-handler
  (:require [next.jdbc :as jdbc]
            [lolteams.backend.database :refer [datasource]]
            [ring.util.http-response :refer :all]))

(defn get-all-servers! []
  (jdbc/execute! @datasource ["SELECT * FROM GameServer;"]))

(defn name->server! [name]
  (jdbc/execute! @datasource ["SELECT * FROM GameServer WHERE Name = ?;" name]))

(defn servers-get [_]
  "Handler for the server GET endpoint.

  Parameters:
    N/A.

  Returns:
    A dictionary of league of legends servers and their corresponding shortened names.
    For example:
      \"servers\" {
        \"North America\": \"NA\",
        \"Europe Nordic & East\": \"EUNE\",
        \"Europe West\": \"EUW\",
        \"Korea\": \"KR\"
      }
  "
  (ok (->> (get-all-servers!)
           (map (fn [server]
                  {:name (:gameserver/name server)
                   :abbreviation (:gameserver/abbreviation server)}))
           (into []))))