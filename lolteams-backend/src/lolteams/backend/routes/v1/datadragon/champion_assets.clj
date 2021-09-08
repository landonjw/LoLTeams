(ns lolteams.backend.routes.v1.datadragon.champion-assets
  (:require [lolteams.backend.routes.v1.datadragon.core :refer [cdn-uri]]))

(defn equals-ignore-case? [string-1 string-2]
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

(defn get-champion-uri-key [champion-name data-dragon]
  "Gets the URI symbol key from a supplied case-insensitive champion name, and the data dragon state.

  **Example Usage**:
      #!clj
      (get-champion-uri-key \"gangplank\" @data-dragon/data-dragon-state)
      ; => :Gangplank
  "
  (let [champion-uri-names (:champion-uri-names data-dragon)]
    (->> champion-uri-names
         (filter #(equals-ignore-case? champion-name (second %)))
         (first)
         (first))))

(defn get-champion-uri-name [champion-name data-dragon]
  "Gets the corresponding URI name from a supplied case-insensitive champion name, and the data dragon state.
  This is necessary because a champion name is not necessarily the same on the asset endpoints.
  An example of this is Wukong, whose URI name is MonkeyKing.

  **Example Usage**:
      #!clj
      (get-champion-uri-name \"gangplank\" @data-dragon/data-dragon-state)
      ; => \"MonkeyKing\"
  "
  (let [champion-uri-key (get-champion-uri-key champion-name data-dragon)]
    (if champion-uri-key
      (name champion-uri-key))))

(defn champion-portrait-uri [champion-name data-dragon]
  "Gets a URI to a champion portrait image from a supplied case-insensitive champion name, and the data dragon state.

  **Example Usage**:
      #!clj
      (champion-portrait \"gangplank\" @data-dragon/data-dragon-state)
      ; => http://ddragon.leagueoflegends.com/cdn/9.3.1/img/champion/Gangplank.png
  "
  (let [champion-uri-name (get-champion-uri-name champion-name data-dragon)]
    (if champion-uri-name
      (str (cdn-uri (:version data-dragon)) "img/champion/" champion-uri-name ".png"))))