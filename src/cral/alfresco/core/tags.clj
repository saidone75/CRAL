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

(ns cral.alfresco.core.tags
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateNodeTagBody
                                     CreateNodeTagQueryParams
                                     GetTagQueryParams
                                     ListNodeTagsQueryParams
                                     ListTagsQueryParams
                                     UpdateTagBody
                                     UpdateTagQueryParams)))

(defn list-node-tags
  "Gets a list of tags for node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/listTagsForNode)."
  ([^Ticket ticket ^String node-id]
   (list-node-tags ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeTagsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/tags" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-node-tag
  "Creates a tag on the node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/createTagForNode)."
  ([^Ticket ticket ^String node-id ^CreateNodeTagBody body]
   (create-node-tag ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^PersistentVector body ^CreateNodeTagQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/tags" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-node-tag
  "Deletes tag *tag-id* from node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/deleteTagFromNode)."
  ([^Ticket ticket ^String node-id ^String tag-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s/tags/%s" (config/get-url 'core) node-id tag-id)
     ticket
     {}
     opts)))

(defn list-tags
  "Gets a list of tags in this repository.You can use the *include* parameter in `query-params` to return additional *values* information.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/listTags)."
  ([^Ticket ticket]
   (list-tags ticket nil))
  ([^Ticket ticket ^ListTagsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/tags" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-tag
  "Get a specific tag with `tag-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/getTag)."
  ([^Ticket ticket ^String tag-id]
   (get-tag ticket tag-id nil))
  ([^Ticket ticket ^String tag-id ^GetTagQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/tags/%s" (config/get-url 'core) tag-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-tag
  "Updates the tag `tag-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/tags/updateTag)."
  ([^Ticket ticket ^String tag-id ^UpdateTagBody body]
   (update-tag ticket tag-id body nil))
  ([^Ticket ticket ^String tag-id ^UpdateTagBody body ^UpdateTagQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/tags/%s" (config/get-url 'core) tag-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))
