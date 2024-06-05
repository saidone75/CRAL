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
           (cral.model.core ListAvailableActionsQueryParams
                            ListNodeActionsQueryParams)))

(defn list-node-actions
  "Retrieve the list of actions that may be executed against the given `node-id`.
  The default sort order for the returned list is for actions to be sorted by ascending name.
  You can override the default by using the **order-by** parameter in `query-params`.
  You can use any of the following fields to order the results:
  - name
  - title\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions/nodeActions)."
  ([^Ticket ticket ^String node-id]
   (list-node-actions ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeActionsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/action-definitions" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-available-actions
  "Gets a list of all available actions.
  The default sort order for the returned list is for actions to be sorted by ascending name.
  You can override the default by using the **order-by** parameter in `query-params`.
  You can use any of the following fields to order the results:
  - name
  - title\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions/listActions)."
  ([^Ticket ticket]
   (list-available-actions ticket nil))
  ([^Ticket ticket ^ListAvailableActionsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/action-definitions" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-action-definition-details
  "Retrieve the details of the action denoted by `action-definition-id`.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions/actionDetails)."
  [^Ticket ticket ^String action-definition-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/action-definitions/%s" (config/get-url 'core) action-definition-id)
    ticket
    nil
    opts))

