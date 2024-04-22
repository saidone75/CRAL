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

(ns cral.preferences-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.preferences :as preferences]))

(def user "admin")
(def password "admin")

(deftest list-preferences-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; list preferences
        list-preferences-response (preferences/list-preferences ticket "-me-")]
    (is (= (:status list-preferences-response) 200))))

(deftest get-preference-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; list preferences
        preferences (get-in (preferences/list-preferences ticket "-me-") [:body :list :entries])]
    ;; get random preference
    (is (= (:status (preferences/get-preference ticket "-me-" (get-in (rand-nth preferences) [:entry :id]))) 200))))