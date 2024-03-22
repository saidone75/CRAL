(ns cral.activities-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.activities :as activities]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest list-activities
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    ;; list activities
    (is (= 200 (:status (activities/list-activities ticket user))))))