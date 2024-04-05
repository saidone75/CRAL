(ns cral.versions-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.versions :as versions]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.model.core]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest list-version-history-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; list version history
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (is (= (:status version-history-response) 200))
      ;; check if history is empty
      (is (empty? (get-in version-history-response [:body :list :entries]))))
    ;; create 1.0 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names ["cm:versionable"]}))
    ;; list version history again
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (is (= (:status version-history-response) 200))
      ;; assert that history is not empty
      (is (not (empty? (get-in version-history-response [:body :list :entries])))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-version-information-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create 1.0 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names ["cm:versionable"]}))
    ;; get version history
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (when-not (empty? (get-in version-history-response [:body :list :entries]))
        ;; get version information
        (let [get-version-information-response (versions/get-version-information ticket created-node-id (get-in (first (get-in version-history-response [:body :list :entries])) [:entry :id]))]
          (is (= (:status get-version-information-response 200)))
          (is (= (get-in get-version-information-response [:body :entry :properties :cm:version-type]) "MAJOR")))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-version-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; create 1.0 version by adding cm:versionable and set cm:autoVersionOnUpdateProps to true
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:aspect-names ["cm:versionable"] :properties {"cm:autoVersionOnUpdateProps" true}}))
    ;; create 1.1 version
    (nodes/update-node ticket created-node-id (model/map->UpdateNodeBody {:properties {"cm:name" (.toString (UUID/randomUUID))}}))
    ;; count versions
    (is (= (count (get-in (versions/list-version-history ticket created-node-id) [:body :list :entries])) 2))
    ;; delete version 1.1
    (is (= (:status (versions/delete-version ticket created-node-id "1.1")) 204))
    ;; count versions
    (is (= (count (get-in (versions/list-version-history ticket created-node-id) [:body :list :entries])) 1))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))