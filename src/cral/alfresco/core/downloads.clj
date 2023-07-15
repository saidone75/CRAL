(ns cral.alfresco.core.downloads
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateDownloadBody
                                     CreateDownloadQueryParams
                                     GetDownloadQueryParams)))

(defn create-download
  "Creates a new download node asynchronously, the content of which will be the zipped content of the **node-ids** specified in the JSON body.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/downloads/createDownload)."
  ([^Ticket ticket ^CreateDownloadBody body]
   (create-download ticket body nil))
  ([^Ticket ticket ^CreateDownloadBody body ^CreateDownloadQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/downloads" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-download
  "Retrieve status information for download node **download-id**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/downloads/getDownload)."
  ([^Ticket ticket ^String download-id]
   (get-download ticket download-id nil))
  ([^Ticket ticket ^String download-id ^GetDownloadQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/downloads/%s" (config/get-url 'core) download-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-download
  "Cancels the creation of a download request.
  **Note:** The download node can be deleted using [[nodes/delete-node]].
  By default, if the download node is not deleted it will be picked up by a cleaner job which removes download nodes older than a configurable amount of time (default is 1 hour).
  Information about the existing progress at the time of cancelling can be retrieved by calling the [[get-download]] function.
  The cancel operation is done asynchronously.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/downloads/cancelDownload)."
  [^Ticket ticket ^String download-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/downloads/%s" (config/get-url 'core) download-id)
    ticket
    {}
    opts))
