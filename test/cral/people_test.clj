(ns cral.people-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.people :as people]
            [cral.alfresco.model.core :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")
(def saidone "saidone")

(deftest create-person-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        create-person-response (->> (model/map->CreatePersonBody {:id         saidone
                                                                  :first-name saidone
                                                                  :email      "saidone@saidone.org"
                                                                  :password   saidone})
                                    (people/create-person ticket))]
    (is (or (= (:status create-person-response) 201) (= (:status create-person-response) 409)))
    ;; check if saidone has been created
    (is (some #(= saidone %) (map #(get-in % [:entry :id]) (get-in (people/list-people ticket) [:body :list :entries]))))))

(deftest list-people-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; list people
        list-people-response (people/list-people ticket)]
    (is (= (:status list-people-response) 200))
    (is (some #(= "admin" %) (map #(get-in % [:entry :id]) (get-in list-people-response [:body :list :entries]))))))

(deftest get-person-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; create user if not exist
        _ (->> (model/map->CreatePersonBody {:id         saidone
                                             :first-name saidone
                                             :email      "saidone@saidone.org"
                                             :password   saidone})
               (people/create-person ticket))
        ;; get person
        get-person-response (people/get-person ticket saidone)]
    (is (= (:status get-person-response) 200))
    (is (= (get-in get-person-response [:body :entry :id]) saidone))))

(deftest update-person-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; create user if not exist
        _ (->> (model/map->CreatePersonBody {:id         saidone
                                             :first-name saidone
                                             :email      "saidone@saidone.org"
                                             :password   saidone})
               (people/create-person ticket))
        new-description (.toString (UUID/randomUUID))
        ;; update person
        update-person-response (->> (model/map->UpdatePersonBody {:first-name                  saidone
                                                                  :email                       "saidone@saidone.org"
                                                                  :old-password                saidone
                                                                  :password                    saidone
                                                                  :description                 new-description
                                                                  :enabled                     true
                                                                  :email-notifications-enabled false})
                                    (people/update-person ticket saidone))]
    (is (= (:status update-person-response) 200))
    (is (= (get-in update-person-response [:body :entry :description]) new-description))))