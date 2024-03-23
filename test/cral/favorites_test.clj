(ns cral.favorites_test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.favorites :as favorites]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest list-favorites
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    ;; list favorites
    (is (= 200 (:status (favorites/list-favorites ticket user))))))