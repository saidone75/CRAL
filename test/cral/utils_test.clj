;  CRAL
;  Copyright (C) 2023-2024 Saidone
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

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