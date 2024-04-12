(ns cral.people-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.people :as people]))

(def user "admin")
(def pass "admin")

;; TODO
(deftest create-person-test)

;; TODO
(deftest list-people-test)

;; TODO
(deftest get-person-test)

;; TODO
(deftest update-person-test)

(deftest people-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])]
    ;; list people
    (let [list-people-response (people/list-people ticket {})]
      (is (= (:status list-people-response) 200))
      (is (some #(= user %) (->> list-people-response
                                 (#(get-in % [:body :list :entries]))
                                 (map #(get-in % [:entry :id]))))))
    ;; get person
    (let [get-person-response (people/get-person ticket user)]
      (is (= (:status get-person-response) 200))
      (is (true? (get-in get-person-response [:body :entry :capabilities :is-admin]))))))