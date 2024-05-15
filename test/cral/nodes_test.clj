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

(ns cral.nodes-test
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.model.alfresco.permissions :as permissions]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(use-fixtures :once fixtures/setup)

(deftest get-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        guest-home-id (tu/get-guest-home ticket)]
    ;; well known fields for query parameters defined in GetNodeQueryParams
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id (model/map->GetNodeQueryParams {:include ["path" "permissions"]})) [:body :entry])) [:path :permissions])))
    ;; but plain maps can be used as well
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id {:include ["path" "permissions"]}) [:body :entry])) [:path :permissions])))))

(deftest update-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket parent-id)
                             (#(get-in % [:body :entry :id])))
        new-name (.toString (UUID/randomUUID))]
    ;; update node with the new name
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:name new-name}))
    ;; check if name has been updated
    (is (= (get-in (nodes/get-node ticket created-node-id) [:body :entry :name]) new-name))
    ;; update node with new permissions
    (->> (model/map->UpdateNodeBody {:permissions (model/map->Permissions {:is-inheritance-enabled false
                                                                           :locally-set            [{:authority-id  "guest"
                                                                                                     :name          permissions/editor
                                                                                                     :access-status "ALLOWED"}]})})
         (nodes/update-node ticket created-node-id))
    ; check if new permissions have been applied
    (let [permissions (get-in (nodes/get-node ticket created-node-id (model/map->GetNodeQueryParams {:include "permissions"})) [:body :entry :permissions])]
      (is (= (:is-inheritance-enabled permissions) false))
      (is (some #(= (:authority-id %) "guest") (:locally-set permissions))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))]
    ;; delete node
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest list-node-children-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        company-home-id (get-in (nodes/get-node ticket "-root-") [:body :entry :id])
        list-node-children-response (nodes/list-node-children ticket company-home-id)]
    (is (= (:status list-node-children-response) 200))
    (is (some #(= "Data Dictionary" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))
    (is (some #(= "Sites" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))

(deftest create-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        create-node-response (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                                  (nodes/create-node ticket (tu/get-guest-home ticket)))]
    (is (= (:status create-node-response) 201))
    ;; delete node
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]) {:permanent true})) 204))))

(deftest copy-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a folder
        new-parent-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-folder})
                           (nodes/create-node ticket (tu/get-guest-home ticket))
                           (#(get-in % [:body :entry :id])))
        ;; copy node into the new folder
        copy-node-response (->> (model/map->CopyNodeBody {:target-parent-id new-parent-id :name (.toString (UUID/randomUUID))})
                                (nodes/copy-node ticket created-node-id))]
    (is (= (:status copy-node-response) 201))
    (is (= (get-in copy-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket new-parent-id {:permanent true})) 204))))

(deftest lock-node-test
  (let
    [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
     ;; create a node
     created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                          (nodes/create-node ticket (tu/get-guest-home ticket))
                          (#(get-in % [:body :entry :id])))]
    ;; lock the node
    (is (= (:status (->> (model/map->LockNodeBody {:time-to-expire 0 :type "ALLOW_OWNER_CHANGES" :lifetime "PERSISTENT"})
                         (nodes/lock-node ticket created-node-id))) 200))
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket created-node-id) [:body :entry :properties])) [cm/prop-lock-type cm/prop-lock-owner cm/prop-lock-lifetime])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest unlock-node-test
  (let
    [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
     ;; create a node
     created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                          (nodes/create-node ticket (tu/get-guest-home ticket))
                          (#(get-in % [:body :entry :id])))]
    ;; lock the node
    (is (= (:status (->> (model/map->LockNodeBody {:time-to-expire 0 :type "ALLOW_OWNER_CHANGES" :lifetime "PERSISTENT"})
                         (nodes/lock-node ticket created-node-id))) 200))
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket created-node-id) [:body :entry :properties])) [cm/prop-lock-type cm/prop-lock-owner cm/prop-lock-lifetime])))
    ;; unlock the node
    (is (= (:status (nodes/unlock-node ticket created-node-id)) 200))
    (is (every? false? (map (partial contains? (get-in (nodes/get-node ticket created-node-id) [:body :entry :properties])) [cm/prop-lock-type cm/prop-lock-owner cm/prop-lock-lifetime])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest move-node-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a new folder
        new-parent-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-folder})
                           (nodes/create-node ticket (tu/get-guest-home ticket))
                           (#(get-in % [:body :entry :id])))
        ;; move node into the new folder
        move-node-response (->> (model/map->MoveNodeBody {:target-parent-id new-parent-id})
                                (nodes/move-node ticket created-node-id))]
    (is (= (:status move-node-response) 200))
    ;; check if the node has been moved
    (is (= (get-in move-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket new-parent-id {:permanent true})) 204))))

(deftest get-node-content-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a temp file
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
    ;; update the node content
    (is (= (:status (nodes/update-node-content ticket created-node-id file-to-be-uploaded) 200)))
    (let [;; get node content
          response (nodes/get-node-content ticket created-node-id)
          ;; download content from the node
          downloaded-file (->> response
                               (#(get-in % [:headers "content-disposition"]))
                               (#(second (re-matches #".*\"([^\"]+)\".*" (second (str/split % #";")))))
                               (#(File. (str (System/getProperty "java.io.tmpdir") "/" %)))
                               (#(with-open [w (clojure.java.io/output-stream %)]
                                   (.write w (bytes (:body response)))
                                   %)))]
      (is (= (:status response) 200))
      ;; check if the content is the same of the uploaded file
      (is (= (apply str (map char (:body response))) (slurp (.getPath file-to-be-uploaded))))
      (is (= (slurp (.getPath file-to-be-uploaded))) (slurp (.getPath downloaded-file)))
      ;;clean up
      (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))
      (io/delete-file file-to-be-uploaded)
      (io/delete-file downloaded-file))))

(deftest update-node-content-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a temp file
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
    ;; update the node content
    (is (= (:status (nodes/update-node-content ticket created-node-id file-to-be-uploaded) 200)))
    (let [;; get node content
          response (nodes/get-node-content ticket created-node-id)]
      (is (= (:status response) 200))
      ;; check if the content is the same of the uploaded file
      (is (= (apply str (map char (:body response))) (slurp (.getPath file-to-be-uploaded))))
      ;;clean up
      (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))
      (io/delete-file file-to-be-uploaded))))

(deftest request-direct-access-url-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))]
    (is (= (:status (nodes/request-direct-access-url ticket created-node-id)) 501))
    ;;clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest create-secondary-child-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create secondary child
    (is (= (:status (->> [(model/map->CreateSecondaryChildBody {:child-id target-node-id :assoc-type cm/assoc-rendition})]
                         (nodes/create-secondary-child ticket source-node-id)) 200)))
    (is (= (get-in (first (get-in (nodes/list-secondary-children ticket source-node-id) [:body :list :entries])) [:entry :id]) target-node-id))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest list-secondary-children-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create secondary child
    (is (= (:status (->> [(model/map->CreateSecondaryChildBody {:child-id target-node-id :assoc-type cm/assoc-rendition})]
                         (nodes/create-secondary-child ticket source-node-id)) 200)))
    ;; list secondary children
    (let [response (nodes/list-secondary-children ticket source-node-id)]
      (is (= (:status response) 200))
      ;; check for target-node-id
      (is (= (get-in (first (get-in response [:body :list :entries])) [:entry :id]) target-node-id)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest delete-secondary-child-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create secondary child
    (is (= (:status (->> [(model/map->CreateSecondaryChildBody {:child-id target-node-id :assoc-type cm/assoc-rendition})]
                         (nodes/create-secondary-child ticket source-node-id)) 200)))
    ;; check for target-node-id
    (is (= (get-in (first (get-in (nodes/list-secondary-children ticket source-node-id) [:body :list :entries])) [:entry :id]) target-node-id))
    ;; delete secondary child
    (is (= (:status (nodes/delete-secondary-child ticket source-node-id target-node-id)) 204))
    ;; check children count
    (is (= (get-in (nodes/list-secondary-children ticket source-node-id) [:body :list :pagination :count]) 0))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest list-parents-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    (is (= (get-in (first (get-in (nodes/list-parents ticket (tu/get-guest-home ticket)) [:body :list :entries])) [:entry :name]) "Company Home"))))

(deftest create-node-assocs-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create an association between source and target
    (is (= (:status (->> [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})]
                         (nodes/create-node-assocs ticket source-node-id))) 201))
    ;; check association
    (is (some #(= % target-node-id) (map #(get-in % [:entry :id]) (get-in (->> (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"})
                                                                               (nodes/list-target-assocs ticket source-node-id)) [:body :list :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest list-target-assocs-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create an association between source and target
    (is (= (:status (->> [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})]
                         (nodes/create-node-assocs ticket source-node-id))) 201))
    ;; list associations
    (let [response (->> (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"})
                        (nodes/list-target-assocs ticket source-node-id))]
      (is (= (:status response) 200))
      (is (some #(= % target-node-id) (map #(get-in % [:entry :id]) (get-in response [:body :list :entries])))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest delete-node-assocs-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create an association between source and target
    (is (= (:status (->> [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})]
                         (nodes/create-node-assocs ticket source-node-id))) 201))
    ;; check association
    (is (some #(= % target-node-id) (map #(get-in % [:entry :id]) (get-in (->> (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"})
                                                                               (nodes/list-target-assocs ticket source-node-id)) [:body :list :entries]))))
    ;; delete association
    (is (= (:status (nodes/delete-node-assocs ticket source-node-id target-node-id)) 204))
    ;; check if association has been deleted
    (is (empty? (get-in (->> (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"})
                             (nodes/list-target-assocs ticket source-node-id)) [:body :list :entries])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))

(deftest list-source-assocs-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))
        ;; create the target node
        target-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                            (nodes/create-node ticket parent-id)
                            (#(get-in % [:body :entry :id])))]
    ;; create an association between source and target
    (is (= (:status (->> [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})]
                         (nodes/create-node-assocs ticket source-node-id))) 201))
    ;; check source association
    (is (not (empty? (->> (model/map->ListSourceAssocsQueryParams {:where "(assocType='cm:references')"})
                          (nodes/list-source-assocs ticket target-node-id)
                          (#(get-in % [:body :list :entries]))))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id {:permanent true})) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id {:permanent true})) 204))))