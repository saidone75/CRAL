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

(ns cral.api.core.trashcan
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.auth]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core GetDeletedNodeContentQueryParams
                            GetDeletedNodeQueryParams
                            GetDeletedNodeRenditionContentQueryParams
                            ListDeletedNodeRenditionsQueryParams
                            ListDeletedNodesQueryParams
                            RestoreDeletedNodeBody
                            RestoreDeletedNodeQueryParams)))

(defn list-deleted-nodes
  "Gets a list of deleted nodes for the current user.
  If the current user is an administrator deleted nodes for all users will be returned.
  The list of deleted nodes will be ordered with the most recently deleted node at the top of the list.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/listDeletedNodes)."
  ([^Ticket ticket]
   (list-deleted-nodes ticket nil))
  ([^Ticket ticket ^ListDeletedNodesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-deleted-node
  "Gets the specific deleted node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/getDeletedNode)."
  ([^Ticket ticket ^String node-id]
   (get-deleted-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^GetDeletedNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-deleted-node
  "Permanently deletes the deleted node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/deleteDeletedNode)."
  [^Ticket ticket ^String node-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/deleted-nodes/%s" (config/get-url 'core) node-id)
    ticket
    nil
    opts))

(defn get-deleted-node-content
  "Gets the content of the deleted node with identifier `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/getDeletedNodeContent)."
  ([^Ticket ticket ^String node-id]
   (get-deleted-node-content ticket node-id nil {:return-headers true}))
  ([^Ticket ticket ^String node-id ^GetDeletedNodeContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes/%s/content" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params :as :byte-array}
     opts)))

(defn restore-deleted-node
  "Attempts to restore the deleted node `node-id` to its original location or to a new location.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/restoreDeletedNode)."
  ([^Ticket ticket ^String node-id ^RestoreDeletedNodeBody body]
   (restore-deleted-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^RestoreDeletedNodeBody body ^RestoreDeletedNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/deleted-nodes/%s/restore" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-deleted-node-renditions
  "Gets a list of the rendition information for each rendition of the file `node-id`, including the rendition id.
  Each rendition returned has a status: CREATED means it is available to view or download, NOT_CREATED means the
  rendition can be requested.
  You can use the **where** parameter in `query-params` to filter the returned renditions by status.
  For example, the following **where** clause will return just the CREATED renditions:
  ```
  (status='CREATED')
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/listDeletedNodeRenditions)."
  ([^Ticket ticket ^String node-id]
   (list-deleted-node-renditions ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListDeletedNodeRenditionsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes/%s/renditions" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-deleted-node-rendition-info
  "Gets the rendition information for `rendition-id` of file `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/getArchivedNodeRendition)."
  [^Ticket ticket ^String node-id ^String rendition-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/deleted-nodes/%s/renditions/%s" (config/get-url 'core) node-id rendition-id)
    ticket
    {:query-params nil}
    opts))

(defn get-deleted-node-rendition-content
  "Gets the rendition content for `rendition-id` of file `node-id`.\\
  More info [here](http://localhost:8080/api-explorer/#/trashcan/getArchivedNodeRenditionContent)."
  ([^Ticket ticket ^String node-id ^String rendition-id]
   (get-deleted-node-rendition-content ticket node-id rendition-id nil))
  ([^Ticket ticket ^String node-id ^String rendition-id ^GetDeletedNodeRenditionContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes/%s/renditions/%s/content" (config/get-url 'core) node-id rendition-id)
     ticket
     {:as           :byte-array
      :query-params query-params}
     (merge {:return-headers true} opts))))