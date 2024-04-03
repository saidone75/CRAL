(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]))

(defonce user "admin")
(defonce password "admin")

(deftest create-ticket
  (let [response (auth/create-ticket user password)]
    (is (= (:status response) 200))
    (is (= (get-in response [:body :entry :user-id]) "admin"))
    (is (not (nil? (get-in response [:body :entry :id]))))))

(deftest validate-ticket
  (let [response (auth/create-ticket user password)]
    (is (= (:status (auth/validate-ticket (get-in response [:body :entry]))) 200))))

(deftest delete-ticket
  (let [response (auth/create-ticket user password)]
    (is (= (:status (auth/delete-ticket (get-in response [:body :entry]))) 204))))