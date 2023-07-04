(ns cral.alfresco.core.nodes
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CopyNodeBody
                                     CopyNodeQueryParams
                                     CreateNodeAssocsQueryParams
                                     CreateNodeBody
                                     CreateNodeQueryParams
                                     CreateSecondaryChildQueryParams
                                     DeleteNodeAssocsQueryParams
                                     DeleteNodeQueryParams
                                     DeleteSecondaryChildQueryParams
                                     GetNodeQueryParams
                                     ListNodeChildrenQueryParams
                                     ListParentsQueryParams
                                     ListSecondaryChildrenQueryParams
                                     ListSourceAssocsQueryParams
                                     ListTargetAssocsQueryParams
                                     LockNodeBody
                                     LockNodeQueryParams
                                     MoveNodeBody
                                     MoveNodeQueryParams
                                     UnLockNodeQueryParams
                                     UpdateNodeBody
                                     UpdateNodeContentQueryParams
                                     UpdateNodeQueryParams)
           (java.io File)))

(defn get-node
  "Get information for node node-id.
  You can use the **include** parameter to return additional information."
  ([^Ticket ticket ^String node-id]
   (get-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^GetNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-node
  "Updates the node **node-id**."
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body]
   (update-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^UpdateNodeBody body ^UpdateNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-node
  "Deletes the node node-id."
  ([^Ticket ticket ^String node-id]
   (delete-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^DeleteNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-node-children
  "Gets a list of children of the parent node node-id."
  ([^Ticket ticket ^String node-id]
   (list-node-children ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeChildrenQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/children" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-node
  "Create a node and add it as a primary child of node node-id."
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body]
   (create-node ticket parent-id body nil))
  ([^Ticket ticket ^String parent-id ^CreateNodeBody body ^CreateNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/children" (config/get-url 'core) parent-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn copy-node
  "Copies the node **node-id** to the parent folder node **target-parent-id**. You specify the **target-parent-id** in the request body.
  The new node has the same name as the source node unless you specify a new **name** in the request body.
  If the source **node-id** is a folder, then all of its children are also copied.
  If the source **node-id** is a file, its properties, aspects and tags are copied, its ratings, comments and locks are not."
  ([^Ticket ticket ^String node-id ^CopyNodeBody body]
   (copy-node ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^CopyNodeBody body ^CopyNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/copy" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

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
      :query-params query-params
      :content-type :json}
     opts)))

(defn unlock-node
  "Unlock a node."
  ([^Ticket ticket ^String node-id]
   (unlock-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^UnLockNodeQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/unlock" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params
      :content-type :json}
     opts)))

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
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-node-content
  "Get node content."
  [^Ticket ticket ^String node-id]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
    ticket
    {:as :byte-array}
    {:return-headers true}))

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
      :query-params query-params}
     opts)))

(defn create-secondary-child
  "Create secondary child."
  ([^Ticket ticket ^String node-id ^PersistentVector body]
   (create-secondary-child ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^PersistentVector body ^CreateSecondaryChildQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/secondary-children" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-secondary-children
  "List secondary children."
  ([^Ticket ticket ^String node-id]
   (list-node-children ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListSecondaryChildrenQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/secondary-children" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-secondary-child
  "Delete secondary child or children."
  ([^Ticket ticket ^String node-id ^String child-id]
   (delete-secondary-child ticket node-id child-id nil))
  ([^Ticket ticket ^String node-id ^String child-id ^DeleteSecondaryChildQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s/secondary-children/%s" (config/get-url 'core) node-id child-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-parents
  "List parents."
  ([^Ticket ticket ^String node-id]
   (list-parents ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListParentsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/parents" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

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
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-target-assocs
  "List target associations."
  ([^Ticket ticket ^String node-id]
   (list-target-assocs ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListTargetAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/targets" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-node-assocs
  "Delete node associations."
  ([^Ticket ticket ^String node-id ^String target-id]
   (delete-node-assocs ticket node-id target-id nil))
  ([^Ticket ticket ^String node-id ^String target-id ^DeleteNodeAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s/targets/%s" (config/get-url 'core) node-id target-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-source-assocs
  "List source associations."
  ([^Ticket ticket ^String node-id]
   (list-source-assocs ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListSourceAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/sources" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))
