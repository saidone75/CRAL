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

(defn get-version-information
  "Gets the version information for **version-id** of file node **node-id**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/getVersion)."
  ([^Ticket ticket ^String node-id ^String version-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/versions/%s" (config/get-url 'core) node-id version-id)
     ticket
     nil
     opts)))

(defn delete-version
  "Delete the version identified by **version-id** and **node-id**.
  If the version is successfully deleted then the content and metadata for that versioned node will be deleted and will no longer appear in the version history. This operation cannot be undone.
  If the most recent version is deleted the live node will revert to the next most recent version.
  We currently do not allow the last version to be deleted. If you wish to clear the history then you can remove the \"cm:versionable\" aspect (via [[update-node]]) which will also disable versioning.
  In this case, you can re-enable versioning by adding back the \"cm:versionable\" aspect or using the version params (major-version and comment) on a subsequent file content update.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/versions/deleteVersion)."
  [^Ticket ticket ^String node-id ^String version-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/versions/%s" (config/get-url 'core) node-id version-id)
    ticket
    {}
    opts))