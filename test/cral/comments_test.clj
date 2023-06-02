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
        create-node-response (nodes/create-node ticket parent-id create-node-body)]

    (comments/list-comments ticket (get-in create-node-response [:body :entry :id]))
    )
  )

(deftest create-comment
  ;; TODO
  )

