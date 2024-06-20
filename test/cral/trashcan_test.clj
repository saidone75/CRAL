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

(ns cral.trashcan-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.renditions :as renditions]
            [cral.api.core.trashcan :as trashcan]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(use-fixtures :once fixtures/setup)

(def ^:const content-file "Elkjaer_Briegel.jpg")

(deftest list-deleted-nodes-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)
        ;; list deleted nodes
        list-deleted-nodes-response (trashcan/list-deleted-nodes ticket)]
    (is (= (:status list-deleted-nodes-response) 200))
    (is (some #(= (get-in % [:entry :id]) created-node-id) (get-in list-deleted-nodes-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest get-deleted-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)
        ;; get deleted node
        get-deleted-node-response (trashcan/get-deleted-node ticket created-node-id)]
    (is (= (:status get-deleted-node-response) 200))
    (is (= (get-in get-deleted-node-response [:body :entry :id]) created-node-id))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest delete-deleted-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)]
    ;; delete deleted node
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))
    ;; check if node have been deleted
    (is (= (:status (trashcan/get-deleted-node ticket created-node-id)) 404))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 404))))

(deftest get-deleted-node-content
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))
        _ (spit file-to-be-uploaded file-content)
        ;; update node content
        _ (nodes/update-node-content ticket created-node-id file-to-be-uploaded)
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)
        ;; get deleted node content
        get-deleted-node-content-response (trashcan/get-deleted-node-content ticket created-node-id)]
    (is (= (:status get-deleted-node-content-response) 200))
    (is (= (apply str (map char (:body get-deleted-node-content-response))) file-content))
    ;; clean up
    (io/delete-file file-to-be-uploaded)
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest restore-deleted-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)]
    ;; check if node have been deleted
    (is (= (:status (nodes/get-node ticket created-node-id)) 404))
    ;; restore deleted node
    (is (= (:status (->> (model/map->RestoreDeletedNodeBody {:target-parent-id (tu/get-guest-home ticket)
                                                             :assoc-type       cm/assoc-contains})
                         (trashcan/restore-deleted-node ticket created-node-id))) 200))
    ;; check if node have been restored
    (is (= (:status (nodes/get-node ticket created-node-id)) 200))
    ;; clean up
    (is (= (:status (->> (model/->DeleteNodeQueryParams true)
                         (nodes/delete-node ticket created-node-id))) 204))))

(deftest list-deleted-node-renditions-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)
        list-deleted-node-renditions-response (trashcan/list-deleted-node-renditions ticket created-node-id)]
    (is (= (:status list-deleted-node-renditions-response) 200))
    (is (not-empty (get-in list-deleted-node-renditions-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest get-deleted-node-rendition-info-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; ask for rendition creation
        _ (renditions/create-rendition ticket created-node-id [(model/map->CreateRenditionBody {:id "doclib"})])
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)]
    (is (= (:status (trashcan/get-deleted-node-rendition-info ticket created-node-id "doclib")) 200))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest get-deleted-node-rendition-content-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; ask for rendition creation
        _ (renditions/create-rendition ticket created-node-id [(model/map->CreateRenditionBody {:id "doclib"})])]
    (loop [list-renditions-response nil]
      (when (empty? (filter #(= (get-in % [:entry :status]) "CREATED") (get-in list-renditions-response [:body :list :entries])))
        (Thread/sleep 1000)
        (recur (renditions/list-renditions ticket created-node-id))))
    ;; delete node
    (nodes/delete-node ticket created-node-id)
    (let [get-deleted-node-rendition-content-response (->> (get-in (first (filter #(= (get-in % [:entry :status]) "CREATED") (get-in (trashcan/list-deleted-node-renditions ticket created-node-id) [:body :list :entries]))) [:entry :id])
                                                           (trashcan/get-deleted-node-rendition-content ticket created-node-id))]
      (is (= (:status get-deleted-node-rendition-content-response) 200))
      (is (bytes? (:body get-deleted-node-rendition-content-response)))
      (is (> (alength (:body get-deleted-node-rendition-content-response)) 0)))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))