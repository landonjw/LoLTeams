(ns lolteams.backend.models.champion-assets-test
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-seeder :refer [test-data-dragon]]
            [lolteams.backend.models.champion-assets :as champion-assets]))

(deftest champion-name->portrait-uri-test
  (testing "valid champion returns correct uri"
    (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/Gangplank.png"
           (champion-assets/champion-name->portrait-uri (test-data-dragon)  "Gangplank"))))

  (testing "valid champion with spaces returns correct uri"
    (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/AurelionSol.png"
           (champion-assets/champion-name->portrait-uri (test-data-dragon) "Aurelion Sol"))))

  (testing "valid champion with special name returns correct uri"
    (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/MonkeyKing.png"
           (champion-assets/champion-name->portrait-uri (test-data-dragon) "Wukong")))))