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

(ns cral.downloads-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.downloads :as downloads]
            [cral.api.core.nodes :as nodes]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(use-fixtures :once fixtures/setup)

(deftest downloads-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a couple of nodes
        node-id1 (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        node-id2 (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    ;; update nodes with some content
    (nodes/update-node-content ticket node-id1 file-to-be-uploaded)
    (nodes/update-node-content ticket node-id2 file-to-be-uploaded)
    (let [get-response
          ;; create download
          (let [create-response (downloads/create-download ticket (model/->CreateDownloadBody [node-id1 node-id2]))]
            (is (= (:status create-response) 202))
            (let [get-response
                  ;; poll download while status is "PENDING"
                  (loop [get-response (downloads/get-download ticket (get-in create-response [:body :entry :id]))]
                    (if (= (get-in get-response [:body :entry :status]) "PENDING")
                      (do
                        (Thread/sleep 100)
                        (recur (downloads/get-download ticket (get-in get-response [:body :entry :id]))))
                      get-response))]
              (is (= (:status get-response) 200))
              get-response))]
      ;; more check for get download response
      (is (= (get-in get-response [:body :entry :files-added]) 2))
      ;; delete download
      (is (= (:status (downloads/delete-download ticket (get-in get-response [:body :entry :id]))) 202)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket node-id1 {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket node-id2 {:permanent true})) 204))
    ;; delete temp file
    (io/delete-file file-to-be-uploaded)))