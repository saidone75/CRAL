(ns cral.tags-tests
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.tags :as tags]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-list-then-delete-node-tags
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        tag-name (.toString (UUID/randomUUID))]
    ;; create a tag for the node
    (= 201 (:status (tags/create-node-tag ticket node-id [(model/map->CreateNodeTagBody {:tag tag-name})])))
    ;; list tags
    (let [response (tags/list-node-tags ticket node-id)]
      (is (= 200 (:status response)))
      (is (= tag-name (get-in (first (get-in response [:body :list :entries])) [:entry :tag]))) [:tag])
    ;; FIXME delete tag
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id))))))

