(ns cral.shared-links-test
  (:require [clojure.test :refer :all])
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.shared-links :as shared-links]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.core]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest create-shared-link
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    (is (= (:status create-node-response) 201))
    (let [create-shared-link-body (model/map->CreateSharedLinkBody {:node-id (get-in create-node-response [:body :entry :id])})
          ;; create a shared link
          create-shared-link-response (shared-links/create-shared-link ticket create-shared-link-body)]
      (= (:status create-shared-link-response) 200))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))))