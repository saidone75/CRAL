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

(ns cral.actions-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.actions :as actions]
            [cral.api.core.nodes :as nodes]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)

(deftest list-node-actions-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket parent-id)
                             (#(get-in % [:body :entry :id])))
        list-node-actions-response (actions/list-node-actions ticket created-node-id)]
    (is (= (:status list-node-actions-response) 200))
    (is (not (empty? (get-in list-node-actions-response [:body :list :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest list-available-actions-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        list-available-actions-response (actions/list-available-actions ticket)]
    (is (= (:status list-available-actions-response) 200))
    (is (not (empty? (get-in list-available-actions-response [:body :list :entries]))))))

(deftest get-action-definition-details-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        action-definition-id (-> (actions/list-available-actions ticket)
                                 (get-in [:body :list :entries])
                                 (rand-nth)
                                 (get-in [:entry :id]))
        get-action-definition-details-response (actions/get-action-definition-details ticket action-definition-id)]
    (is (= (:status get-action-definition-details-response) 200))))

(deftest execute-action-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a new folder
        new-parent-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-folder})
                           (nodes/create-node ticket (tu/get-guest-home ticket))
                           (#(get-in % [:body :entry :id])))
        execute-action-response (->> (model/map->ExecuteActionBody {:action-definition-id "copy"
                                                                    :target-id created-node-id
                                                                    :params {:destination-folder new-parent-id}})
                                     (actions/execute-action ticket))]
    (println execute-action-response)
    ;; clean up
    (is (= (:status (nodes/delete-node ticket new-parent-id {:permanent true})) 204))))