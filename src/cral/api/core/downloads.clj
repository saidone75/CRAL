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

(ns cral.api.core.downloads
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core CreateDownloadBody
                            CreateDownloadQueryParams
                            GetDownloadQueryParams)))

(defn create-download
  "Creates a new download node asynchronously, the content of which will be the zipped content of the **node-ids** specified in the `body` like this:
  ```clojure
  (model/->CreateDownloadBody [\"514ddb33-c5ae-4f7f-a3b2-7ec529f57d54\" \"24c440c8-e015-4879-aeb3-ac3d03dd1440\"])
  ```
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
  "Retrieve status information for download node `download-id`.\\
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
  "Cancels the creation of a download request.\\
  **Note:** The download node can be deleted using [[nodes/delete-node]].\\
  By default, if the download node is not deleted it will be picked up by a cleaner job which removes download nodes older than a configurable amount of time (default is 1 hour).
  Information about the existing progress at the time of cancelling can be retrieved by calling the [[get-download]] function.
  The cancel operation is done asynchronously.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/downloads/cancelDownload)."
  [^Ticket ticket ^String download-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/downloads/%s" (config/get-url 'core) download-id)
    ticket
    nil
    opts))