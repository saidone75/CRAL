(ns cral.alfresco.core.shared-links
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateSharedLinkBody CreateSharedLinkQueryParams EmailSharedLinkBody GetSharedLinkContentQueryParams ListSharedLinksQueryParams)))

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

(defn list-shared-links
  "Get a list of links that the current user has read permission on source node.
  The list is ordered in descending modified order.
  **Note:** The list of links is eventually consistent so newly created shared links may not appear immediately.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/listSharedLinks)."
  ([^Ticket ticket]
   (list-shared-links ticket nil))
  ([^Ticket ticket ^ListSharedLinksQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/shared-links" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-shared-link
  "Gets minimal information for the file with shared link identifier **shared-id**.
  **Note:** No authentication is required to call this endpoint.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/listSharedLinks)."
  ([^String shared-id]
   (get-shared-link shared-id nil))
  ([^String shared-id ^ListSharedLinksQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/shared-links/%s" (config/get-url 'core) shared-id)
     nil
     {:query-params query-params}
     opts)))

(defn delete-shared-link
  "Deletes the shared link with identifier **shared-id**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/deleteSharedLink)."
  [^Ticket ticket ^String shared-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/shared-links/%s" (config/get-url 'core) shared-id)
    ticket
    {}
    opts))

(defn get-shared-link-content
  "Gets the content of the file with shared link identifier **shared-id**.
  **Note:** No authentication is required to call this endpoint.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/getSharedLinkContent)."
  ([^String shared-id]
   (get-shared-link-content shared-id nil nil {:return-headers true}))
  ([^String shared-id ^GetSharedLinkContentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/shared-links/%s/content" (config/get-url 'core) shared-id)
     nil
     {:query-params query-params :as :byte-array}
     opts)))

(defn email-shared-link
  "Sends email with app-specific url including identifier **shared-id**.
  The client and recipient-emails properties are mandatory in the request body.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/shared-links/emailSharedLink)."
  [^Ticket ticket ^String shared-id ^EmailSharedLinkBody body & [^PersistentHashMap opts]]
  (utils/call-rest
    client/post
    (format "%s/shared-links/%s/email" (config/get-url 'core) shared-id)
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys body))
     :content-type :json}
    opts))