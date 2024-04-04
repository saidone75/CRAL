(ns cral.comments-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.comments :as comments]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.core]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest comments-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (:id (tu/get-guest-home ticket))
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        created-node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        comment-content (.toString (UUID/randomUUID))]
    ;; create comment
    (is (= (:status (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content comment-content})])) 201))
    ;; list comments and check content
    (let [list-comments-response (comments/list-comments ticket created-node-id)
          comment-entry (:entry (first (get-in list-comments-response [:body :list :entries])))]
      (is (= (:status list-comments-response) 200))
      (is (= comment-content (:content comment-entry))))
    ;; update comment
    (let [list-comments-response (comments/list-comments ticket created-node-id)
          comment-entry (:entry (first (get-in list-comments-response [:body :list :entries])))
          updated-comment-content (.toString (UUID/randomUUID))]
      (comments/update-comment ticket created-node-id (:id comment-entry) (model/map->UpdateCommentBody {:content updated-comment-content}))
      ;; check if comment has been updated
      (is (= updated-comment-content) (get-in (first (get-in (comments/list-comments ticket created-node-id) [:body :list :entries])) [:entry :content])))
    ;; delete comment
    (let [list-comments-response (comments/list-comments ticket created-node-id)
          comment-entry (:entry (first (get-in list-comments-response [:body :list :entries])))]
      (is (= (:status (comments/delete-comment ticket created-node-id (:id comment-entry))) 204)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

