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

(ns cral.categories-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.categories :as categories]
            [cral.api.core.nodes :as nodes]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)

(deftest list-node-categories-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))]
    ;; list categories
    (let [list-node-categories-response (categories/list-node-categories ticket created-node-id)]
      (is (= (:status list-node-categories-response) 200)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest assign-node-category-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        rand-category-id (-> (categories/list-categories ticket "-root-")
                             (get-in [:body :list :entries])
                             (rand-nth)
                             (get-in [:entry :id]))
        ;; create node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        assign-node-category-response (categories/assign-node-category ticket created-node-id [(model/map->AssignNodeCategoryBody {:category-id rand-category-id})])]
    (is (= (:status assign-node-category-response) 201))
    (is (some #(= (get-in % [:entry :id]) rand-category-id)
              (-> (categories/list-node-categories ticket created-node-id)
                  (get-in [:body :list :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-category-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create category
        created-category-id (get-in (categories/create-category ticket "-root-" (model/map->CreateCategoryBody {:name (.toString (UUID/randomUUID))})) [:body :entry :id])]
    (is (= (:status (categories/delete-category ticket created-category-id)) 204))))

(deftest list-categories-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    ;; list categories
    (let [list-categories-response (categories/list-categories ticket "-root-")]
      (is (= (:status list-categories-response) 200))
      (is (not (empty? (get-in list-categories-response [:body :list :entries])))))))

(deftest create-category-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create category
        create-category-response (categories/create-category ticket "-root-" (model/map->CreateCategoryBody {:name (.toString (UUID/randomUUID))}))]
    (is (= (:status create-category-response) 201))
    ;; clean up
    (is (= (:status (categories/delete-category ticket (get-in create-category-response [:body :entry :id]))) 204))))