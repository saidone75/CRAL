(ns cral.alfresco.core.versions
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListVersionHistoryQueryParams)))

(defn list-version-history
  "Gets the version history as an ordered list of versions for the specified **node-id**.
  The list is ordered in descending modified order. So the most recent version is first and the original version is last in the list.
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