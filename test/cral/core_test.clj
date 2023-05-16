(ns cral.core-test
  (:import (java.util UUID))
  (:require [clojure.test :refer :all]
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
              [:list :entries]))))

(deftest get-ticket
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])]
    (is (not (nil? (:id ticket))))))

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

(deftest delete-node
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        parent-id (:id (get-guest-home))
        node-body-create (core/map->NodeBodyCreate {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (core/create-node ticket parent-id node-body-create) [:body :entry :id])]
    (is (= 204 (:status (core/delete-node ticket node-id))))))