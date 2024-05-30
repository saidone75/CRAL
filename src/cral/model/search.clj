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

(ns cral.model.search
  (:import (clojure.lang PersistentVector)))

(defrecord RequestQuery
  [^String language
   ^String user-query
   ^String query])

(defrecord Paging
  [^Integer max-items
   ^Integer skip-count])

(defrecord Sort
  [^String type
   ^String field
   ^Boolean ascending])

(defrecord QueryBody
  [^RequestQuery query
   ^Paging paging
   ^PersistentVector include
   ^PersistentVector fields
   ^Sort sort])

(defn make-query-body
  "Constructor for a simple AFTS query body that takes only a `query` string with optional `paging` and `sort`."
  [^String query & [^Paging paging ^Sort sort]]
  (map->QueryBody {:query  (map->RequestQuery {:query query})
                   :paging paging
                   :sort   sort}))