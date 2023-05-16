(ns cral.core-test
  (:import (java.util UUID)
           (java.io File))
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [cral.core :refer :all]
            [cral.alfresco.core :as core]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]))

(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
                    search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))

(deftest get-ticket
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])]
    (is (not (nil? (:id ticket))))))

;; FIXME
(let [node-id "75edc814-d2fe-41bc-99b6-fd953f1ddb15"
      ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
      response (core/get-node-content ticket node-id)
      file-name (second (re-matches #".*\"([^\"]+)\".*" (second (str/split (get-in response [:headers "content-disposition"]) #";"))))
      file (File. (str (System/getProperty "java.io.tmpdir") "/" file-name))]
  (with-open [w (clojure.java.io/output-stream file)]
    (.write w (:body response)))
  (println (.getPath file)))

(deftest create-node
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (core/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})]
    (is (= 201) (:status (core/create-node ticket parent-id node-body-create)))))

(deftest update-node
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (core/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])
        new-name (.toString (UUID/randomUUID))]
    (core/update-node ticket node-id (core/map->NodeBodyUpdate {:name new-name}))
    (is (= new-name (get-in (core/get-node ticket node-id) [:body :entry :name])))))

(deftest update-content
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (core/map->NodeBodyCreate {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])
        content (File/createTempFile "tmp." ".txt")]
    (spit content "hello")
    (core/update-node-content ticket node-id content)))

(deftest delete-node
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (core/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])]
    (is (= 204 (:status (core/delete-node ticket node-id))))))