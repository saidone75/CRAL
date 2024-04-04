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

(deftest favorites-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])]
    ;; add node to favorites
    (let [favorite-id (get-in (favorites/create-favorite ticket "-me-" [(model/->CreateFavoriteBody {:file {:guid node-id}})]) [:body :entry :target-guid])]
      ;; get favorite
      (is (= (:status (favorites/get-favorite ticket "-me-" favorite-id)) 200))
      ;; list favorites
      (let [list-favorites-response (favorites/list-favorites ticket user)]
        (is (= (:status list-favorites-response) 200))
        ;; count favorites
        (is (> (get-in list-favorites-response [:body :list :pagination :count]) 0))
        ;; check if node belongs to favorites
        (is (some true? (map #(= (get-in % [:entry :target-guid]) node-id) (get-in list-favorites-response [:body :list :entries])))))
      ;; delete favorite
      (is (= (:status (favorites/delete-favorite ticket "-me-" favorite-id)) 204)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket node-id)) 204))))