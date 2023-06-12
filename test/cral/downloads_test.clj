(ns cral.downloads-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.downloads :as downloads]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-get-then-delete-download
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a couple of nodes
        node-id1 (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        node-id2 (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    ;; update nodes with some content
    (nodes/update-node-content ticket node-id1 file-to-be-uploaded)
    (nodes/update-node-content ticket node-id2 file-to-be-uploaded)
    (let [get-response
          ;; create download
          (let [create-response (downloads/create-download ticket (model/->CreateDownloadBody [node-id1 node-id2]))]
            (is (= 202 (:status create-response)))
            (let [get-response
                  ;; poll download while status is "PENDING"
                  (loop [get-response (downloads/get-download ticket (get-in create-response [:body :entry :id]))]
                    (if (= "PENDING" (get-in get-response [:body :entry :status]))
                      (do
                        (Thread/sleep 100)
                        (recur (downloads/get-download ticket (get-in get-response [:body :entry :id]))))
                      get-response))]
              (is (= 200 (:status get-response)))
              get-response))]
      ;; more check for get download response
      (is (= 2 (get-in get-response [:body :entry :files-added])))
      ;; delete download
      (is (= 202 (:status (downloads/delete-download ticket (get-in get-response [:body :entry :id]))))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id1))))
    (is (= 204 (:status (nodes/delete-node ticket node-id2))))))
