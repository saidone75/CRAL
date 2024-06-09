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

(ns cral.renditions-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.renditions :as renditions]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)

(def ^:const content-file "Elkjaer_Briegel.jpg")

(deftest create-rendition-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".jpg") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; ask for rendition creation
        create-rendition-response (renditions/create-rendition ticket created-node-id [(model/map->CreateRenditionBody {:id "doclib"})])]
    (is (= (:status create-rendition-response) 202))
    ;; wait until rendition is status is "CREATED"
    (loop [get-rendition-info-response nil]
      (if (= (get-in get-rendition-info-response [:body :entry :status]) "CREATED")
        (is (= (:status get-rendition-info-response) 200))
        (do
          (Thread/sleep 1000)
          (recur (renditions/get-rendition-info ticket created-node-id "doclib")))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest list-renditions-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".jpg") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; ask for rendition creation
        _ (renditions/create-rendition ticket created-node-id [(model/map->CreateRenditionBody {:id "doclib"})])
        list-renditions-response (renditions/list-renditions ticket created-node-id)]
    (is (= (:status list-renditions-response) 200))
    (is (not (empty? (get-in list-renditions-response [:body :list :entries]))))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-rendition-info-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (str (.toString (UUID/randomUUID)) ".jpg") :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; update the node content
        _ (nodes/update-node-content ticket created-node-id (io/as-file (io/resource content-file)))
        ;; ask for rendition creation
        _ (renditions/create-rendition ticket created-node-id [(model/map->CreateRenditionBody {:id "doclib"})])
        get-rendition-info-response (renditions/get-rendition-info ticket created-node-id "doclib")]
    (is (= (:status get-rendition-info-response) 200))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))