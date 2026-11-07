;
;  CRAL
;  Copyright (C) 2023-2026 Saidone
;  SPDX-License-Identifier: GPL-3.0-or-later
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
;

(ns cral.preferences-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [cral.api.auth :as auth]
            [cral.api.core.preferences :as preferences]
            [cral.api.core.favorites :as favorites]
            [cral.api.core.nodes :as nodes]
            [cral.test-utils :as tu]
            [cral.config :as c]
            [cral.fixtures :as fixtures])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(use-fixtures :each fixtures/resources)

(deftest list-preferences-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; list preferences
        list-preferences-response (preferences/list-preferences ticket "-me-")]
    (is (= (:status list-preferences-response) 200))))

(deftest get-preference-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        node (nodes/create-node ticket (tu/get-guest-home ticket) {:name (str (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in node [:body :entry :id])
        preference-name "org.alfresco.share.documents.favourites"]
    (is (= 201 (:status node)))
    (when (= 201 (:status node))
      (fixtures/on-cleanup #(favorites/delete-favorite ticket "-me-" node-id))
      (is (= 201 (:status (favorites/create-favorite ticket "-me-" [{:target {:file {:guid node-id}}}]))))
      (let [response (preferences/get-preference ticket "-me-" preference-name)]
        (is (= 200 (:status response)))
        (is (= preference-name (get-in response [:body :entry :id])))
        (is (str/includes? (str (get-in response [:body :entry :value])) node-id))))))
