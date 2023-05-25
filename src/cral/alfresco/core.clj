(ns cral.alfresco.core
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (cral.alfresco.model CopyNodeQueryParams CreateNodeQueryParams
                                DeleteNodeQueryParams
                                GetNodeQueryParams
                                ListNodeChildrenQueryParams
                                ListParentsQueryParams
                                CopyNodeBody CreateNodeBody
                                UpdateNodeBody
                                Ticket
                                UpdateNodeContentQueryParams
                                UpdateNodeQueryParams)
           (java.io File)))

(defn get-node
  "Get node metadata."
  ([^Ticket ticket ^String node-id]
   (get-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^GetNodeQueryParams query-params]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn update-node
  "Update a node."
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body]
   (update-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body ^UpdateNodeQueryParams query-params]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json})))

(defn delete-node
  "Delete a node."
  ([^Ticket ticket ^String node-id]
   (delete-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^DeleteNodeQueryParams query-params]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn list-node-children
  "List node children."
  ([^Ticket ticket ^String node-id]
   (list-node-children ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeChildrenQueryParams query-params]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/children" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn create-node
  "Create a node."
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body]
   (create-node ticket parent-id body nil))
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body ^CreateNodeQueryParams query-params]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/children" (config/get-url 'core) parent-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json})))

(defn copy-node
  "Copy node."
  ([^Ticket ticket ^String node-id ^CopyNodeBody body]
   (copy-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^CopyNodeBody body ^CopyNodeQueryParams query-params]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/copy" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json})))

(defn get-node-content
  "Get node content."
  [^Ticket ticket ^String node-id]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
    ticket
    {:as :byte-array}))

(defn update-node-content
  "Upload node content."
  ([^Ticket ticket ^String node-id ^File content]
   (update-node-content ticket node-id content nil))
  ([^Ticket ticket ^String node-id ^File content ^UpdateNodeContentQueryParams query-params]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
     ticket
     {:body         content
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn list-parents
  "List parents."
  ([^Ticket ticket ^String node-id]
   (list-parents ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListParentsQueryParams query-params]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/parents" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))