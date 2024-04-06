(ns cral.trashcan-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.trashcan :as trashcan]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core :as model]
            [cral.core :refer :all]
            [cral.test-utils :as tu]
            [taoensso.timbre :as timbre])
  (:import (java.io File)
           (java.util UUID)))

(def user "admin")
(def password "admin")

(timbre/set-config! {:min-level :info})

(deftest trashcan-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    ;; update node content
    (let [file-to-be-uploaded (File/createTempFile "tmp." ".txt")
          file-content (.toString (UUID/randomUUID))]
      (spit file-to-be-uploaded file-content)
      (nodes/update-node-content ticket (get-in create-node-response [:body :entry :id]) file-to-be-uploaded)
      ;; delete node
      (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))
      ;; list deleted nodes
      (is (= (:status (trashcan/list-deleted-nodes ticket)) 200))
      ;; get deleted node content
      (let [content (trashcan/get-deleted-node-content ticket (get-in create-node-response [:body :entry :id]))]
        (is (= (apply str (map char (:body content))) file-content)))
      ;; delete temp file
      (io/delete-file file-to-be-uploaded))
    ;; get deleted node
    (let [get-deleted-node-response (trashcan/get-deleted-node ticket (get-in create-node-response [:body :entry :id]))]
      (is (= (:status get-deleted-node-response) 200))
      (is (= (get-in get-deleted-node-response [:body :entry :id]) (get-in create-node-response [:body :entry :id])))
      ;; restore deleted node
      (is (= (:status (trashcan/restore-deleted-node ticket (get-in create-node-response [:body :entry :id]) nil)) 200))
      ;; check if the node has been restored
      (is (= (get-in (nodes/get-node ticket (get-in create-node-response [:body :entry :id])) [:body :entry :parent-id]) parent-id))
      ;; delete node again
      (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))
      ;; delete deleted node
      (is (= (:status (trashcan/delete-deleted-node ticket (get-in get-deleted-node-response [:body :entry :id]))) 204))
      ;; check if node has been permanently deleted
      (is (= (:status (trashcan/get-deleted-node ticket (get-in create-node-response [:body :entry :id]))) 404))
      (is (= (:status (trashcan/delete-deleted-node ticket (get-in get-deleted-node-response [:body :entry :id]))) 404)))))