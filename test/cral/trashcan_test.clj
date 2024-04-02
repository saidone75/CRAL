(ns cral.trashcan-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.trashcan :as trashcan]
            [cral.alfresco.model.core :as model]
            [cral.core :refer :all]
            [cral.test-utils :as tu]
            [taoensso.timbre :as timbre])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(timbre/set-config! {:min-level :info})

(deftest list-deleted-nodes
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    (is (= (:status (trashcan/list-deleted-nodes ticket)) 200))))

(deftest get-then-delete-deleted-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    ;; delete node
    (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))
    ;; get deleted node
    (let [get-deleted-node-response (trashcan/get-deleted-node ticket (get-in create-node-response [:body :entry :id]))]
      (is (= (:status get-deleted-node-response) 200))
      (is (= (get-in get-deleted-node-response [:body :entry :id]) (get-in create-node-response [:body :entry :id])))
      ;; delete deleted node
      (is (= (:status (trashcan/delete-deleted-node ticket (get-in get-deleted-node-response [:body :entry :id]))) 204))
      ;; check if node has been permanently deleted
      (is (= (:status (trashcan/get-deleted-node ticket (get-in create-node-response [:body :entry :id]))) 404))
      (is (= (:status (trashcan/delete-deleted-node ticket (get-in get-deleted-node-response [:body :entry :id]))) 404)))))
