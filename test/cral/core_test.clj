(ns cral.core-test
  (:import (java.util UUID)
           (java.io File))
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [cral.core :refer :all]
            [cral.alfresco.model :as model]
            [cral.alfresco.core :as core]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]))

(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (model/map->Ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry]))
                    search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))

(deftest get-node
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        guest-home-id (:id (get-guest-home))]
    ;; well known fields for query parameters defined in core/QueryParamsGetNode
    (core/get-node ticket guest-home-id (model/map->GetNodeQueryParams {:include ["path" "permissions"]}))
    ;; but plain maps can be used as well
    (core/get-node ticket guest-home-id {:include ["path" "permissions"]})))

(deftest update-node
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (model/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])
        new-name (.toString (UUID/randomUUID))]
    (core/update-node ticket node-id (model/map->NodeBodyUpdate {:name new-name}))
    (is (= new-name (get-in (core/get-node ticket node-id) [:body :entry :name])))
    ;; clean up
    (core/delete-node ticket node-id)))

(deftest delete-node
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (model/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])]
    (is (= 204 (:status (core/delete-node ticket node-id))))))

(deftest list-node-children
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        company-home-id (get-in (first (get-in (search/search ticket (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home'"})})) [:body :list :entries])) [:entry :id])
        list-node-children-response (core/list-node-children ticket company-home-id)]
    (is (= 200 (:status list-node-children-response)))
    (is (not (nil? (some #(= "Data Dictionary" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))
    (is (not (nil? (some #(= "Sites" (:name %)) (map :entry (get-in list-node-children-response [:body :list :entries]))))))))

(deftest create-node
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (model/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        create-node-response (core/create-node ticket parent-id node-body-create)]
    (is (= 201) (:status create-node-response))
    ;; clean up
    (core/delete-node ticket (get-in create-node-response [:body :entry :id]))))

(deftest get-node-content
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (model/map->NodeBodyCreate {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded "hello")
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
      (core/delete-node ticket node-id)
      (io/delete-file file-to-be-uploaded)
      (io/delete-file downloaded-file))))

(deftest update-node-content
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (model/map->NodeBodyCreate {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    (spit file-to-be-uploaded "hello")
    (core/update-node-content ticket node-id file-to-be-uploaded)
    (is (= "hello" (apply str (map char (:body (core/get-node-content ticket node-id))))))
    ;; clean up
    (core/delete-node ticket node-id)
    (io/delete-file file-to-be-uploaded)))

(deftest list-parents
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        node-id (:id (get-guest-home))]
    (is (= "Company Home" (get-in (first (get-in (core/list-parents ticket node-id) [:body :list :entries])) [:entry :name])))))