(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.model]
            [cral.alfresco.auth :as auth]))

(deftest create-ticket
  (let [ticket-response (auth/create-ticket "admin" "admin")]
    (is (= 201 (:status ticket-response)))
    (is (= "admin" (get-in ticket-response [:body :entry :user-id])))
    (is (not (nil? (get-in ticket-response [:body :entry :id]))))))

(deftest validate-ticket
  (let [ticket-response (auth/create-ticket "admin" "admin")]
    (is (= 200 (:status (auth/validate-ticket (get-in ticket-response [:body :entry])))))))

(deftest delete-ticket
  (let [ticket-response (auth/create-ticket "admin" "admin")]
    (is (= 204 (:status (auth/delete-ticket (get-in ticket-response [:body :entry])))))))