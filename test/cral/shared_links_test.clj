;  CRAL
;  Copyright (C) 2023-2024 Saidone
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

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

(deftest list-shared-link-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a shared link
        created-shared-link-id (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                    (shared-links/create-shared-link ticket)
                                    (#(get-in % [:body :entry :id])))
        ;; list shared links
        list-shared-link-response (loop [list-shared-link-response (shared-links/list-shared-links ticket)]
                                    (if (some #(= created-node-id %) (map #(get-in % [:entry :node-id]) (get-in list-shared-link-response [:body :list :entries])))
                                      list-shared-link-response
                                      (do
                                        (Thread/sleep 1000)
                                        (recur (shared-links/list-shared-links ticket)))))]
    (is (= (:status list-shared-link-response) 200))
    ;; clean up
    (is (= (:status (shared-links/delete-shared-link ticket created-shared-link-id)) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-shared-link-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a shared link
        created-shared-link-id (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                    (shared-links/create-shared-link ticket)
                                    (#(get-in % [:body :entry :id])))
        ;; get shared link
        get-shared-link-response (shared-links/get-shared-link created-shared-link-id)]
    (is (= (:status get-shared-link-response) 200))
    (is (= (get-in get-shared-link-response [:body :entry :node-id]) created-node-id))
    ;; clean up
    (is (= (:status (shared-links/delete-shared-link ticket created-shared-link-id)) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest delete-shared-link-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a shared link
        created-shared-link-id (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                    (shared-links/create-shared-link ticket)
                                    (#(get-in % [:body :entry :id])))]
    (is (= (get-in (shared-links/get-shared-link created-shared-link-id) [:body :entry :node-id]) created-node-id))
    ;; delete shared link
    (is (= (:status (shared-links/delete-shared-link ticket created-shared-link-id)) 204))
    ;; check if shared link has been deleted
    (is (= (:status (shared-links/get-shared-link created-shared-link-id)) 404))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))

(deftest get-shared-link-content-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".txt") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a temp file
        file-to-be-uploaded (File/createTempFile "tmp." ".txt")
        _ (spit file-to-be-uploaded (.toString (UUID/randomUUID)))
        ;; update node content
        _ (nodes/update-node-content ticket created-node-id file-to-be-uploaded)
        ;; create a shared link
        created-shared-link-id (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                    (shared-links/create-shared-link ticket)
                                    (#(get-in % [:body :entry :id])))
        get-shared-link-content-response (shared-links/get-shared-link-content created-shared-link-id)]
    (is (= (:status get-shared-link-content-response) 200))
    ;; check if the content is the same of the uploaded file
    (is (= (apply str (map char (:body get-shared-link-content-response))) (slurp (.getPath file-to-be-uploaded))))
    ;; clean up
    (is (= (:status (shared-links/delete-shared-link ticket created-shared-link-id)) 204))
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))
    (io/delete-file file-to-be-uploaded)))

(deftest email-shared-link-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create a shared link
        created-shared-link-id (->> (model/map->CreateSharedLinkBody {:node-id created-node-id})
                                    (shared-links/create-shared-link ticket)
                                    (#(get-in % [:body :entry :id])))
        ;; email shared link
        email-shared-link-response (->> (model/map->EmailSharedLinkBody {:client "share" :recipient-emails ["saidone@saidone.org"]})
                                        (shared-links/email-shared-link ticket created-shared-link-id))]
    (is (= (:status email-shared-link-response) 202))
    ;; clean up
    (is (= (:status (shared-links/delete-shared-link ticket created-shared-link-id))) 204)
    (is (= (:status (nodes/delete-node ticket created-node-id)) 204))))