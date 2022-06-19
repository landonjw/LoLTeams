(ns lolteams.backend.core-test
  (:require
    [lolteams.backend.handlers.v1.auth-test]
    [lolteams.backend.handlers.v1.champion-test]
    [lolteams.backend.models.champion-assets-test]
    [lolteams.backend.models.rbac.role-test]
    [lolteams.backend.test-seeder :as test-seeder]))

(do
  (test-seeder/init-test-env)
  (clojure.test/run-tests
    'lolteams.backend.handlers.v1.auth-test
    'lolteams.backend.handlers.v1.champion-test
    'lolteams.backend.models.champion-assets-test
    'lolteams.backend.models.rbac.role-test))