(ns lolteams.backend.handlers.v1.auth
  (:require [struct.core :as st]
            [ring.util.http-response :refer :all]
            [lolteams.backend.models.user-account :as user-model]
            [lolteams.backend.models.game-server :as server-model]
            [lolteams.backend.auth.authenticator :as authenticator]))

(def login-schema
  "
  Schema for the login request model.

  Rules:
    Username is required and is a string.
    Password is required and is a string.
  "
  {:username [st/required st/string]
   :password [st/required st/string]})

(defn login-user [db config]
  "
  Creates the endpoint for logging into a user account.

  Endpoint Type: POST

  Request Model:
    username => The username of the user attempting to log in.
    password => The password of the user attempting to log in.

  Response(s):
    200:
      Request has succeeded.
      Body will contain authentication token to be used in endpoints that require authorization.
    400:
      The request contains a bad data model.
      Body will include any errors caught in data model.
    401:
      Username and password match do not succeed in authenticating a registered user.
  "
  (fn [{params :body-params}]
    (let [errors (first (st/validate params login-schema))
          username (:username params)
          password (:password params)]
      (if errors
        (bad-request errors)
        (if (authenticator/authenticates? db username password)
          (ok (authenticator/create-auth-token (get-in config [:auth :private-key]) username))
          (unauthorized "Authentication error"))))))

(defn unique-username-validator [db]
  "
  Validates that a user does not currently exist in the database with the given username.
  "
  {:message "already registered to another user"
   :validate #(nil? (user-model/username->user-account db %))})

(def good-password-validator
  "
  Validates that a password contains at least one number and one letter.
  "
  {:message  "must have at least one number and one letter"
   :validate (fn [password]
               (and
                 (re-find #"[0-9]" password)
                 (re-find #"[a-zA-Z]" password)))})

(def good-email-validator
  "
  Validates that an email is valid. This does a simple check to see if it contains an @.
  Better alternative down the road would be to have the user verify their email upon registration.
  "
  {:message  "not a valid email"
   :validate (fn [email]
               (re-find #"@" email))})

(defn unique-email-validator [db]
  "
  Validates that a user does not currently exist in the database with the given email.
  "
  {:message  "already registered to another user"
   :validate #(nil? (user-model/email->user-account db %))})

(defn valid-game-server-validator [db]
  "
  Validates that a game server is contained within our database.
  "
  {:message  "game server is not valid or unsupported"
   :validate #(not (nil? (server-model/abbreviation->game-server db %)))})

(defn unique-in-game-name-validator [db]
  "
  Validates that a user does not currently exist in the database with the given in-game name.
  "
  {:message  "already registered to another user"
   :validate #(nil? (user-model/in-game-name->user-account db %))})

(defn register-schema [db]
  "
  Schema for the register request model.

  Rules:
    Username is a string, between 3 to 16 characters and is unique.
    Password is a string, at least 8 characters and has at least one number and one letter.
    Email is a string and valid (has an @).
    Server is a string and exists in our database.
    In-game name is a string and is unique.
  "
  {:username     [st/required st/string [st/min-count 3] [st/max-count 16] (unique-username-validator db)]
   :password     [st/required st/string [st/min-count 8] good-password-validator]
   :email        [st/required st/string good-email-validator (unique-email-validator db)]
   :server       [st/required st/string (valid-game-server-validator db)]
   :in-game-name [st/required st/string [st/min-count 3] [st/max-count 16] (unique-in-game-name-validator db)]})

(defn register-user [db config]
  "
  Creates the endpoint for registering a new user account.

  Endpoint Type: POST

  Request Model:
    username => The username of the new account.
    password => The password of the new account.
    email => The email of the user.
    server => The League of Legends server the user plays on.
    in-game-name => The username of the player's account on League of Legends.

  Response Model:
    201:
      Request has succeeded and user account has been created.
      Body will contain authentication token to be used in endpoints that require authorization.
    400:
      The request contains a bad data model.
      Body will include a map with keys of invalid request parameters to values of details on errors caught in data model.
  "
  (fn [{params :body-params}]
    (let [errors (first (st/validate params (register-schema db)))]
      (if errors
        (bad-request errors)
        (let [{:keys [username password email server in-game-name]} params
              server-id (-> (server-model/abbreviation->game-server db server)
                            (:id))]
          (user-model/create-user db username password email server-id in-game-name)
          (created "/" (authenticator/create-auth-token (get-in config [:auth :private-key]) username)))))))