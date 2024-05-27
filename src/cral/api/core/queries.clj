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

(ns cral.api.core.queries
  (:require [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core FindNodesQueryParams)))

(defn find-nodes
  "Gets a list of nodes that match the given search criteria.
  The search term is used to look for nodes that match against name, title, description, full text content or tags.
  The search term:
  - must contain a minimum of 3 alphanumeric characters
  - allows \"quoted term\"
  - can optionally use '*' for wildcard matching\n\n
  By default, file and folder types will be searched unless a specific type is provided as a query parameter.
  By default, the search will be across the repository unless a specific root node id is provided to start the search from.
  You can sort the result list using the **order-by** parameter in `query-params`.
  You can specify one or more of the following fields in the **order-by** parameter:
  - name
  - modifiedAt
  - createdAt\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/queries/findNodes)."
  ([^Ticket ticket ^FindNodesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/queries/nodes" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))