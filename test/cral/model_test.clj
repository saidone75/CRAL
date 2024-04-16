(ns cral.model-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model :as model]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest list-aspects-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        list-aspects-response (model/list-aspects ticket)]
    ;; list aspects
    (is (= (:status list-aspects-response) 200))))