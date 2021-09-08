(ns lolteams.backend.routes.v1.datadragon.champion-handler
  (:require [lolteams.backend.routes.v1.datadragon.champion-assets :as champ-assets]
            [lolteams.backend.routes.v1.datadragon.core :refer [data-dragon-state]]))

(defn portrait-get [{params :params}]
  "Handler for champion portrait GET endpoint.

  Parameters:
    champion_name => The name of the champion to get portrait for, case insensitive.

  Returns:
    URI to the champion portrait image, or 404 if no resource available for name.
  "
  (let [champion-name (get params "champion")
        portrait-uri (champ-assets/champion-portrait-uri champion-name @data-dragon-state)]
    (if portrait-uri
      {:status 200
       :body   (champ-assets/champion-portrait-uri champion-name @data-dragon-state)}
      {:status 404
       :body   "Resource could not be found"})))