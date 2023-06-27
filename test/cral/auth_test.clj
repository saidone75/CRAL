(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]))

(defonce user "admin")
(defonce password "admin")

(deftest create-ticket
  (let [ticket-response (auth/create-ticket user password)]
    (is (= 201 (:status ticket-response)))
    (is (= "admin" (get-in ticket-response [:body :entry :user-id])))
    (is (not (nil? (get-in ticket-response [:body :entry :id]))))))

(deftest validate-ticket
  (let [ticket-response (auth/create-ticket user password)]
    (is (= 200 (:status (auth/validate-ticket (get-in ticket-response [:body :entry])))))))

(deftest delete-ticket
  (let [ticket-response (auth/create-ticket user password)]
    (is (= 204 (:status (auth/delete-ticket (get-in ticket-response [:body :entry])))))))