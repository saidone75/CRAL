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
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)

(deftest list-node-tags-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        tag-name (.toString (UUID/randomUUID))
        ;; create a tag for the node
        _ (tags/create-node-tag ticket created-node-id [(model/map->CreateNodeTagBody {:tag tag-name})])
        ;; list tags
        list-node-tag-response (tags/list-node-tags ticket created-node-id)]
    (is (= (:status list-node-tag-response) 200))
    (is (= (count (get-in list-node-tag-response [:body :list :entries])) 1))
    ;; clean up
    (is (= (:status (tags/delete-node-tag ticket created-node-id (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]))) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest create-node-tags-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        tag-name (.toString (UUID/randomUUID))
        ;; create a tag for the node
        create-node-tags-response (tags/create-node-tag ticket created-node-id [(model/map->CreateNodeTagBody {:tag tag-name})])]
    (is (= (:status create-node-tags-response) 201))
    ;; clean up
    (is (= (:status (tags/delete-node-tag ticket created-node-id (get-in create-node-tags-response [:body :entry :id]))) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-node-tags-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        tag-name (.toString (UUID/randomUUID))
        ;; create a tag for the node
        create-node-tags-response (tags/create-node-tag ticket created-node-id [(model/map->CreateNodeTagBody {:tag tag-name})])]
    (is (= (:status create-node-tags-response) 201))
    ;; delete tag from node
    (is (= (:status (tags/delete-node-tag ticket created-node-id (get-in create-node-tags-response [:body :entry :id]))) 204))
    ;; check if tag has been deleted
    (is (= (count (get-in (tags/list-node-tags ticket created-node-id) [:body :list :entries])) 0))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest list-tags-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a tag
        created-tag-id (get-in (->> (model/map->CreateTagBody {:tag (.toString (UUID/randomUUID))})
                                    (tags/create-tag ticket)) [:body :entry :id])
        ;; list tags
        list-tags-response (tags/list-tags ticket)]
    (is (= (:status list-tags-response) 200))
    (is (some #(= (get-in % [:entry :id]) created-tag-id) (get-in list-tags-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (tags/delete-tag ticket created-tag-id)) 204))))

(deftest create-tag-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a tag
        create-tag-response (->> (model/map->CreateTagBody {:tag (.toString (UUID/randomUUID))})
                                 (tags/create-tag ticket))]
    (is (= (:status create-tag-response) 201))
    ;; check if tag has been created
    (is (some #(= (get-in % [:entry :id]) (get-in create-tag-response [:body :entry :id])) (get-in (tags/list-tags ticket) [:body :list :entries])))
    ;; clean up
    (is (= (:status (tags/delete-tag ticket (get-in create-tag-response [:body :entry :id]))) 204))))

(deftest get-tag-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a tag
        created-tag-id (get-in (->> (model/map->CreateTagBody {:tag (.toString (UUID/randomUUID))})
                                    (tags/create-tag ticket)) [:body :entry :id])
        ;; get tag
        get-tag-response (tags/get-tag ticket created-tag-id)]
    (is (= (:status get-tag-response) 200))
    (is (= (get-in get-tag-response [:body :entry :id] created-tag-id)))
    ;; clean up
    (is (= (:status (tags/delete-tag ticket created-tag-id)) 204))))

(deftest update-tag-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a tag
        created-tag-id (get-in (->> (model/map->CreateTagBody {:tag (.toString (UUID/randomUUID))})
                                    (tags/create-tag ticket)) [:body :entry :id])
        ;; update tag with new name
        new-tag-name (.toString (UUID/randomUUID))
        update-tag-response (->> (model/map->UpdateTagBody {:tag new-tag-name})
                                 (tags/update-tag ticket created-tag-id))]
    (is (= (:status update-tag-response) 200))
    (is (= (get-in update-tag-response [:body :entry :tag]) new-tag-name))
    ;; clean up
    (is (= (:status (tags/delete-tag ticket created-tag-id)) 204))))

(deftest delete-tag-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a tag
        created-tag-id (get-in (->> (model/map->CreateTagBody {:tag (.toString (UUID/randomUUID))})
                                    (tags/create-tag ticket)) [:body :entry :id])]
    ;; delete tag
    (is (= (:status (tags/delete-tag ticket created-tag-id)) 204))
    ;; check if tag has been deleted
    (is (not (some #(= (get-in % [:entry :id]) created-tag-id) (get-in (tags/list-tags ticket) [:body :list :entries]))))
    ;; clean up
    (is (= (:status (tags/delete-tag ticket created-tag-id)) 404))))