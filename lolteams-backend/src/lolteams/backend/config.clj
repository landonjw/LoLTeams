(ns lolteams.backend.config
  (:require [buddy.core.keys :as keys]
            [clojure.java.io :as io]))

(defn read-public-key! [public-key-filename]
  (keys/public-key (io/resource (str "keys/" public-key-filename))))

(defn read-private-key! [private-key-filename passphrase]
  (keys/private-key (io/resource (str "keys/" private-key-filename)) passphrase))

(defn create-config! []
  (println "Reading configuration...")
  (let [config (clojure.edn/read-string (slurp "config.edn"))
        auth-config (:auth config)]
    (-> config
        (assoc-in [:database-properties :dbtype] "postgres")
        (assoc-in [:auth :public-key] (read-public-key! (:public-key-filename auth-config)))
        (assoc-in [:auth :private-key] (read-private-key! (:private-key-filename auth-config) (:passphrase auth-config))))))