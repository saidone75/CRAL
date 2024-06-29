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
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core ExecuteActionBody
                            ListAvailableActionsQueryParams
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

(defn get-parameter-constraint
  "Gets action parameter constraint by requested name.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions)."
  [^Ticket ticket ^String parameter-constraint-name & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/action-parameter-constraint/%s" (config/get-url 'core) parameter-constraint-name)
    ticket
    {:query-params nil}
    opts))

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
  "Retrieve the details of the action denoted by `action-definition-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions/actionDetails)."
  [^Ticket ticket ^String action-definition-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/action-definitions/%s" (config/get-url 'core) action-definition-id)
    ticket
    nil
    opts))

(defn execute-action
  "Executes an action.
  An action may be executed against a node specified by **target-id**. For example:
  ```json
  {
    \"action-definition-id\": \"copy\",
    \"target-id\": \"4c4b3c43-f18b-43ff-af84-751f16f1ddfd\",
    \"params\": {
      \"destination-folder\": \"34219f79-66fa-4ebf-b371-118598af898c\"
    }
  }
  ```
  Performing a POST with the request body shown above will result in the node identified by **target-id** being copied
  to the destination folder specified in the **params** object by the key **destination-folder**.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/actions/actionExec)."
  [^Ticket ticket ^ExecuteActionBody body & [^PersistentHashMap opts]]
  (utils/call-rest
    client/post
    (format "%s/action-executions" (config/get-url 'core))
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys body #{:destination-folder}))
     :query-params nil
     :content-type :json}
    opts))