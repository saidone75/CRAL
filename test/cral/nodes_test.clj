(ns cral.nodes-test
  (:import (java.util UUID)
           (java.io File))
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [cral.core :refer :all]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.model.search :as search-model]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]
            [cral.test-utils :as tu]))

(def user "admin")
(def password "admin")

(timbre/set-config! {:min-level :info})

(deftest get-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        guest-home-id (:id (tu/get-guest-home ticket))]
    ;; well known fields for query parameters defined in core/QueryParamsGetNode
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id (model/map->GetNodeQueryParams {:include ["path" "permissions"]})) [:body :entry])) [:path :permissions])))
    ;; but plain maps can be used as well
    (is (every? true? (map (partial contains? (get-in (nodes/get-node ticket guest-home-id {:include ["path" "permissions"]}) [:body :entry])) [:path :permissions])))))

(deftest update-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        new-name (.toString (UUID/randomUUID))]
    ;; update node with the new name
    (nodes/update-node ticket node-id (model/map->UpdateNodeBody {:name new-name}))
    ;; check if name has been updated
    (is (= new-name (get-in (nodes/get-node ticket node-id) [:body :entry :name])))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id))))))

(deftest list-node-children
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        company-home-id (get-in (first (get-in (search/search ticket (search-model/map->SearchBody {:query (search-model/map->RequestQuery {:query "PATH:'app:company_home'"})})) [:body :list :entries])) [:entry :id])
        list-node-children-response (nodes/list-node-children ticket company-home-id)]
    (is (= 200 (:status list-node-children-response)))
    (is (not (nil? (some #(= "Data Dictionary" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))
    (is (not (nil? (some #(= "Sites" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))))

(deftest create-then-delete-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    (is (= 201 (:status create-node-response)))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id])))))))

(deftest copy-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create a folder
        new-parent-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})) [:body :entry :id])
        ;; copy node into the new folder
        copy-node-body (model/map->CopyNodeBody {:target-parent-id new-parent-id :name (.toString (UUID/randomUUID))})
        copy-node-response (nodes/copy-node ticket created-node-id copy-node-body)]
    ;; check if node has been copied
    (is (= (get-in copy-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket created-node-id))))
    (is (= 204 (:status (nodes/delete-node ticket new-parent-id))))))

(deftest lock-then-unlock-node
  (let
    [ticket (get-in (auth/create-ticket user password) [:body :entry])
     parent-id (:id (tu/get-guest-home ticket))
     ;; create a noe
     node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
     ;; lock the node
     lock-node-body (model/map->LockNodeBody {:time-to-expire 0
                                              :type           "ALLOW_OWNER_CHANGES"
                                              :lifetime       "PERSISTENT"})]
    (is (= 200 (:status (nodes/lock-node ticket node-id lock-node-body))))
    (let [properties (get-in (nodes/get-node ticket node-id) [:body :entry :properties])]
      ;; check if the node is locked
      (is (every? true? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; unlock the node
    (is (= 200 (:status (nodes/unlock-node ticket node-id))))
    (let [properties (get-in (nodes/get-node ticket node-id) [:body :entry :properties])]
      ;; check if the node is unlocked
      (is (every? false? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; clean up
    (is (= 204) (:status (nodes/delete-node ticket node-id)))))

(deftest move-node
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create a new folder
        new-parent-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})) [:body :entry :id])
        move-node-body (model/map->MoveNodeBody {:target-parent-id new-parent-id})
        ;; move node into the new folder
        move-node-response (nodes/move-node ticket created-node-id move-node-body)]
    ;; check if the node has been moved
    (is (= (get-in move-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket new-parent-id))))))

(deftest get-node-content
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
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
      (is (= (slurp (.getPath downloaded-file)) (apply str (map char (:body (nodes/get-node-content ticket node-id))))))
      ;;clean up
      (is (= 204 (:status (nodes/delete-node ticket node-id))))
      (io/delete-file file-to-be-uploaded)
      (io/delete-file downloaded-file))))

(deftest update-node-content
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    ;; update the node content
    (nodes/update-node-content ticket node-id file-to-be-uploaded)
    ;; check if the content is the same previously spit to the file
    (is (= file-content (apply str (map char (:body (nodes/get-node-content ticket node-id))))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id))))
    (io/delete-file file-to-be-uploaded)))

;; incomplete: missing list and delete secondary child/children
(deftest create-then-list-secondary-child
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create association
    (let [response (nodes/create-secondary-child ticket source-node-id [(model/map->CreateSecondaryChildBody {:child-id target-node-id :assoc-type "rn:rendition"})])]
      ;; FIXME check response return code
      ;; list secondary children
      (let [response (nodes/list-secondary-children ticket source-node-id)]
        (is (= 200 (:status response)))
        ;; check for target-node-id

        )
      ;; clean up
      (is (= 204 (:status (nodes/delete-node ticket source-node-id))))
      (is (= 204 (:status (nodes/delete-node ticket target-node-id))))
      response)))

(deftest list-parents
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        node-id (:id (tu/get-guest-home ticket))]
    (is (= "Company Home" (get-in (first (get-in (nodes/list-parents ticket node-id) [:body :list :entries])) [:entry :name])))))

(deftest create-then-list-then-delete-node-assocs
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create an association between source and target
    (is (= 201 (:status (nodes/create-node-assocs ticket source-node-id [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type "cm:references"})]))))
    ;; list associations
    (let [response (nodes/list-target-assocs ticket source-node-id (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"}))
          entry (:entry (first (get-in response [:body :list :entries])))]
      (is (= 200 (:status response)))
      (is (= "cm:references") (get-in entry [:association :assoc-type])))
    ;; delete the association
    (is (= 204 (:status (nodes/delete-node-assocs ticket source-node-id target-node-id))))
    ;; check if association has been deleted
    (let [response (nodes/list-target-assocs ticket source-node-id (model/map->ListTargetAssocsQueryParams {:where "(assocType='cm:references')"}))]
      (is (= 200 (:status response)))
      (is (empty? (get-in response [:body :list :pagination :entries]))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket source-node-id))))
    (is (= 204 (:status (nodes/delete-node ticket target-node-id))))))

(deftest create-then-list-then-delete-source-assocs
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create the source node
        source-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create the target node
        target-node-id (get-in (nodes/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create association
    (is (= 201 (:status (nodes/create-node-assocs ticket source-node-id [(model/map->CreateNodeAssocsBody {:target-id target-node-id :assoc-type "cm:references"})]))))
    ;; list associations
    (let [response (nodes/list-source-assocs ticket target-node-id (model/map->ListSourceAssocsQueryParams {:where "(assocType='cm:references')"}))
          entry (:entry (first (get-in response [:body :list :entries])))]
      (is (= 200 (:status response)))
      (is (= "cm:references") (get-in entry [:association :assoc-type])))
    ;; delete association
    (is (= 204 (:status (nodes/delete-node-assocs ticket source-node-id target-node-id))))
    ;; check if association has been deleted
    (let [response (nodes/list-source-assocs ticket source-node-id (model/map->ListSourceAssocsQueryParams {:where "(assocType='cm:references')"}))]
      (is (= 200 (:status response)))
      (is (empty? (get-in response [:body :list :pagination :entries]))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket source-node-id))))
    (is (= 204 (:status (nodes/delete-node ticket target-node-id))))))