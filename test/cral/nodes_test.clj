(ns cral.nodes-test
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
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

(deftest get-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        guest-home-id (tu/get-guest-home ticket)]
    ;; well known fields for query parameters defined in GetNodeQueryParams
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id (model/map->GetNodeQueryParams {:include ["path" "permissions"]})) [:body :entry])) [:path :permissions])))
    ;; but plain maps can be used as well
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id {:include ["path" "permissions"]}) [:body :entry])) [:path :permissions])))))

(deftest update-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
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
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))]
    ;; delete node
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest list-node-children-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        company-home-id (get-in (nodes/get-node ticket "-root-") [:body :entry :id])
        list-node-children-response (nodes/list-node-children ticket company-home-id)]
    (is (= (:status list-node-children-response) 200))
    (is (not (nil? (some #(= "Data Dictionary" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))
    (is (not (nil? (some #(= "Sites" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))))

(deftest create-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        create-node-response (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                                  (nodes/create-node ticket (tu/get-guest-home ticket)))]
    (is (= (:status create-node-response) 201))
    ;; delete node
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))))

(deftest copy-node-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create a folder
        new-parent-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-folder})) [:body :entry :id])
        ;; copy node into the new folder
        copy-node-response (->> (model/map->CopyNodeBody {:target-parent-id new-parent-id :name (.toString (UUID/randomUUID))})
                                (nodes/copy-node ticket created-node-id))]
    (is (= (:status copy-node-response) 201))
    (is (= (get-in copy-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))
    (is (= (:status (nodes/delete-node ticket new-parent-id)) 204))))

(deftest lock-node-test
  (let
    [ticket (get-in (auth/create-ticket user password) [:body :entry])
     ;; create a node
     created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                          (nodes/create-node ticket (tu/get-guest-home ticket))
                          (#(get-in % [:body :entry :id])))]
    ;; lock the node
    (is (= (:status (->> (model/map->LockNodeBody {:time-to-expire 0 :type "ALLOW_OWNER_CHANGES" :lifetime "PERSISTENT"})
                         (nodes/lock-node ticket created-node-id))) 200))
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket created-node-id) [:body :entry :properties])) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest create-then-delete-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    (is (= (:status create-node-response) 201))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))))

(deftest lock-then-unlock-node
  (let
    [ticket (get-in (auth/create-ticket user password) [:body :entry])
     parent-id (tu/get-guest-home ticket)
     ;; create a noe
     node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
     ;; lock the node
     lock-node-body (model/map->LockNodeBody {:time-to-expire 0
                                              :type           "ALLOW_OWNER_CHANGES"
                                              :lifetime       "PERSISTENT"})]
    (is (= (:status (nodes/lock-node ticket node-id lock-node-body)) 200))
    (let [properties (get-in (nodes/get-node ticket node-id) [:body :entry :properties])]
      ;; check if the node is locked
      (is (every? true? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; unlock the node
    (is (= (:status (nodes/unlock-node ticket node-id)) 200))
    (let [properties (get-in (nodes/get-node ticket node-id) [:body :entry :properties])]
      ;; check if the node is unlocked
      (is (every? false? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket node-id)) 204))))

(deftest move-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create a new folder
        new-parent-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-folder})) [:body :entry :id])
        move-node-body (model/map->MoveNodeBody {:target-parent-id new-parent-id})
        ;; move node into the new folder
        move-node-response (nodes/move-node ticket created-node-id move-node-body)]
    ;; check if the node has been moved
    (is (= (get-in move-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket new-parent-id)) 204))))

(deftest get-node-content
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        ;; create a temp file
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
    ;; update the node content
    (nodes/update-node-content ticket node-id file-to-be-uploaded)
    (let [response (nodes/get-node-content ticket node-id)
          ;; download content from the node
          downloaded-file (->> response
                               (#(get-in % [:headers "content-disposition"]))
                               (#(second (re-matches #".*\"([^\"]+)\".*" (second (str/split % #";")))))
                               (#(File. (str (System/getProperty "java.io.tmpdir") "/" %)))
                               (#(with-open [w (clojure.java.io/output-stream %)]
                                   (.write w (bytes (:body response)))
                                   %)))]
      ;; check if the content is the same of the uploaded file
      (is (= (apply str (map char (:body (nodes/get-node-content ticket node-id)))) (slurp (.getPath downloaded-file))))
      ;;clean up
      (is (= (:status (nodes/delete-node ticket node-id)) 204))
      (io/delete-file file-to-be-uploaded)
      (io/delete-file downloaded-file))))

(deftest update-node-content
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    ;; update the node content
    (nodes/update-node-content ticket node-id file-to-be-uploaded)
    ;; check if the content is the same previously spit to the file
    (is (= (apply str (map char (:body (nodes/get-node-content ticket node-id)))) file-content))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket node-id)) 204))
    (io/delete-file file-to-be-uploaded)))

(deftest create-then-list-secondary-child
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create association
    (let [response (nodes/create-secondary-child ticket source-node-id [(model/map->CreateSecondaryChildBody {:child-id target-node-id :assoc-type cm/assoc-rendition})])]
      ;; list secondary children
      (let [response (nodes/list-secondary-children ticket source-node-id)]
        (is (= (:status response) 200))
        ;; check for target-node-id
        (is (= (get-in (first (get-in response [:body :list :entries])) [:entry :id]) target-node-id)))
      ;; clean up
      (is (= (:status (nodes/delete-node ticket source-node-id)) 204))
      (is (= (:status (nodes/delete-node ticket target-node-id)) 204))
      response)))

(deftest list-parents
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        node-id (tu/get-guest-home ticket)]
    (is (= (get-in (first (get-in (nodes/list-parents ticket node-id) [:body :list :entries])) [:entry :name]) "Company Home"))))

(deftest create-then-list-then-delete-node-assocs
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create an association between source and target
    (is (= (:status (nodes/create-node-assocs ticket source-node-id [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})])) 201))
    ;; list associations
    (let [response (nodes/list-target-assocs ticket source-node-id (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"}))
          entry (:entry (first (get-in response [:body :list :entries])))]
      (is (= (:status response) 200))
      (is (= "cm:references") (get-in entry [:association :assoc-type])))
    ;; delete the association
    (is (= (:status (nodes/delete-node-assocs ticket source-node-id target-node-id)) 204))
    ;; check if association has been deleted
    (let [response (nodes/list-target-assocs ticket source-node-id (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"}))]
      (is (= (:status response) 200))
      (is (empty? (get-in response [:body :list :pagination :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id)) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id)) 204))))

(deftest create-then-list-then-delete-source-assocs
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create association
    (is (= (:status (nodes/create-node-assocs ticket source-node-id [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type cm/assoc-references})])) 201))
    ;; list associations
    (let [response (nodes/list-source-assocs ticket target-node-id (model/map->ListSourceAssocsQueryParams {:where "(assocType='cm:references')"}))
          entry (:entry (first (get-in response [:body :list :entries])))]
      (is (= (:status response) 200))
      (is (= (get-in entry [:association :assoc-type]) "cm:references")))
    ;; delete association
    (is (= (:status (nodes/delete-node-assocs ticket source-node-id target-node-id)) 204))
    ;; check if association has been deleted
    (let [response (nodes/list-source-assocs ticket source-node-id (model/map->ListSourceAssocsQueryParams {:where "(assocType='cm:references')"}))]
      (is (= (:status response) 200))
      (is (empty? (get-in response [:body :list :pagination :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket source-node-id)) 204))
    (is (= (:status (nodes/delete-node ticket target-node-id)) 204))))