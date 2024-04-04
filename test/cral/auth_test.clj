(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]))

(defonce user "admin")
(defonce password "admin")


(deftest auth-test
  ;; create ticket
  (let [create-ticket-response (auth/create-ticket user password)]
    (is (= (:status create-ticket-response) 201))
    ;; validate ticket
    (is (= (:status (auth/validate-ticket (get-in create-ticket-response [:body :entry]))) 200))
    ;; delete ticket
    (is (= (:status (auth/delete-ticket (get-in create-ticket-response [:body :entry]))) 204))))