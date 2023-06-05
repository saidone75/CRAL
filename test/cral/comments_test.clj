(ns cral.comments-test
  (:import (java.util UUID))
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.comments :as comments]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model :as model]
            [cral.alfresco.search :as search]
            [taoensso.timbre :as timbre]))

(def user "admin")
(def pass "admin")

(timbre/set-config! {:min-level :info})

;; TODO put in an utility ns
(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (model/map->Ticket (get-in (auth/create-ticket user pass) [:body :entry]))
                    search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))

(deftest test-comments
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-id (:id (get-guest-home))
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
        created-node-id (get-in (nodes/create-node ticket parent-id create-node-body) [:body :entry :id])
        comment-content (.toString (UUID/randomUUID))]
    ;; create comment
    (is (= 201 (:status (comments/create-comment ticket created-node-id [(model/map->CreateCommentBody {:content comment-content})]))))
    ;; list comments and check content
    (let [list-comments-response (comments/list-comments ticket created-node-id)
          comment-entry (:entry (first (get-in list-comments-response [:body :list :entries])))]
      (is (= 200 (:status list-comments-response)))
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
      (is (= 204 (:status (comments/delete-comment ticket created-node-id (:id comment-entry))))))
    ;; clean up
    (is (= 204 (:status (nodes/delete-node ticket created-node-id))))))

