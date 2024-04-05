(ns cral.versions-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.core.versions :as versions]
            [cral.alfresco.model.core :as model]
            [cral.alfresco.model.core]
            [cral.test-utils :as tu])
  (:import (java.io File)
           (java.util UUID)))

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
    (let [;; create a temp file
          file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
      ;; update node content
      (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
      (nodes/update-node-content ticket created-node-id file-to-be-uploaded {:major-version true})
      (io/delete-file file-to-be-uploaded))
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
        created-node-id (get-in (nodes/create-node ticket (tu/get-guest-home ticket) (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})) [:body :entry :id])
        ;; create a temp file
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")]
    ;; update node content
    (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
    (nodes/update-node-content ticket created-node-id file-to-be-uploaded {:major-version true})
    (io/delete-file file-to-be-uploaded)
    ;; get version history
    (let [version-history-response (versions/list-version-history ticket created-node-id)]
      (when-not (empty? (get-in version-history-response [:body :list :entries]))
        ;; get version information
        (let [get-version-information-response (versions/get-version-information ticket created-node-id (get-in (first (get-in version-history-response [:body :list :entries])) [:entry :id]))]
          (is (= (:status get-version-information-response 200)))
          (is (= (get-in get-version-information-response [:body :entry :properties :cm:version-type]) "MAJOR")))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))