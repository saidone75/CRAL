;
;  CRAL
;  Copyright (C) 2023-2026 Saidone
;  SPDX-License-Identifier: GPL-3.0-or-later
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
;

(ns cral.search-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.search :as search]
            [cral.api.core.nodes :as nodes]
            [cral.test-utils :as tu]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.search :as search-model])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(use-fixtures :each fixtures/resources)

(deftest search-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        response
        (->> (search-model/map->RequestQuery {:query "PATH:'app:company_home'"})
             (#(search-model/map->QueryBody {:query %}))
             (#(search/search ticket %)))]
    (is (= (:status response) 200))
    (is (= (get-in (first (get-in response [:body :list :entries])) [:entry :name]) "Company Home"))))

(deftest search-simple-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        response (search/search ticket (search-model/make-query-body "PATH:'app:company_home'"))]
    (is (= (:status response) 200))
    (is (= (get-in (first (get-in response [:body :list :entries])) [:entry :name]) "Company Home"))))

(deftest search-sorting-and-paging-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        ;; create a dedicated folder and documents with two sorting groups
        folder-response (nodes/create-node ticket (tu/get-guest-home ticket)
                                           {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})
        folder-id (get-in folder-response [:body :entry :id])]
    (is (= (:status folder-response) 201))
    (when (= (:status folder-response) 201)
      (doseq [[name title] [["a.txt" "A"] ["b.txt" "A"] ["c.txt" "B"] ["d.txt" "B"]]]
        (is (= (:status (nodes/create-node ticket folder-id
                                          {:name name :node-type "cm:content"
                                           :aspect-names ["cm:titled"] :properties {"cm:title" title}})) 201)))
      (let [body (search-model/make-query-body
                   (format "PARENT:'workspace://SpacesStore/%s' AND TYPE:'cm:content'" folder-id)
                   (search-model/map->Paging {:max-items 2 :skip-count 1})
                   [(search-model/map->Sort {:type "FIELD" :field "cm:title" :ascending true})
                    (search-model/map->Sort {:type "FIELD" :field "cm:name" :ascending false})])
            ;; wait for all documents to become searchable
            response (tu/poll #(search/search ticket body)
                              #(= (get-in % [:body :list :pagination :total-items]) 4))]
        (is (= (:status response) 200))
        (is (= (get-in response [:body :list :pagination :skip-count]) 1))
        (is (= (get-in response [:body :list :pagination :count]) 2))
        (is (= (map #(get-in % [:entry :name]) (get-in response [:body :list :entries]))
               ["a.txt" "d.txt"]))))))
