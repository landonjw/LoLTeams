(ns lolteams.backend.config
  (:require [buddy.core.keys :as keys]
            [clojure.java.io :as io]))

(defn read-public-key! [public-key-filename]
  (keys/public-key (io/resource (str "keys/" public-key-filename))))

(defn read-private-key! [private-key-filename passphrase]
  (keys/private-key (io/resource (str "keys/" private-key-filename)) passphrase))

(defn load-extra-config-info [config]
  (let [auth-settings (:auth config)]
    (-> config
        (assoc-in [:database-properties :dbtype] "postgres")
        (assoc-in [:auth :public-key] (read-public-key! (:public-key-filename auth-settings)))
        (assoc-in [:auth :private-key] (read-private-key! (:private-key-filename auth-settings) (:passphrase auth-settings))))))

(defn create-config! []
  (println "Reading configuration...")
  (-> (slurp "config.edn")
      (clojure.edn/read-string)
      (load-extra-config-info)))