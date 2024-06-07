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

(ns cral.api.core.renditions
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)))

(defn create-rendition
  "An asynchronous request to create a rendition for file `node-id`.
  The rendition is specified by name **id** in the request body:
  ```json
  {
    \"id\": \"doclib\"
  }
  ```
  Multiple names may be specified as a comma separated list or using a list format:
  ```json
  [
    {
      \"id\": \"doclib\"
    },
    {
     \"id\": \"avatar\"
    }
  ]
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/renditions/createRendition)."
  ([^Ticket ticket ^String node-id ^PersistentVector body & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/renditions" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params nil
      :content-type :json}
     opts)))