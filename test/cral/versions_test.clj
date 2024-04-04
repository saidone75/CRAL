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
        created-node-id (get-in (nodes/create-node ticket (:id (tu/get-guest-home ticket)) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])]
    ;; list version history
    (is (= (:status (versions/list-version-history ticket created-node-id)) 200))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))