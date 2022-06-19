(ns lolteams.backend.handlers.v1.auth-test
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-seeder :refer [test-config]]
            [lolteams.backend.handlers.v1.auth :as auth-handler]
            [lolteams.backend.models.user-account :as user-model]
            [lolteams.backend.test-utils :refer [with-test-db create-test-user]]))

(deftest login-user-test
  (testing "valid user credentials produces 200 status code with auth token in body"
    (with-test-db
     (let [response
           ((auth-handler/login-user transaction (test-config))
            {:body-params {:username     "landonjw"
                           :password     "landonjw123"}})]
       (is (= (:status response) 200))
       (is (not (nil? (:body response)))))))

  (testing "valid username with incorrect password produces 401"
    (with-test-db
     (let [response
           ((auth-handler/login-user transaction (test-config))
            {:body-params {:username     "landonjw"
                           :password     "badpassword"}})]
       (is (= (:status response) 401)))))

  (testing "invalid request data produces 401"
    (testing "username is not a string"
      (with-test-db
         (let [response
               ((auth-handler/login-user transaction (test-config))
                {:body-params {:username     12345
                               :password     "badpassword"}})]
           (is (= (:status response) 400))
           (is (= (get-in response [:body :username]) "must be a string")))))

    (testing "no username"
      (with-test-db
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:password     "badpassword"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "this field is mandatory")))))

    (testing "password is not a string"
      (with-test-db
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:username     "landonjw"
                             :password     12345}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "must be a string")))))

    (testing "no password"
      (with-test-db
       (let [response
             ((auth-handler/login-user transaction (test-config))
              {:body-params {:username     "landonjw"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "this field is mandatory")))))))

(deftest register-user-test
  (testing "with valid user state produces 201 status code with auth token in body"
    (with-test-db
     (let [response
           ((auth-handler/register-user transaction (test-config))
            {:body-params {:username     "testaccount"
                           :password     "foobar123"
                           :email        "example@example.com"
                           :server       "NA"
                           :in-game-name "testaccount"}})]
       (is (= (:status response) 201))
       (is (not (nil? (:body response)))))))

  (testing "with invalid username produces 400 status with reason in body"
    (testing "taken username"
      (with-test-db
       (let [test-user (user-model/create-user! transaction (create-test-user))
             response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     (:username test-user)
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "already registered to another user")))))

    (testing "username with less than 3 characters"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "te"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "less than the minimum 3")))))

    (testing "username with more than 16 characters"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount1234567"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "longer than the maximum 16")))))

    (testing "username that isnt a string"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     123421
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "must be a string")))))

    (testing "no username"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :username]) "this field is mandatory"))))))

  (testing "with invalid password produces 400 status with reason in body"
    (testing "password that isnt a string"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     12345678
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "must be a string")))))

    (testing "password that is less than 8 characters"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "test1"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "less than the minimum 8")))))

    (testing "password that doesnt have a letter"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "12345678"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "must have at least one number and one letter")))))

    (testing "password that doesnt have a number"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpassword"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "must have at least one number and one letter")))))

    (testing "no password"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :password]) "this field is mandatory"))))))

  (testing "with invalid email produces 400 status with reason in body"
    (testing "email that isnt a string"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :email        12342343245
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :email]) "must be a string")))))

    (testing "taken email"
      (with-test-db
       (let [test-user (user-model/create-user! transaction (create-test-user))
             response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount2"
                             :password     "testpass123"
                             :email        (:email test-user)
                             :server       "NA"
                             :in-game-name "testaccount2"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :email]) "already registered to another user")))))

    (testing "invalid email (no @)"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :email        "bademail"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :email]) "not a valid email")))))

    (testing "no email"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "testpass123"
                             :server       "NA"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :email]) "this field is mandatory"))))))

  (testing "with invalid server produces 400 status with reason in body"
    (testing "no server in database with abbreviation"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "EXAMPLE"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :server]) "game server is not valid or unsupported")))))

    (testing "no server"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :server]) "this field is mandatory")))))

    (testing "server that isnt a string"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       1234
                             :in-game-name "testaccount"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :server]) "must be a string"))))))

  (testing "with invalid in-game name produces 400 status with reason in body"
    (testing "taken in-game name"
      (with-test-db
       (user-model/create-user! transaction (create-test-user))
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount2"
                             :password     "testpass123"
                             :email        "example2@example.com"
                             :server       "NA"
                             :in-game-name "testaccount1"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :in-game-name]) "already registered to another user")))))

    (testing "in-game name that is less than 3 characters"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "te"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :in-game-name]) "less than the minimum 3")))))

    (testing "in-game name that is more than 16 characters"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name "testaccount123456"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :in-game-name]) "longer than the maximum 16")))))

    (testing "in-game name that isnt a string"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"
                             :in-game-name 1234}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :in-game-name]) "must be a string")))))

    (testing "no in-game name"
      (with-test-db
       (let [response
             ((auth-handler/register-user transaction (test-config))
              {:body-params {:username     "testaccount"
                             :password     "foobar123"
                             :email        "example@example.com"
                             :server       "NA"}})]
         (is (= (:status response) 400))
         (is (= (get-in response [:body :in-game-name]) "this field is mandatory")))))))