(ns cral.alfresco.core.shared-links
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateSharedLinkBody CreateSharedLinkQueryParams)))

(defn create-shared-link
  "Create a shared link to the file **node-id** in the request body. Also, an optional expiry date could be set,
  so the shared link would become invalid when the expiry date is reached.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/createSharedLink)."
  ([^Ticket ticket ^CreateSharedLinkBody body]
   (create-shared-link ticket body nil))
  ([^Ticket ticket ^CreateSharedLinkBody body ^CreateSharedLinkQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/shared-links" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))