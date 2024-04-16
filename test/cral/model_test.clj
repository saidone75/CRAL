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

(ns cral.model-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model :as model]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest list-aspects-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; list aspects
        list-aspects-response (model/list-aspects ticket)]
    (is (= (:status list-aspects-response) 200))))

(deftest get-aspect-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; get aspect
        get-aspect-response (model/get-aspect ticket cm/asp-titled)]
    (is (= (get-in get-aspect-response [:body :entry :id]) (name cm/asp-titled)))
    (is (= (:status get-aspect-response) 200))))

(deftest list-types-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; list types
        list-types-response (model/list-types ticket)]
    (is (= (:status list-types-response) 200))))