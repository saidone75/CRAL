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

(ns cral.versions-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.versions :as versions]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(use-fixtures :once fixtures/setup)

(def ^:const content-file "Elkjaer_Briegel.jpg")

(deftest list-version-history-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; list version history
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (is (= (:status version-history-response) 200))
      ;; check if history is empty
      (is (empty? (get-in version-history-response [:body :list :entries]))))
    ;; create 1.0 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; list version history again
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (is (= (:status version-history-response) 200))
      ;; assert that history is not empty
      (is (not (empty? (get-in version-history-response [:body :list :entries])))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-version-information-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create 1.0 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; get version history
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (when-not (empty? (get-in version-history-response [:body :list :entries]))
        ;; get version information
        (let [get-version-information-response (versions/get-version-information ticket created-node-id (get-in (first (get-in version-history-response [:body :list :entries])) [:entry :id]))]
          (is (= (:status get-version-information-response 200)))
          (is (= (get-in get-version-information-response [:body :entry :properties cm/prop-version-type]) "MAJOR")))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-version-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create 1.0 version by adding cm:versionable and set cm:autoVersionOnUpdateProps to true
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable] :properties {cm/prop-auto-version-on-update-props true}}))
    ;; create 1.1 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:properties {cm/prop-name (.toString (UUID/randomUUID))}}))
    ;; count versions
    (is (= (count (get-in (versions/list-version-history ticket created-node-id) [:body :list :entries])) 2))
    ;; delete version 1.1
    (is (= (:status (versions/delete-version ticket created-node-id "1.1")) 204))
    ;; count versions
    (is (= (count (get-in (versions/list-version-history ticket created-node-id) [:body :list :entries])) 1))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-version-content-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    ;; update the node content
    (nodes/update-node-content ticket created-node-id file-to-be-uploaded)
    ;; create 1.0 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; get 1.0 version content
    (is (= (apply str (map char (:body (versions/get-version-content ticket created-node-id "1.0")))) file-content))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))
    (io/delete-file file-to-be-uploaded)))

(deftest revert-version-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create 1.0 version by adding cm:versionable and set cm:autoVersionOnUpdateProps to true
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable] :properties {cm/prop-auto-version-on-update-props true}}))
    ;; create 1.1 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:properties {cm/prop-name (.toString (UUID/randomUUID))}}))
    ;; revert 1.0 version as new major version
    (let [revert-version-response (versions/revert-version ticket created-node-id "1.0" (model/map->RevertVersionBody {:major-version true}))]
      (is (= (:status revert-version-response) 200))
      (is (= (get-in revert-version-response [:body :entry :properties cm/prop-version-type]) "MAJOR")))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest create-version-rendition-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; add cm:versionable aspect
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; update the node content
    (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
    ;; ask for rendition creation
    (versions/create-version-rendition ticket created-node-id "1.1" [(model/map->CreateVersionRenditionBody {:id "doclib"})])
    ;; list renditions for version
    (loop [list-version-renditions-response nil]
      (if (empty? (filter #(= (get-in % [:entry :status]) "CREATED") (get-in list-version-renditions-response [:body :list :entries])))
        (do (Thread/sleep 1000)
            (recur (versions/list-version-renditions ticket created-node-id "1.1")))
        (is (= (:status list-version-renditions-response) 200))))
    ; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest list-version-renditions-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; add cm:versionable aspect
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; update the node content
    (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
    ;; ask for rendition creation
    (versions/create-version-rendition ticket created-node-id "1.1" [(model/map->CreateVersionRenditionBody {:id "doclib"})])
    ;; list renditions for version
    (let [list-version-renditions-response (versions/list-version-renditions ticket created-node-id "1.1")]
      (is (= (:status list-version-renditions-response) 200))
      (is (not (empty? (get-in list-version-renditions-response [:body :list :entries])))))
    ; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-version-rendition-info-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; add cm:versionable aspect
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; update the node content
    (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
    ;; ask for rendition creation
    (versions/create-version-rendition ticket created-node-id "1.1" [(model/map->CreateVersionRenditionBody {:id "doclib"})])
    ;; get version rendition info
    (let [get-version-rendition-info-response (versions/get-version-rendition-info ticket created-node-id "1.1" "doclib")]
      (is (= (:status get-version-rendition-info-response) 200))
      (is (= (get-in get-version-rendition-info-response [:body :entry :id]) "doclib")))
    ; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-version-rendition-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; add cm:versionable aspect
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; update the node content
    (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
    ;; ask for rendition creation
    (versions/create-version-rendition ticket created-node-id "1.1" [(model/map->CreateVersionRenditionBody {:id "doclib"})])
    (loop [list-version-renditions-response nil]
      (if (empty? (filter #(= (get-in % [:entry :status]) "CREATED") (get-in list-version-renditions-response [:body :list :entries])))
        (do (Thread/sleep 1000)
            (recur (versions/list-version-renditions ticket created-node-id "1.1")))
        (is (= (:status list-version-renditions-response) 200))))
    ;; delete rendition
    (is (= (:status (versions/delete-version-rendition ticket created-node-id "1.1" "doclib")) 204))
    (is (=
          (->> (get-in (versions/list-version-renditions ticket created-node-id "1.1") [:body :list :entries])
               (filter #(= "doclib" (get-in % [:entry :id])))
               (first)
               (#(get-in % [:entry :status])))
          "NOT_CREATED"))
    ; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-version-rendition-content-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; add cm:versionable aspect
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names [cm/asp-versionable]}))
    ;; update the node content
    (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
    ;; ask for rendition creation
    (versions/create-version-rendition ticket created-node-id "1.1" [(model/map->CreateVersionRenditionBody {:id "doclib"})])
    ;; get version rendition info
    (loop [get-version-rendition-info-response nil]
      (when-not (= (get-in get-version-rendition-info-response [:body :entry :status]) "CREATED")
        (Thread/sleep 1000)
        (recur (versions/get-version-rendition-info ticket created-node-id "1.1" "doclib"))))
    (let [get-version-rendition-content-response (versions/get-version-rendition-content ticket created-node-id "1.1" "doclib")]
      (is (= (:status get-version-rendition-content-response) 200))
      (is (bytes? (:body get-version-rendition-content-response)))
      (is (> (alength (:body get-version-rendition-content-response)) 0)))
    ; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))