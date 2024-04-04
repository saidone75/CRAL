(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]))

(defonce user "admin")
(defonce password "admin")

(deftest create-ticket-test
  ;; create ticket
  (let [create-ticket-response (auth/create-ticket user password)]
    (is (= (:status create-ticket-response) 201))))

(deftest validate-ticket-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    ;; validate ticket
    (is (= (:status (auth/validate-ticket ticket)) 200))))

(deftest delete-ticket-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    ;; delete ticket
    (is (= (:status (auth/delete-ticket ticket)) 204))))