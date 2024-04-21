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
            [cral.api.core.trashcan :as trashcan]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest list-deleted-nodes-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; delete node
        _ (nodes/delete-node ticket created-node-id)
        ;; list deleted nodes
        list-deleted-nodes-response (trashcan/list-deleted-nodes ticket)]
    (is (= (:status list-deleted-nodes-response) 200))
    (is (some #(is (= (get-in % [:entry :id]) created-node-id)) (get-in list-deleted-nodes-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (trashcan/delete-deleted-node ticket created-node-id)) 204))))

(deftest get-deleted-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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

;; old tests
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