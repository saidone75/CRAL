(ns cral.core-test
  (:import (java.util UUID)
           (java.io File))
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [cral.core :refer :all]
            [cral.alfresco.model :as model]
            [cral.alfresco.core :as core]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]))

(def user "admin")
(def pass "admin")

(timbre/set-config! {:min-level :info})

(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (model/map->Ticket (get-in (auth/create-ticket user pass) [:body :entry]))
                    search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))

(deftest get-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        guest-home-id (:id (get-guest-home))]
    ;; well known fields for query parameters defined in core/QueryParamsGetNode
    (is (every? true? (map (partial contains? (get-in (core/get-node ticket guest-home-id (model/map->GetNodeQueryParams {:include ["path" "permissions"]})) [:body :entry])) [:path :permissions])))
    ;; but plain maps can be used as well
    (is (every? true? (map (partial contains? (get-in (core/get-node ticket guest-home-id {:include ["path" "permissions"]}) [:body :entry])) [:path :permissions])))))

(deftest update-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id create-node-body) [:body :entry :id])
        new-name (.toString (UUID/randomUUID))]
    (core/update-node ticket node-id (model/map->UpdateNodeBody {:name new-name}))
    (is (= new-name (get-in (core/get-node ticket node-id) [:body :entry :name])))
    ;; clean up
    (is (= 204 (:status (core/delete-node ticket node-id))))))

(deftest delete-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id create-node-body) [:body :entry :id])]
    (is (= 204 (:status (core/delete-node ticket node-id))))))

(deftest list-node-children
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        company-home-id (get-in (first (get-in (search/search ticket (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home'"})})) [:body :list :entries])) [:entry :id])
        list-node-children-response (core/list-node-children ticket company-home-id)]
    (is (= 200 (:status list-node-children-response)))
    (is (not (nil? (some #(= "Data Dictionary" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))
    (is (not (nil? (some #(= "Sites" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))))

(deftest create-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (core/create-node ticket parent-id create-node-body)]
    (is (= 201 (:status create-node-response)))
    ;; clean up
    (is (= 204 (:status (core/delete-node ticket (get-in create-node-response [:body :entry :id])))))))

(deftest copy-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        created-node-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        new-parent-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})) [:body :entry :id])
        copy-node-body (model/map->CopyNodeBody {:target-parent-id new-parent-id :name (.toString (UUID/randomUUID))})
        copy-node-response (core/copy-node ticket created-node-id copy-node-body)]
    ;; check if node has been copied
    (is (= (get-in copy-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= 204 (:status (core/delete-node ticket created-node-id))))
    (is (= 204 (:status (core/delete-node ticket new-parent-id))))))

(deftest lock-node
  (let
    [ticket (get-in (auth/create-ticket user pass) [:body :entry])
     parent-id (:id (get-guest-home))
     node-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
     lock-node-body (model/map->LockNodeBody {:time-to-expire 0
                                              :type           "ALLOW_OWNER_CHANGES"
                                              :lifetime       "PERSISTENT"})]
    (is (= 200 (:status (core/lock-node ticket node-id lock-node-body))))
    (let [properties (get-in (core/get-node ticket node-id) [:body :entry :properties])]
      ;; check if node is locked
      (is (every? true? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; clean up
    (is (= 204) (:status (core/delete-node ticket node-id)))))

(deftest unlock-node
  (let
    [ticket (get-in (auth/create-ticket user pass) [:body :entry])
     parent-id (:id (get-guest-home))
     node-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
     lock-node-body (model/map->LockNodeBody {:time-to-expire 0
                                              :type           "ALLOW_OWNER_CHANGES"
                                              :lifetime       "PERSISTENT"})]
    (is (= 200 (:status (core/lock-node ticket node-id lock-node-body))))
    (let [properties (get-in (core/get-node ticket node-id) [:body :entry :properties])]
      ;; check if node is locked
      (is (every? true? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; unlock node
    (is (= 200 (:status (core/unlock-node ticket node-id))))
    (let [properties (get-in (core/get-node ticket node-id) [:body :entry :properties])]
      ;; check if node is unlocked
      (is (every? false? (map (partial contains? properties) [:cm:lock-type :cm:lock-owner :cm:lock-lifetime]))))
    ;; clean up
    (is (= 204) (:status (core/delete-node ticket node-id)))))

(deftest move-node
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        created-node-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        new-parent-id (get-in (core/create-node ticket parent-id (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})) [:body :entry :id])
        move-node-body (model/map->MoveNodeBody {:target-parent-id new-parent-id})
        move-node-response (core/move-node ticket created-node-id move-node-body)]
    ;; check if node has been moved
    (is (= (get-in move-node-response [:body :entry :parent-id]) new-parent-id))
    ;; clean up
    (is (= 204 (:status (core/delete-node ticket new-parent-id))))))

(deftest get-node-content
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id create-node-body) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
    (core/update-node-content ticket node-id file-to-be-uploaded)
    (let [response (core/get-node-content ticket node-id)
          downloaded-file (->> response
                               (#(get-in % [:headers "content-disposition"]))
                               (#(second (re-matches #".*\"([^\"]+)\".*" (second (str/split % #";")))))
                               (#(File. (str (System/getProperty "java.io.tmpdir") "/" %)))
                               (#(with-open [w (clojure.java.io/output-stream %)]
                                   (.write w (bytes (:body response)))
                                   %)))]
      (is (= (slurp (.getPath downloaded-file)) (apply str (map char (:body (core/get-node-content ticket node-id))))))
      ;;clean up
      (is (= 204 (:status (core/delete-node ticket node-id))))
      (io/delete-file file-to-be-uploaded)
      (io/delete-file downloaded-file))))

(deftest update-node-content
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id create-node-body) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        file-content (.toString (UUID/randomUUID))]
    (spit file-to-be-uploaded file-content)
    (core/update-node-content ticket node-id file-to-be-uploaded)
    (is (= file-content (apply str (map char (:body (core/get-node-content ticket node-id))))))
    ;; clean up
    (is (= 204 (:status (core/delete-node ticket node-id))))
    (io/delete-file file-to-be-uploaded)))

(deftest list-parents
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        node-id (:id (get-guest-home))]
    (is (= "Company Home" (get-in (first (get-in (core/list-parents ticket node-id) [:body :list :entries])) [:entry :name])))))

(deftest create-node-assoc)