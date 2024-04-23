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

(ns cral.api.core.nodes
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)
           (cral.model.core CopyNodeBody
                            CopyNodeQueryParams
                            CreateNodeAssocsQueryParams
                            CreateNodeBody
                            CreateNodeQueryParams
                            CreateSecondaryChildQueryParams
                            DeleteNodeAssocsQueryParams
                            DeleteNodeQueryParams
                            DeleteSecondaryChildQueryParams
                            GetNodeContentQueryParams
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
  "Get information for node `node-id`. You can use the **include** parameter in `query-params` to return additional information.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/getNode)."
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
  "Update the node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/updateNode)."
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
  "Delete the node `node-id`. If **node-id** is a folder, then its children are also deleted.
  Deleted nodes are moved to the trashcan unless the **permanent** query parameter is set to **true**.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/deleteNode)."
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
  "Gets a list of children of the parent node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/listNodeChildren)."
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
  "Create a node and add it as a primary child of node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/createNode)."
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
  "Copy the node `node-id` to the parent folder node **target-parent-id**. The **target-parent-id** should be specified in the request `body`.
  The new node will have the same name as the source node unless a new **name** is specified in the request `body`.
  If the source `node-id` is a folder then all of its children are also copied.
  If the source `node-id` is a file then its properties, aspects and tags will be copied, its ratings, comments and locks will not.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/copyNode)."
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
  "Places a lock on node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/lockNode)."
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
  "Deletes a lock on `node node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/unlockNode)."
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
  "Move the node `node-id` to the parent folder node **target-parent-id**.
  The **target-parent-id** is specified in the in request `body`.
  The moved node retains its name unless you specify a new name in the request `body`.
  If the source `node-id` is a folder, then its children are also moved.
  The move will effectively change the primary parent.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/moveNode)."
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
  "Gets the content of the node with identifier `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/getNodeContent)."
  ([^Ticket ticket ^String node-id]
   (get-node-content ticket node-id nil))
  ([^Ticket ticket ^String node-id ^GetNodeContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
     ticket
     {:as           :byte-array
      :query-params query-params}
     (merge {:return-headers true} opts))))

(defn update-node-content
  "Updates the content of the node with identifier `node-id`.
  The **major-version** and **comment** in `query-params` can be used to control versioning behaviour.
  If the content is versionable then a new minor version is created by default.
  Optionally a new **name** that must be unique within the parent folder can also be specified in `query-params`.
  If specified and valid then this will rename the node. If invalid then an error is returned and the content is not updated.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/updateNodeContent)."
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
  "Create a secondary child association, with the given association type, between the parent `node-id` and a child node.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/createSecondaryChildAssociation)."
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
  "Gets a list of secondary child nodes that are associated with the current parent `node-id`, via a secondary child association.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/listSecondaryChildren)."
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
  "Delete secondary child associations between the parent `node-id` and child nodes for the given association type.
  If the association type is not specified, then all secondary child associations, of any type in the direction from parent to secondary child, will be deleted.
  The child will still have a primary parent and may still be associated as a secondary child with other secondary parents.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/deleteSecondaryChildAssociation)."
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
  "Gets a list of parent nodes that are associated with the current child `node-id`.
  The list includes both the primary parent and any secondary parents.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/listParents)."
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
  "Create an association, with the given association type, between the source `node-id` and a target node.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/createAssociation)."
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
  "Gets a list of target nodes that are associated with the current source `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/listTargetAssociations)."
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
  "Delete an association, or associations, from the source `node-id` to a target node for the given association type.
  If the association type is not specified, then all peer associations, of any type, in the direction from source to target, will be deleted.\\
  **Note:** After removal of the peer association, or associations, from source to target, the two nodes may still have peer associations in the other direction.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/deleteAssociation)."
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
  "Gets a list of source nodes that are associated with the current target `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/nodes/listSourceAssociations)."
  ([^Ticket ticket ^String node-id]
   (list-source-assocs ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListSourceAssocsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/sources" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))