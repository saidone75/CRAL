(ns cral.alfresco.core.nodes
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model CopyNodeBody
                                CopyNodeQueryParams
                                CreateNodeAssocsQueryParams
                                CreateNodeBody
                                CreateNodeQueryParams
                                DeleteNodeAssocsQueryParams
                                DeleteNodeQueryParams
                                GetNodeQueryParams
                                ListNodeChildrenQueryParams
                                ListParentsQueryParams
                                ListSourceAssocsQueryParams
                                ListTargetAssocsQueryParams
                                LockNodeBody
                                LockNodeQueryParams
                                MoveNodeBody
                                MoveNodeQueryParams
                                Ticket
                                UnLockNodeQueryParams
                                UpdateNodeBody
                                UpdateNodeContentQueryParams
                                UpdateNodeQueryParams)
           (java.io File)))

(defn get-node
  "Get node metadata."
  ([^Ticket ticket ^String node-id]
   (get-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^GetNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn update-node
  "Update a node."
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body]
   (update-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body ^UpdateNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn delete-node
  "Delete a node."
  ([^Ticket ticket ^String node-id]
   (delete-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^DeleteNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn list-node-children
  "List node children."
  ([^Ticket ticket ^String node-id]
   (list-node-children ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeChildrenQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/children" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn create-node
  "Create a node."
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body]
   (create-node ticket parent-id body nil))
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body ^CreateNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/children" (config/get-url 'core) parent-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn copy-node
  "Copy node."
  ([^Ticket ticket ^String node-id ^CopyNodeBody body]
   (copy-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^CopyNodeBody body ^CopyNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/copy" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn lock-node
  "Lock node."
  ([^Ticket ticket ^String node-id ^LockNodeBody body]
   (lock-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^LockNodeBody body ^LockNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/lock" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn unlock-node
  "Unlock a node."
  ([^Ticket ticket ^String node-id]
   (unlock-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^UnLockNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/unlock" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn move-node
  "Move node."
  ([^Ticket ticket ^String node-id ^MoveNodeBody body]
   (move-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^MoveNodeBody body ^MoveNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/move" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn get-node-content
  "Get node content."
  [^Ticket ticket ^String node-id]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
    ticket
    {:as :byte-array}
    true))

(defn update-node-content
  "Upload node content."
  ([^Ticket ticket ^String node-id ^File content]
   (update-node-content ticket node-id content nil))
  ([^Ticket ticket ^String node-id ^File content ^UpdateNodeContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
     ticket
     {:body         content
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn list-parents
  "List parents."
  ([^Ticket ticket ^String node-id]
   (list-parents ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListParentsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/parents" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn create-node-assocs
  "Create node associations."
  ([^Ticket ticket ^String node-id ^PersistentVector body]
   (create-node-assocs ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^PersistentVector body ^CreateNodeAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/targets" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn list-target-assocs
  "List target associations."
  ([^Ticket ticket ^String node-id]
   (list-target-assocs ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListTargetAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/targets" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn delete-node-assocs
  "Delete node associations."
  ([^Ticket ticket ^String node-id ^String target-id]
   (delete-node-assocs ticket node-id target-id nil))
  ([^Ticket ticket ^String node-id ^String target-id ^DeleteNodeAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s/targets/%s" (config/get-url 'core) node-id target-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn list-source-assocs
  "List source associations."
  ([^Ticket ticket ^String node-id]
   (list-source-assocs ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListSourceAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/sources" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))