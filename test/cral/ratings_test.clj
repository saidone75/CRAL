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

(ns cral.ratings-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.ratings :as ratings]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.alfresco.cm :as cm]
            [cral.model.alfresco.ratings :as rm]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(def saidone "saidone")

(deftest list-ratings-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; rate the node
        _ (->> (model/map->CreateRatingBody {:id rm/likes :my-rating true})
               (ratings/create-rating ticket created-node-id))
        _ (->> (model/map->CreateRatingBody {:id rm/five-star :my-rating 5})
               (ratings/create-rating saidone-ticket created-node-id))
        list-ratings-response (ratings/list-ratings ticket created-node-id)]
    (is (= (:status list-ratings-response) 200))
    (is (some #(= (get-in % [:entry :id]) rm/five-star) (get-in list-ratings-response [:body :list :entries])))
    (is (some #(= (get-in % [:entry :id]) rm/likes) (get-in list-ratings-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest create-rating-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; rate the node
        create-like-rating-response (->> (model/map->CreateRatingBody {:id rm/likes :my-rating true})
                                         (ratings/create-rating ticket created-node-id))
        create-five-star-rating-response (->> (model/map->CreateRatingBody {:id rm/five-star :my-rating 5})
                                              (ratings/create-rating saidone-ticket created-node-id))]
    (is (= (:status create-like-rating-response) 201))
    (is (= (get-in create-like-rating-response [:body :entry :id]) rm/likes))
    (is (= (:status create-five-star-rating-response) 201))
    (is (= (get-in create-five-star-rating-response [:body :entry :id]) rm/five-star))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest get-rating-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; rate the node
        _ (->> (model/map->CreateRatingBody {:id rm/likes :my-rating true})
               (ratings/create-rating ticket created-node-id))
        _ (->> (model/map->CreateRatingBody {:id rm/five-star :my-rating 5})
               (ratings/create-rating saidone-ticket created-node-id))]
    (is (= (:status (ratings/get-rating ticket created-node-id rm/likes)) 200))
    (is (= (:status (ratings/get-rating ticket created-node-id rm/five-star)) 200))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))

(deftest delete-rating-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a node
        created-node-id (->> (model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type cm/type-content})
                             (nodes/create-node ticket (tu/get-guest-home ticket))
                             (#(get-in % [:body :entry :id])))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; rate the node
        _ (->> (model/map->CreateRatingBody {:id rm/likes :my-rating true})
                                         (ratings/create-rating ticket created-node-id))
        _ (->> (model/map->CreateRatingBody {:id rm/five-star :my-rating 5})
                                              (ratings/create-rating saidone-ticket created-node-id))]
    (is (= (:status (ratings/delete-rating ticket created-node-id rm/likes)) 204))
    (is (= (:status (ratings/delete-rating ticket created-node-id rm/five-star)) 204))
    ;; clean up
    (is (= (:status (nodes/delete-node ticket created-node-id {:permanent true})) 204))))