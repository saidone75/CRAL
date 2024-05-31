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

(ns cral.api.core.actions
  (:require [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core RetrieveNodeActionsQueryParams)))

(defn retrieve-node-actions
  ([^Ticket ticket ^String node-id]
   (retrieve-node-actions ticket node-id nil))
  ([^Ticket ticket ^String node-id ^RetrieveNodeActionsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/action-definitions" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))