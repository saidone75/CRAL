(ns cral.alfresco.core.trashcan
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core GetDeletedNodeQueryParams ListDeletedNodesQueryParams)))

(defn list-deleted-nodes
  "Gets a list of deleted nodes for the current user.
  If the current user is an administrator deleted nodes for all users will be returned.
  The list of deleted nodes will be ordered with the most recently deleted node at the top of the list.
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
  "Gets the specific deleted node **node-id**.
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
  "Permanently deletes the deleted node **node-id**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/deleteDeletedNode)."
  [^Ticket ticket ^String node-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/deleted-nodes/%s" (config/get-url 'core) node-id)
    ticket
    {}
    opts))