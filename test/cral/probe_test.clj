(ns cral.probe-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.core.probes :as probes]
            [cral.alfresco.model.core]))

(deftest get-probes
  (is (= (:status (probes/probes "-ready-")) 200))
  (is (= (:status (probes/probes "-live-")) 200)))