(ns cral.probe-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.core.probes :as probes]
            [cral.alfresco.model.core]))

(deftest get-probes
  (is (= 200 (:status (probes/probes "-ready-"))))
  (is (= 200 (:status (probes/probes "-live-")))))