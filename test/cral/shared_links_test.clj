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

(deftest create-then-list-then-get-then-delete-shared-link
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    (is (= (:status create-node-response) 201))
    (let [create-shared-link-body (model/map->CreateSharedLinkBody {:node-id (get-in create-node-response [:body :entry :id])})
          ;; create a shared link
          create-shared-link-response (shared-links/create-shared-link ticket create-shared-link-body)]
      (= (:status create-shared-link-response) 200)
      ;; list shared links
      (loop [list-shared-link-response (shared-links/list-shared-links ticket)]
        (when-not (some #(= (get-in create-node-response [:body :entry :id]) %) (map #(get-in % [:entry :node-id]) (get-in list-shared-link-response [:body :list :entries])))
          (Thread/sleep 1000)
          (recur (shared-links/list-shared-links ticket))))
      ;; get shared link
      (is (= (:status (shared-links/get-shared-link (get-in create-shared-link-response [:body :entry :id]))) 200))
      ;; delete shared link
      (is (= (:status (shared-links/delete-shared-link ticket (get-in create-shared-link-response [:body :entry :id]))) 204)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))))