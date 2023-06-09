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

(deftest list-tags
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; list tags
    (let [response (tags/list-tags ticket node-id)]
      (is (= 204 (:status (nodes/delete-node ticket node-id))))
      response)))


