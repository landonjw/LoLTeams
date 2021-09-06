(defproject lolteams-backend "0.1.0-SNAPSHOT"
  :description "Backend service(s) for LoLTeams"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [metosin/reitit "0.5.15"]
                 [http-kit "2.4.0"]
                 [org.postgresql/postgresql "42.2.2"]
                 [com.github.seancorfield/next.jdbc "1.2.689"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/core.cache "1.0.217"]
                 [jarohen/chime "0.3.3"]]
  :main ^:skip-aot lolteams.backend.core
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler lolteams.backend.core/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
