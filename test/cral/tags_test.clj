(ns cral.tags-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.tags :as tags]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-list-then-get-then-delete-node-tags
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        tag-name (.toString (UUID/randomUUID))]
    ;; create a tag for the node
    (= 201 (:status (tags/create-node-tag ticket node-id [(model/map->CreateNodeTagBody {:tag tag-name})])))
    ;; list tags
    (let [list-node-tag-response (tags/list-node-tags ticket node-id)]
      (is (= 200 (:status list-node-tag-response)))
      (is (= tag-name (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :tag])))
      ;; get tag
      (is (= 200 (:status (tags/get-tag ticket (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id])))))
      ;; update tag
      (let [new-tag-name (.toString (UUID/randomUUID))
            update-tag-response (tags/update-tag ticket (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]) (model/map->UpdateTagBody {:tag new-tag-name}))]
        (is (= 200 (:status update-tag-response)))
        (is (= new-tag-name (get-in update-tag-response [:body :entry :tag]))))
      ;; delete tag
      (is (= 204 (:status (tags/delete-node-tag ticket node-id (get-in (first (get-in list-node-tag-response [:body :list :entries])) [:entry :id]))))))
    ;; check if tag has been deleted
    (is (empty? (get-in (tags/list-node-tags ticket node-id) [:body :list :entries])))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id))))))