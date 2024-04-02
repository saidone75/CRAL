(ns cral.alfresco.core.trashcan
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core GetDeletedNodesQueryParams)))

(defn list-deleted-nodes
  "Gets a list of deleted nodes for the current user.
  If the current user is an administrator deleted nodes for all users will be returned.
  The list of deleted nodes will be ordered with the most recently deleted node at the top of the list.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/trashcan/listDeletedNodes)."
  ([^Ticket ticket]
   (list-deleted-nodes ticket nil))
  ([^Ticket ticket ^GetDeletedNodesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/deleted-nodes" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))