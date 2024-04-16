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
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.versions :as versions]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest list-version-history-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-version-information-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-version-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-version-content-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))
    (io/delete-file file-to-be-uploaded)))