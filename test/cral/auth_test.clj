(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]))

(defonce user "admin")
(defonce password "admin")

(deftest create-ticket
  (let [response (auth/create-ticket user password)]
    (is (= 201 (:status response)))
    (is (= "admin" (get-in response [:body :entry :user-id])))
    (is (not (nil? (get-in response [:body :entry :id]))))))

(deftest validate-ticket
  (let [response (auth/create-ticket user password)]
    (is (= 200 (:status (auth/validate-ticket (get-in response [:body :entry])))))))

(deftest delete-ticket
  (let [response (auth/create-ticket user password)]
    (is (= 204 (:status (auth/delete-ticket (get-in response [:body :entry])))))))