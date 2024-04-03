(ns cral.utils-test
  (:require [clojure.test :refer :all]
            [cral.utils.utils :as utils]))

(deftest kebab-case
  (is (= (utils/kebab-case "kebabCase")
         "kebab-case")))

(deftest kebab-keywordize-keys
  (is (= (utils/kebab-keywordize-keys {"levelOne" {"levelTwo" "value"}})
         {:level-one {:level-two "value"}})))

(deftest camel-case-keywordize-keys
  (is (= (utils/camel-case-keywordize-keys {"level-one" {"level-two" "value"}})
         {:levelOne {:levelTwo "value"}})))

(deftest camel-case-stringify-keys
  (is (= (utils/camel-case-stringify-keys {"level-one" {"level-two" "value"}})
         {"levelOne" {"levelTwo" "value"}})))