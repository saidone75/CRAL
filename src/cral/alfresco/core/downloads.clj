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
  "Create a new download."
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
  "Get a download."
  ([^Ticket ticket ^String download-id]
   (get-download ticket download-id nil))
  ([^Ticket ticket ^String download-id ^GetDownloadQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/downloads/%s" (config/get-url 'core) download-id)
     ticket
     {:query-params query-params}
     opts)))
