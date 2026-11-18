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

(ns cral.model.search
  (:require [cral.model.body :refer [defbody]])
  (:import (clojure.lang PersistentVector)))

(defbody RequestQuery
  [^String language
   ^String user-query
   ^String query])

(defbody Paging
  [^Integer max-items
   ^Integer skip-count])

(defbody Sort
  [^String type
   ^String field
   ^Boolean ascending])

(defbody QueryBody
  [^RequestQuery query
   ^Paging paging
   ^PersistentVector include
   ^PersistentVector fields
   ^PersistentVector sort])

(defn make-query-body
  "Constructor for a simple AFTS query body with optional `paging` and ordered `sort` criteria.
  A single sort map or record is also accepted and wrapped in a vector."
  [^String query & [^Paging paging sort :as options]]
  (map->QueryBody (cond-> {:query (map->RequestQuery {:query query})}
                   (seq options) (assoc :paging paging)
                   (next options) (assoc :sort (if (map? sort) [sort] sort)))))
