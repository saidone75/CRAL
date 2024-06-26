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

(ns cral.auth-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.config :as c]
            [cral.fixtures :as fixtures]))

(use-fixtures :once fixtures/setup)

(deftest create-ticket-test
  ;; create ticket
  (let [create-ticket-response (auth/create-ticket c/user c/password)]
    (is (= (:status create-ticket-response) 201))))

(deftest validate-ticket-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    ;; validate ticket
    (is (= (:status (auth/validate-ticket ticket)) 200))))

(deftest delete-ticket-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    ;; delete ticket
    (is (= (:status (auth/delete-ticket ticket)) 204))))