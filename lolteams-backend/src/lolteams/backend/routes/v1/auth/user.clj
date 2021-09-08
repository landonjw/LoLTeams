(ns lolteams.backend.routes.v1.auth.user
  (:require [buddy.hashers :as hashers]
            [next.jdbc :as jdbc]
            [lolteams.backend.database :refer [datasource]]
            [clojure.string :refer [lower-case]]))

(defn username-validates? [username]
  (let [username-length (count username)]
    (<= 3 username-length 16)))                             ; Username is between 3 and 16 characters

(defn password-validates? [password]
  (and (re-find #"[0-9]" password)                          ; Password has at least one digit
       (re-find #"[a-zA-Z]" password)                       ; Password has at least one alphabetical character
       (<= 8 (count password))))                            ; Password length is at least 8

(defn email-validates? [email]
  (re-find #"@" email))                                     ; Email contains an @

(defn create-user! [username password email]
  "Creates and persists a user given a username, password, and email.

  This function validates all parameters and will return a failure message if
  the user does not pass validation.

  The password will be hashed before being persisted in the database.

  **Example Usage**:
      #!clj
      (create-user! \"landonjw\" \"foobar123\" \"landonjwdev@gmail.com\")
      ; => {:success? true}
      (create-user! \"landonjw\" \"foobar123\" \"bademail\")
      ; => {:success? false :message \"Illegal email format\"}
  "
  (let [fail (fn [msg] {:success? false :message msg})
        succeed (fn [] {:success? true})]
    (cond
      (not (username-validates? username)) (fail "Username must be between 3 and 16 characters")
      (not (password-validates? password)) (fail "Password must be at least 8 characters and have one digit and one character")
      (not (email-validates? email)) (fail "Illegal email format")
      :else (let [encrypted-password (hashers/derive password)]
              (try
                (jdbc/execute! @datasource ["
                INSERT INTO UserAccount (Username, Password, Email)
                VALUES (?, ?, ?);
                " username encrypted-password email])
                (succeed)
                (catch Exception e
                  (fail (-> e .getCause .getMessage))))))))

(defn username->user! [username]
  (-> (jdbc/execute! @datasource ["
        SELECT *
        FROM UserAccount
        WHERE Username = ?;
        " username])
      (first)))

(defn authenticates? [username password]
  "Checks if a username and password can successfully be authenticated.

  **Example Usage**:
      #!clj
      (authenticates? \"landonjw\" \"foobar123\")
      ; => true
      (authenticates? \"landonjw\" \"badpass\")
      ; => false
  "
  (if (or (empty? username)
          (empty? password))
    false
    (let [user-record (username->user! username)]
      (->> user-record
           (:useraccount/password)
           (hashers/verify password)
           (:valid)))))