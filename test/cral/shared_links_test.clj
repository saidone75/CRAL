(ns cral.shared-links-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.shared-links :as shared-links]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core]
            [cral.alfresco.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

(def user "admin")
(def password "admin")

(deftest create-shared-link-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a shared link
        create-shared-link-response (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                         (shared-links/create-shared-link ticket))]
    (is (= (:status create-shared-link-response) 201))
    (is (= (get-in create-shared-link-response [:body :entry :node-id]) created-node-id))
    ;; clean up
    (is (= (:status (shared-links/delete-shared-link ticket (get-in create-shared-link-response [:body :entry :id]))) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

;; old test
(deftest create-then-list-then-get-then-get-content-then-email-then-delete-shared-link
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        parent-id (tu/get-guest-home ticket)
        ;; create a node
        create-node-body (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
        create-node-response (nodes/create-node ticket parent-id create-node-body)]
    (is (= (:status create-node-response) 201))
    (let [create-shared-link-body (model/map->CreateSharedLinkBody {:node-id (get-in create-node-response [:body :entry :id])})
          ;; create a shared link
          create-shared-link-response (shared-links/create-shared-link ticket create-shared-link-body)]
      (= (:status create-shared-link-response) 200)
      ;; list shared links
      (loop [list-shared-link-response (shared-links/list-shared-links ticket)]
        (when-not (some #(= (get-in create-node-response [:body :entry :id]) %) (map #(get-in % [:entry :node-id]) (get-in list-shared-link-response [:body :list :entries])))
          (Thread/sleep 1000)
          (recur (shared-links/list-shared-links ticket))))
      ;; get shared link
      (is (= (:status (shared-links/get-shared-link (get-in create-shared-link-response [:body :entry :id]))) 200))
      ;; update node content
      (let [file-to-be-uploaded (File/createTempFile "tmp." ".txt")
            file-content (.toString (UUID/randomUUID))]
        (spit file-to-be-uploaded file-content)
        (nodes/update-node-content ticket (get-in create-node-response [:body :entry :id]) file-to-be-uploaded)
        ;; get shared link content
        (let [content (shared-links/get-shared-link-content (get-in create-shared-link-response [:body :entry :id]))]
          (is (= (apply str (map char (:body content))) file-content)))
        ;; delete temp file
        (io/delete-file file-to-be-uploaded))
      ;; email shared link
      (is (= (:status (shared-links/email-shared-link ticket (get-in create-shared-link-response [:body :entry :id])
                                                      (model/map->EmailSharedLinkBody {:client "share" :recipient-emails ["saidone@saidone.org"]}))) 202))
      ;; delete shared link
      (is (= (:status (shared-links/delete-shared-link ticket (get-in create-shared-link-response [:body :entry :id]))) 204)))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket (get-in create-node-response [:body :entry :id]))) 204))))