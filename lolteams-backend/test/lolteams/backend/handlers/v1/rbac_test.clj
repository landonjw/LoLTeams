(ns lolteams.backend.handlers.v1.rbac-test
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-utils :refer [with-test-db create-test-user]]
            [lolteams.backend.models.rbac.permission :as permission-model]
            [lolteams.backend.models.user-account :as account-model]
            [lolteams.backend.handlers.v1.rbac :as sut]))

(deftest get-permissions-for-user-test
  (testing "existing username returns 200 with body of appropriate permissions for user"
    (with-test-db
      (let [test-user (account-model/create-user! transaction (create-test-user))
            expected-permissions [{:id 1 :name "testperm1" :comments nil} {:id 2 :name "testperm2" :comments nil}]]
        (with-redefs [permission-model/get-permissions-for-user (fn [_ _] expected-permissions)] ; TODO: This should actually hit DB once other models are created to set up test...
          (let [response ((sut/get-permissions-for-user transaction) {:identity true :params {"username" (:username test-user)}})]
            (is (= (:status response) 200))
            (is (= (:body response) expected-permissions)))))))

  (testing "returns 400 when username does not correlate to existing user"
    (with-test-db
      (let [response ((sut/get-permissions-for-user transaction) {:identity true :params {"username" "nonexistent_username"}})]
        (is (= (:status response) 400))
        (is (= (get-in response [:body :username] "no user exists with this username"))))))

  (testing "returns 400 when no username is supplied at all"
    (with-test-db
      (let [response ((sut/get-permissions-for-user transaction) {:identity true :params {}})]
        (is (= (:status response) 400)
            (= (get-in response [:body :username] "this field is mandatory")))))))