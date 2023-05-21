(ns cral.utils-test
  (:require [clojure.test :refer :all]
            [cral.utils.utils :as utils]))

(deftest kebab-case
  (is (= "kebab-case") (utils/kebab-case "kebabCase")))

(deftest kebab-keywordize-keys
  (is (=
        {:level-one {:level-two "value"}}
        (utils/kebab-keywordize-keys {"levelOne" {"levelTwo" "value"}}))))

(deftest camel-case-keywordize-keys
  (is (=
        {:levelOne {:levelTwo "value"}}
        (utils/camel-case-keywordize-keys {"level-one" {"level-two" "value"}}))))

(deftest camel-case-stringify-keys
  (is (=
        {"levelOne" {"levelTwo" "value"}}
        (utils/camel-case-stringify-keys {"level-one" {"level-two" "value"}}))))