(ns cral.people-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.people :as people]))

(def user "admin")
(def pass "admin")

(deftest list-people
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        list-people-response (people/list-people ticket {})]
    (is (= 200 (:status list-people-response)))
    (is (some #(= user %) (->> list-people-response
                               (#(get-in % [:body :list :entries]))
                               (map #(get-in % [:entry :id])))))))

(deftest get-person
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        get-person-response (people/get-person ticket user)]
    (is (= 200 (:status get-person-response)))
    (is (true? (get-in get-person-response [:body :entry :capabilities :is-admin])))))