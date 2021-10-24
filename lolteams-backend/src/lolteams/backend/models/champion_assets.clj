(ns lolteams.backend.models.champion-assets)

(defn- equals-ignore-case? [string-1 string-2]
  "Evaluates if two strings are equal, without accounting for case sensitivity.

  **Example Usage**:
      #!clj
      (equals-ignore-case? \"foo\" \"foo\")
      ; => true
      (equals-ignore-case? \"foo\" \"FOO\")
      ; => true
      (equals-ignore-case? \"foo\" \"bar\")
      ; => false
  "
  (if (and string-1 string-2) ; Check neither string is nil
    (= (clojure.string/lower-case string-1) (clojure.string/lower-case string-2))))

(defn- champion-name->champion-uri-symbol [data-dragon champion-name]
  "Gets the URI symbol key from a supplied case-insensitive champion name, and the data dragon state.

  **Example Usage**:
      #!clj
      (get-champion-uri-key @data-dragon \"gangplank\")
      ; => :Gangplank
  "
  (let [champion-uri-names (:champion-uri-names data-dragon)]
    (->> champion-uri-names
         (filter #(equals-ignore-case? champion-name (second %)))
         (first)
         (first))))

(defn- champion-name->champion-uri-name [data-dragon champion-name ]
  "Gets the corresponding URI name from a supplied case-insensitive champion name, and the data dragon state.
  This is necessary because a champion name is not necessarily the same on the asset endpoints.
  An example of this is Wukong, whose URI name is MonkeyKing.

  **Example Usage**:
      #!clj
      (get-champion-uri-name @data-dragon \"wukong\")
      ; => \"MonkeyKing\"
  "
  (let [champion-uri-key (champion-name->champion-uri-symbol data-dragon champion-name)]
    (if champion-uri-key
      (name champion-uri-key))))

(defn champion-name->portrait-uri [data-dragon champion-name]
  "Gets a URI to a champion portrait image from a supplied case-insensitive champion name, and the data dragon state.

  **Example Usage**:
      #!clj
      (champion-portrait @data-dragon \"gangplank\")
      ; => http://ddragon.leagueoflegends.com/cdn/9.3.1/img/champion/Gangplank.png
  "
  (let [champion-uri-name (champion-name->champion-uri-name data-dragon champion-name)]
    (if champion-uri-name
      (str (:cdn-uri data-dragon) "img/champion/" champion-uri-name ".png"))))