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
    (is (some #(= "admin" %) (->> list-people-response
                                  (#(get-in % [:body :list :entries]))
                                  (map #(get-in % [:entry :id])))))))