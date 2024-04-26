;  CRAL
;  Copyright (C) 2023-2024 Saidone
;  
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;  
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;  
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns cral.people-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.people :as people]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(def saidone "saidone")

(deftest create-person-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        create-person-response (->> (model/map->CreatePersonBody {:id         saidone
                                                                  :first-name saidone
                                                                  :last-name  saidone
                                                                  :email      "saidone@saidone.org"
                                                                  :password   saidone})
                                    (people/create-person ticket))]
    (is (or (= (:status create-person-response) 201) (= (:status create-person-response) 409)))
    ;; check if saidone has been created
    (is (some #(= saidone %) (map #(get-in % [:entry :id]) (get-in (people/list-people ticket) [:body :list :entries]))))))

(deftest list-people-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; list people
        list-people-response (people/list-people ticket)]
    (is (= (:status list-people-response) 200))
    (is (some #(= "admin" %) (map #(get-in % [:entry :id]) (get-in list-people-response [:body :list :entries]))))))

(deftest get-person-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; get person
        get-person-response (people/get-person ticket saidone)]
    (is (= (:status get-person-response) 200))
    (is (= (get-in get-person-response [:body :entry :id]) saidone))))

(deftest update-person-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        new-description (.toString (UUID/randomUUID))
        ;; update person
        update-person-response (->> (model/map->UpdatePersonBody {:first-name                  saidone
                                                                  :last-name                   saidone
                                                                  :email                       "saidone@saidone.org"
                                                                  :old-password                saidone
                                                                  :password                    saidone
                                                                  :description                 new-description
                                                                  :enabled                     true
                                                                  :email-notifications-enabled false})
                                    (people/update-person ticket saidone))]
    (is (= (:status update-person-response) 200))
    (is (= (get-in update-person-response [:body :entry :description]) new-description))))