(ns lolteams.backend.models.rbac.role-test
  (:require [clojure.test :refer :all]
            [lolteams.backend.models.rbac.role :as sut]
            [lolteams.backend.models.rbac.permission :refer [get-permissions-for-role]]
            [lolteams.backend.test-utils :refer [with-test-db]]))

(deftest record->lite-entity-test
  (testing "returns valid lite-entity if given a valid record"
    (let [test-record {:rbac_role/id 1 :rbac_role/name "foo" :rbac_role/comments "bar"}
          expected {:id 1 :name "foo" :comments "bar"}]
      (is (= (sut/record->lite-entity test-record) expected))))

  (testing "returns nil if given given nil record"
    (is (= nil (sut/record->lite-entity nil))))

  (testing "returns nil comment if record has nil comment"
    (let [test-record {:rbac_role/id 1 :rbac_role/name "foo" :rbac_role/comments nil}
          expected {:id 1 :name "foo" :comments nil}]
      (is (= (sut/record->lite-entity test-record) expected)))))

(deftest lite-entity->entity-test
  (testing "populates appropriate permissions in entity"
    (let [test-lite-entity {:id 1 :name "foo" :comments "bar"}
          permissions [{:id 1 :name "ExamplePermission1" :comments nil} {:id 2 :name "ExamplePermission2" :comments "foo"}]
          expected (conj test-lite-entity {:permissions permissions})]
      (with-redefs [get-permissions-for-role (fn [_ _] permissions)]
        (is (= (sut/lite-entity->entity nil test-lite-entity) expected)))))

  (testing "populates empty vector for permissions in entity if there are no permissions for user"
    (let [test-lite-entity {:id 1 :name "foo" :comments "bar"}
          permissions []
          expected (conj test-lite-entity {:permissions permissions})]
      (with-redefs [get-permissions-for-role (fn [_ _] permissions)]
        (is (= (sut/lite-entity->entity nil test-lite-entity) expected)))))

  (testing "returns nil if given a nil lite-entity"
    (is (= nil (sut/lite-entity->entity nil nil)))))

(deftest record->entity-test
  (testing "returns valid entity if given a valid record"
    (let [test-record {:rbac_role/id 1 :rbac_role/name "foo" :rbac_role/comments "bar"}
          permissions [{:id 1 :name "ExamplePermission1" :comments nil} {:id 2 :name "ExamplePermission2" :comments "foo"}]
          expected (conj {:id 1 :name "foo" :comments "bar"} {:permissions permissions})]
      (is (= (sut/record->entity test-record permissions) expected))))

  (testing "returns nil if given a nil record"
    (is (= nil (sut/record->entity nil nil)))))

(deftest get-by-id-test
  (testing "returns entity for existing role"
    (with-test-db
      (let [permissions [{:id 1 :name "ExamplePermission1" :comments nil} {:id 2 :name "ExamplePermission2" :comments "foo"}]
            test-role (conj {:id 0 :name "foo" :comments "bar"} {:permissions permissions})
            expected (sut/create-role! transaction test-role)]
        (with-redefs [get-permissions-for-role (fn [_ _] permissions)]
          (is (= expected (sut/get-by-id transaction (:id expected)))))))))