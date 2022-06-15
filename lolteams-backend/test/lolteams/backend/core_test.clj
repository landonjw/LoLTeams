(ns lolteams.backend.core-test
  (:require
    [lolteams.backend.handlers.v1.auth_test]
    [lolteams.backend.handlers.v1.champion_test]
    [lolteams.backend.models.v1.champion_assets_test]
    [lolteams.backend.test-seeder :as test-seeder]))

(do
  (test-seeder/init-test-env)
  (clojure.test/run-tests
    'lolteams.backend.handlers.v1.auth_test
    'lolteams.backend.handlers.v1.champion_test
    'lolteams.backend.models.v1.champion_assets_test))