(ns lolteams.backend.handlers.v1.auth_test
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-seeder :refer [test-config test-database]]
            [lolteams.backend.handlers.v1.auth :as auth-handler]
            [lolteams.backend.models.user-account :as user-model]
            [next.jdbc :as jdbc]))

(deftest login-user-test
  (testing "valid user credentials produces 200 status code with auth token in body"
    (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
     (let [response
           ((auth-handler/login-user transaction (test-config))
            {:body-params {:username     "landonjw"
                           :password     "landonjw123"}})]
       (is (= 200 (:status response)))
       (is (not (nil? (:body response)))))))

  (testing "valid username with incorrect password produces 401"
    (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
     (let [response
           ((auth-handler/login-user transaction (test-config))
            {:body-params {:username     "landonjw"
                           :password     "badpassword"}})]
       (is (= 401 (:status response))))))

  (testing "invalid request data produces 401"
    (testing "username is not a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
         (let [response
               ((auth-handler/login-user transaction (test-config))
                {:body-params {:username     12345
                               :password     "badpassword"}})]
           (is (= 400 (:status response)))
           (is (= "must be a string" (get-in response [:body :username]))))))

    (testing "no username"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:password     "badpassword"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :username]))))))

    (testing "password is not a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:username     "landonjw"
                             :password     12345}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :password]))))))

    (testing "no password"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:username     "landonjw"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :password]))))))))

(deftest register-user-test
  (testing "with valid user state produces 201 status code with auth token in body"
    (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
     (let [response
           ((auth-handler/register-user transaction (test-config))
            {:body-params {:username     "testaccount"
                           :password     "foobar123"
                           :email        "example@example.com"
                           :server       "NA"
                           :in-game-name "testaccount"}})]
       (is (= 201 (:status response)))
       (is (not (nil? (:body response)))))))

  (testing "with invalid username produces 400 status with reason in body"
    (testing "taken username"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (user-model/create-user transaction "testaccount" "testpass123" "example1@example.com" 1 "testaccount1")
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "already registered to another user" (get-in response [:body :username]))))))

    (testing "username with less than 3 characters"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "te"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "less than the minimum 3" (get-in response [:body :username]))))))

    (testing "username with more than 16 characters"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount1234567"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "longer than the maximum 16" (get-in response [:body :username]))))))

    (testing "username that isnt a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     123421
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :username]))))))

    (testing "no username"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :username])))))))

  (testing "with invalid password produces 400 status with reason in body"
    (testing "password that isnt a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     12345678
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :password]))))))

    (testing "password that is less than 8 characters"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "test1"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "less than the minimum 8" (get-in response [:body :password]))))))

    (testing "password that doesnt have a letter"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "12345678"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "must have at least one number and one letter" (get-in response [:body :password]))))))

    (testing "password that doesnt have a number"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpassword"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "must have at least one number and one letter" (get-in response [:body :password]))))))

    (testing "no password"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :password])))))))

  (testing "with invalid email produces 400 status with reason in body"
    (testing "email that isnt a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :email        12342343245
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :email]))))))

    (testing "taken email"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (user-model/create-user transaction "testaccount1" "testpass123" "example@example.com" 1 "testaccount1")
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount2"
                             :password     "testpass123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= 400 (:status response)))
         (is (= "already registered to another user" (get-in response [:body :email]))))))

    (testing "invalid email (no @)"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :email        "bademail"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "not a valid email" (get-in response [:body :email]))))))

    (testing "no email"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :email])))))))

  (testing "with invalid server produces 400 status with reason in body"
    (testing "no server in database with abbreviation"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "EXAMPLE"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "game server is not valid or unsupported" (get-in response [:body :server]))))))

    (testing "no server"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :server]))))))

    (testing "server that isnt a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       1234
                             :in-game-name "testaccount"}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :server])))))))

  (testing "with invalid in-game name produces 400 status with reason in body"
    (testing "taken in-game name"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (user-model/create-user transaction "testaccount1" "testpass123" "example@example.com" 1 "testaccount1")
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount2"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount1"}})]
         (is (= 400 (:status response)))
         (is (= "already registered to another user" (get-in response [:body :in-game-name]))))))

    (testing "in-game name that is less than 3 characters"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "te"}})]
         (is (= 400 (:status response)))
         (is (= "less than the minimum 3" (get-in response [:body :in-game-name]))))))

    (testing "in-game name that is more than 16 characters"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount123456"}})]
         (is (= 400 (:status response)))
         (is (= "longer than the maximum 16" (get-in response [:body :in-game-name]))))))

    (testing "in-game name that isnt a string"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name 1234}})]
         (is (= 400 (:status response)))
         (is (= "must be a string" (get-in response [:body :in-game-name]))))))

    (testing "no in-game name"
      (jdbc/with-transaction [transaction (test-database) {:rollback-only true}]
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"}})]
         (is (= 400 (:status response)))
         (is (= "this field is mandatory" (get-in response [:body :in-game-name]))))))))