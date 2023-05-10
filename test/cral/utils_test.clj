(ns cral.utils-test
  (:require [clojure.test :refer :all]
            [cral.utils.utils :as utils]))

(deftest camel-to-kebab
  (is (= "kebab-case") (utils/kebab-case "kebabCase")))

(deftest kebab-keywordize-keys
  (is (=
        {:level-one {:level-two "value"}}
        (utils/kebab-keywordize-keys {"levelOne" {"levelTwo" "value"}}))))