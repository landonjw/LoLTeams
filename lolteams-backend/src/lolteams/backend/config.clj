(ns lolteams.backend.config)

(def config (clojure.edn/read-string (slurp "config.edn")))