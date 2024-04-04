(ns cral.favorites-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.favorites :as favorites]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.model.core]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest list-favorites-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket (:id (tu/get-guest-home ticket)) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; add node to favorites
    (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid created-node-id}})])
    ;; list favorites
    (let [list-favorites-response (favorites/list-favorites ticket user)]
      (is (= (:status list-favorites-response) 200))
      ;; count favorites
      (is (> (get-in list-favorites-response [:body :list :pagination :count]) 0))
      ;; check if node belongs to favorites
      (is (some true? (map #(= (get-in % [:entry :target-guid]) created-node-id) (get-in list-favorites-response [:body :list :entries])))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest create-favorite-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket (:id (tu/get-guest-home ticket)) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create favorite
    (is (= (:status (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid created-node-id}})])) 201))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-favorite-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket (:id (tu/get-guest-home ticket)) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; add node to favorites
        created-favorite-id (get-in (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid created-node-id}})]) [:body :entry :target-guid])]
    ;; get favorite
    (is (= (:status (favorites/get-favorite ticket "-me-" created-favorite-id)) 200))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-favorite-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (get-in (nodes/create-node ticket (:id (tu/get-guest-home ticket)) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; add node to favorites
        created-favorite-id (get-in (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid created-node-id}})]) [:body :entry :target-guid])]
    ;; delete favorite
    (is (= (:status (favorites/delete-favorite ticket "-me-" created-favorite-id)) 204))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))