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

(ns cral.api.core.versions
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)
           (cral.model.core GetVersionContentQueryParams
                            GetVersionRenditionContentQueryParams
                            ListVersionHistoryQueryParams
                            ListVersionRenditionsQueryParams
                            RevertVersionBody
                            RevertVersionQueryParams)))

(defn list-version-history
  "Gets the version history as an ordered list of versions for the specified `node-id`.
  The list is ordered in descending modified order. So the most recent version is first and the original version is last in the list.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/listVersionHistory)."
  ([^Ticket ticket ^String node-id]
   (list-version-history ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListVersionHistoryQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-version-information
  "Gets the version information for `version-id` of file node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/getVersion)."
  ([^Ticket ticket ^String node-id ^String version-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions/%s" (config/get-url 'core) node-id version-id)
     ticket
     nil
     opts)))

(defn delete-version
  "Delete the version identified by `version-id` and `node-id`.
  If the version is successfully deleted then the content and metadata for that versioned node will be deleted and will no longer appear in the version history. This operation cannot be undone.
  If the most recent version is deleted the live node will revert to the next most recent version.
  We currently do not allow the last version to be deleted. If you wish to clear the history then you can remove the **cm:versionable** aspect (via [[nodes/update-node]]) which will also disable versioning.
  In this case, you can re-enable versioning by adding back the **cm:versionable** aspect or using the version params (major-version and comment) on a subsequent file content update.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/deleteVersion)."
  [^Ticket ticket ^String node-id ^String version-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/versions/%s" (config/get-url 'core) node-id version-id)
    ticket
    nil
    opts))

(defn get-version-content
  "Gets the version content for `version-id` of file node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/getVersionContent)."
  ([^Ticket ticket ^String node-id ^String version-id]
   (get-version-content ticket node-id version-id nil))
  ([^Ticket ticket ^String node-id ^String version-id ^GetVersionContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions/%s/content" (config/get-url 'core) node-id version-id)
     ticket
     (merge {:as :byte-array} query-params)
     (merge {:return-headers true} opts))))

(defn revert-version
  "Attempts to revert the version identified by `version-id` and `node-id` to the live node.
  If the node is successfully reverted then the content and metadata for that versioned node will be promoted to the
  live node and a new version will appear in the version history.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/revertVersion)."
  ([^Ticket ticket ^String node-id ^String version-id ^RevertVersionBody body]
   (revert-version ticket node-id version-id body nil))
  ([^Ticket ticket ^String node-id ^String version-id ^RevertVersionBody body ^RevertVersionQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/versions/%s/revert" (config/get-url 'core) node-id version-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn create-version-rendition
  "An asynchronous request to create a rendition for version of file `node-id~ and `version-id`.
  The rendition is specified by name **id** in the request body:
  ```json
  {
    \"id\": \"doclib\"
  }
  ```
  Multiple names may be specified as a comma separated list or using a list format:
  ```json
  [
    {
      \"id\": \"doclib\"
    },
    {
     \"id\": \"avatar\"
    }
  ]
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/createVersionRendition)."
  ([^Ticket ticket ^String node-id ^String version-id ^PersistentVector body & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/versions/%s/renditions" (config/get-url 'core) node-id version-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params nil
      :content-type :json}
     opts)))

(defn list-version-renditions
  "Gets a list of the rendition information for each rendition of the version of file `node-id` and `version-id`,
  including the rendition id.
  Each rendition returned has a **status**: CREATED means it is available to view or download, NOT_CREATED means the
  rendition can be requested.
  You can use the **where** parameter in `query-params` to filter the returned renditions by status. For example, the
  following where clause will return just the CREATED renditions:
  ```json
  (status='CREATED')
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/listVersionRenditions)."
  ([^Ticket ticket ^String node-id ^String version-id]
   (list-version-renditions ticket node-id version-id nil))
  ([^Ticket ticket ^String node-id ^String version-id ^ListVersionRenditionsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions/%s/renditions" (config/get-url 'core) node-id version-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-version-rendition-info
  "Gets the rendition information for `rendition-id` of version of file `node-id` and `version-id`\\.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/getVersionRendition)."
  [^Ticket ticket ^String node-id ^String version-id ^String rendition-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/versions/%s/renditions/%s" (config/get-url 'core) node-id version-id rendition-id)
    ticket
    {:query-params nil}
    opts))

(defn delete-version-rendition
  "Delete the rendition for `rendition-id` of version `version-id` of `node-id`.
  If the rendition is successfully deleted then the content for that rendition node will be cleared.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions)."
  [^Ticket ticket ^String node-id ^String version-id ^String rendition-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/versions/%s/renditions/%s" (config/get-url 'core) node-id version-id rendition-id)
    ticket
    nil
    opts))

(defn get-version-rendition-content
  "Gets the rendition content for `rendition-id` of version of file `node-id` and `version-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/getVersionRenditionContent)."
  ([^Ticket ticket ^String node-id ^String version-id ^String rendition-id]
   (get-version-rendition-content ticket node-id version-id rendition-id nil))
  ([^Ticket ticket ^String node-id ^String version-id ^String rendition-id ^GetVersionRenditionContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions/%s/renditions/%s/content" (config/get-url 'core) node-id version-id rendition-id)
     ticket
     {:as           :byte-array
      :query-params query-params}
     (merge {:return-headers true} opts))))