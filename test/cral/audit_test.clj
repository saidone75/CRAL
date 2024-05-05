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

(ns cral.audit-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.audit :as audit]
            [cral.fixtures :as fixtures]
            [cral.config :as c]))

(use-fixtures :once fixtures/setup)

(deftest list-audit-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    ;; list audit applications
    (is (= (:status (audit/list-audit-applications ticket)) 200))))

(deftest get-audit-application-info-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; get an audit application id
        audit-application-id (get-in (rand-nth (get-in (audit/list-audit-applications ticket) [:body :list :entries])) [:entry :id])
        get-audit-application-info-response (audit/get-audit-application-info ticket audit-application-id)]
    (is (= (:status get-audit-application-info-response) 200))))