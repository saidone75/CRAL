(ns cral.alfresco.core.tags
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateNodeTagBody
                                     CreateNodeTagQueryParams ListNodeTagsQueryParams)))

(defn list-node-tags
  "List tags for a node."
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
  "Create a tag for a node."
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
  "Delete a tag for a node."
  ([^Ticket ticket ^String node-id ^String tag-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s/tags/%s" (config/get-url 'core) node-id tag-id)
     ticket
     {}
     opts)))
