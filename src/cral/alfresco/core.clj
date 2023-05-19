(ns cral.alfresco.core
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (java.io File)
           (java.util Base64)))

(defn- add-auth
  [ticket req]
  (assoc-in req [:headers "Authorization"] (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))))

(defn- call-rest
  [method url ticket req]
  (try
    (let [response (method url (add-auth ticket req))]
      (utils/ok-response response))
    (catch Exception e (utils/ex-response e))))

(defn get-node
  "Get node metadata."
  [ticket node-id & [query-params]]
  (call-rest
    client/get
    (str (config/get-url 'core) "/nodes/" node-id)
    ticket
    {:query-params query-params}))

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

(defn update-node
  "Update a node."
  [ticket node-id ^NodeBodyUpdate node-body-update]
  (call-rest
    client/put
    (str (config/get-url 'core) "/nodes/" node-id)
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys node-body-update))
     :content-type :json}))

(defn delete-node
  "Delete a node."
  [ticket node-id]
  (call-rest
    client/delete
    (str (config/get-url 'core) "/nodes/" node-id)
    ticket
    {}))

(defn list-node-children
  "List node children."
  [ticket node-id & [query-params]]
  (call-rest
    client/get
    (str (config/get-url 'core) "/nodes/" node-id "/children")
    ticket
    {:query-params query-params}))

(defrecord NodeBodyCreate
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defn create-node
  "Create a node."
  [ticket parent-id ^NodeBodyCreate node-body-create]
  (call-rest
    client/post
    (str (config/get-url 'core) "/nodes/" parent-id "/children")
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys node-body-create))
     :content-type :json}))

(defn get-node-content
  "Get node content."
  [ticket node-id & [query-params]]
  (call-rest
    client/get
    (str (config/get-url 'core) "/nodes/" node-id "/content")
    ticket
    {:query-params query-params
     :as           :byte-array}))

(defn update-node-content
  "Upload node content."
  [ticket node-id ^File content]
  (call-rest
    client/put
    (str (config/get-url 'core) "/nodes/" node-id "/content")
    ticket
    {:body content}))
