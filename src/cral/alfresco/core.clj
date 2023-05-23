(ns cral.alfresco.core
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.auth Ticket)
           (java.io File)))

(defrecord QueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector include
   ^String relative-path
   ^Boolean include-source
   ^PersistentVector fields
   ^Boolean permanent])

(defrecord NodeBodyUpdate
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

(defrecord LocallySet
  [^String authority-id
   ^String name
   ^String access-status])

(defrecord Permissions
  [^Boolean is-inheritance-enabled
   ^PersistentVector locally-set])

(defrecord NodeBodyCreate
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defn get-node
  "Get node metadata."
  ([^Ticket ticket ^String node-id]
   (get-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^QueryParams query-params]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn update-node
  "Update a node."
  ([^Ticket ticket ^String node-id ^NodeBodyUpdate node-body-update]
   (update-node ticket node-id node-body-update nil))
  ([^Ticket ticket ^String node-id ^NodeBodyUpdate node-body-update ^QueryParams query-params]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys node-body-update))
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json})))

(defn delete-node
  "Delete a node."
  ([^Ticket ticket ^String node-id]
   (delete-node ticket node-id nil))
  ([^Ticket ticket ^String node-id ^QueryParams query-params]
   (utils/call-rest
     client/delete
     (format "%s/nodes/%s" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))})))

(defn list-node-children
  "List node children."
  [ticket node-id & [query-params]]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/children" (config/get-url 'core) node-id)
    ticket
    {:query-params query-params}))

(defn create-node
  "Create a node."
  [ticket parent-id ^NodeBodyCreate node-body-create]
  (utils/call-rest
    client/post
    (format "%s/nodes/%s/children" (config/get-url 'core) parent-id)
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys node-body-create))
     :content-type :json}))

(defn get-node-content
  "Get node content."
  [ticket node-id & [query-params]]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
    ticket
    {:query-params query-params
     :as           :byte-array}))

(defn update-node-content
  "Upload node content."
  [ticket node-id ^File content]
  (utils/call-rest
    client/put
    (format "%s/nodes/%s/content" (config/get-url 'core) node-id)
    ticket
    {:body content}))

(defn list-parents
  "List parents"
  [ticket node-id & [query-params]]
  (utils/call-rest
    client/get
    (format "%s/nodes/%s/parents" (config/get-url 'core) node-id)
    ticket
    {:query-params query-params}))