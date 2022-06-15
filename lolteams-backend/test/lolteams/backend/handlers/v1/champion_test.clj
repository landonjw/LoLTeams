(ns lolteams.backend.handlers.v1.champion_test
  (:require [clojure.test :refer :all]
            [lolteams.backend.test-seeder :refer [test-data-dragon]]
            [lolteams.backend.handlers.v1.champion :as champion-handler]))

(deftest get-portrait-uri-test
  (testing "valid champion returns 200 with body of correct uri"
    (let [response
          ((champion-handler/get-portrait-uri (test-data-dragon))
           {:params {"champion" "Gangplank"}})]
      (is (= 200 (:status response)))
      (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/Gangplank.png" (:body response)))))

  (testing "valid champion with spaces returns 200 with body of correct uri"
    (let [response
          ((champion-handler/get-portrait-uri (test-data-dragon))
           {:params {"champion" "Aurelion Sol"}})]
      (is (= 200 (:status response)))
      (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/AurelionSol.png" (:body response)))))

  (testing "valid champion with special name returns 200 with body of correct uri"
    (let [response
          ((champion-handler/get-portrait-uri (test-data-dragon))
           {:params {"champion" "Wukong"}})]
      (is (= 200 (:status response)))
      (is (= "http://ddragon.leagueoflegends.com/cdn/11.21.1/img/champion/MonkeyKing.png" (:body response)))))

  (testing "invalid champion returns 404"
    (let [response
          ((champion-handler/get-portrait-uri (test-data-dragon))
           {:params {"champion" "BadChampion"}})]
      (is (= 404 (:status response))))))