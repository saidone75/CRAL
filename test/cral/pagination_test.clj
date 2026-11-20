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

(ns cral.pagination-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.core :as model]
            [cral.pagination :as pagination]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(use-fixtures :each fixtures/resources)

(deftest paginated-node-children-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        folder (nodes/create-node ticket (tu/get-guest-home ticket)
                                  {:name (.toString (UUID/randomUUID)) :node-type "cm:folder"})
        folder-id (get-in folder [:body :entry :id])
        names ["a.txt" "b.txt" "c.txt" "d.txt" "e.txt"]]
    (is (= (:status folder) 201))
    (when (= (:status folder) 201)
      (doseq [name names]
        (is (= (:status (nodes/create-node ticket folder-id {:name name :node-type "cm:content"})) 201)))
      (let [requests (atom [])
            fetch-page (fn [params]
                         (swap! requests conj params)
                         (nodes/list-node-children ticket folder-id params))
            params (model/map->ListNodeChildrenQueryParams
                     {:max-items 2 :order-by ["name ASC"] :fields ["id" "name"]
                      :where "(isFile=true)"})
            entries (pagination/entries fetch-page params)]
        ;; No requests until the sequence is consumed; one page is enough for take.
        (is (empty? @requests))
        (is (= (map #(get-in % [:entry :name]) (take 1 entries)) ["a.txt"]))
        (is (= (count @requests) 1))
        (is (= (map #(get-in % [:entry :name]) (doall entries)) names))
        (is (= (map :skip-count @requests) [0 2 4]))
        (is (every? #(= (dissoc % :skip-count) (dissoc params :skip-count)) @requests))
        ;; A nonzero offset is honored and a final empty page terminates normally.
        (is (= (map #(get-in % [:entry :name])
                    (pagination/entries fetch-page (assoc params :skip-count 1)))
               ["b.txt" "c.txt" "d.txt" "e.txt"]))
        (is (empty? (pagination/entries fetch-page (assoc params :skip-count 5)))))
      ;; An HTTP error is raised lazily, with the original response preserved.
      (let [entries (pagination/entries #(nodes/list-node-children ticket (.toString (UUID/randomUUID)) %))
            failure (try (doall entries) nil (catch clojure.lang.ExceptionInfo e e))]
        (is (instance? clojure.lang.ExceptionInfo failure))
        (is (= (get-in (ex-data failure) [:response :status]) 404))))))
