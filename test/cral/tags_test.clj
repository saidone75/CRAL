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

(ns cral.tags-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.tags :as tags]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-list-then-get-then-delete-node-tags
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        tag-name (.toString (UUID/randomUUID))]
    ;; create a tag for the node
    (= (:status (tags/create-node-tag ticket node-id [(model/map->CreateNodeTagBody {:tag tag-name})])) 201)
    ;; list tags
    (let [list-node-tag-response (tags/list-node-tags ticket node-id)]
      (is (= (:status list-node-tag-response) 200))
      (is (= (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :tag]) tag-name))
      ;; get tag
      (is (= (:status (tags/get-tag ticket (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]))) 200))
      ;; update tag
      (let [new-tag-name (.toString (UUID/randomUUID))
            update-tag-response (tags/update-tag ticket (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]) (model/map->UpdateTagBody {:tag new-tag-name}))]
        (is (= (:status update-tag-response) 200))
        (is (= (get-in update-tag-response [:body :entry :tag]) new-tag-name)))
      ;; delete tag
      (is (= (:status (tags/delete-node-tag ticket node-id (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]))) 204)))
    ;; check if tag has been deleted
    (is (empty? (get-in (tags/list-node-tags ticket node-id) [:body :list :entries])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket node-id)) 204))))