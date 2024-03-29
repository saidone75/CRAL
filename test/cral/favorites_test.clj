(ns cral.favorites_test
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

(deftest list-favorites
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    ;; list favorites
    (is (= 200 (:status (favorites/list-favorites ticket user))))))

(deftest create-then-get-then-list-then-delete-favorites
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])]
    ;; add node to favorites
    (let [favorite-id (get-in (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid node-id}})]) [:body :entry :target-guid])]
      ;; get favorite
      (is (= 200 (:status (favorites/get-favorite ticket "-me-" favorite-id))))
      (let [favorites (favorites/list-favorites ticket user)]
        ;; count favorites
        (is (> (get-in favorites [:body :list :pagination :count]) 0))
        ;; check if node belongs to favorites
        (is (some true? (map #(= (get-in % [:entry :target-guid]) node-id) (get-in favorites [:body :list :entries])))))
      ;; delete favorite
      (is (= 204 (:status (favorites/delete-favorite ticket "-me-" favorite-id)))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket node-id))))))