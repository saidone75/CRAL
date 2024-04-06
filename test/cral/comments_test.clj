(ns cral.comments-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.comments :as comments]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest list-comments-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create comment
        created-comment-id (get-in (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content (.toString (UUID/randomUUID))})]) [:body :entry :id])]
    ;; list comments
    (let [list-comments-response (comments/list-comments ticket created-node-id)]
      (is (= (:status list-comments-response) 200))
      (is (= (get-in (first (get-in list-comments-response [:body :list :entries])) [:entry :id]) created-comment-id)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest create-comment-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])]
    ;; create comment
    (is (= (:status (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content (.toString (UUID/randomUUID))})])) 201))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest update-comment-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create comment
        created-comment-id (get-in (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content (.toString (UUID/randomUUID))})]) [:body :entry :id])]
    (let [updated-comment-content (.toString (UUID/randomUUID))]
      ;; update comment
      (is (= (:status (comments/update-comment ticket created-node-id created-comment-id (model/map->UpdateCommentBody {:content updated-comment-content}))) 200))
      ;; check if comment has been updated
      (is (= updated-comment-content) (get-in (first (get-in (comments/list-comments ticket created-node-id) [:body :list :entries])) [:entry :content])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-comment-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create node
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})) [:body :entry :id])
        ;; create comment
        created-comment-id (get-in (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content (.toString (UUID/randomUUID))})]) [:body :entry :id])]
    ;; delete comment
    (is (= (:status (comments/delete-comment ticket created-node-id created-comment-id)) 204))
    ;; check if comment list is empty
    (is (= (get-in (comments/list-comments ticket created-node-id) [:body :list :pagination :count]) 0))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))