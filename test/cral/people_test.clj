(ns cral.people-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.people :as people]
            [cral.alfresco.model.core :as model]))

(def user "admin")
(def pass "admin")

(deftest create-person-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        create-person-response (->> (model/map->CreatePersonBody {:id         "saidone"
                                                                  :first-name "saidone"
                                                                  :email      "saidone@saidone.org"
                                                                  :password   "saidone"})
                                    (people/create-person ticket))]
    (is (or (= (:status create-person-response) 201) (= (:status create-person-response) 409)))
    ;; check if "saidone" has been created
    (is (some #(= "saidone" %) (map #(get-in % [:entry :id]) (get-in (people/list-people ticket) [:body :list :entries]))))))

;; TODO
(deftest list-people-test)

;; TODO
(deftest get-person-test)

;; TODO
(deftest update-person-test)

;; old tests
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