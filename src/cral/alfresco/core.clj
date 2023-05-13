(ns cral.alfresco.core
  (:import (clojure.lang PersistentArrayMap PersistentVector)
           (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defn get-node
  "Get node metadata."
  [ticket node-id & [query-params]]
  (utils/kebab-keywordize-keys
    (get
      (json/read-str
        (:body
          (client/get
            (str (config/get-url 'core) "/nodes/" node-id)
            {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
             :query-params query-params}
            )))
      "entry")))

(defrecord LocallySet
  [^String authority-id
   ^String name
   ^String access-status])

(defn make-locally-set
  [authority-id name access-status]
  (map->LocallySet {:authority-id authority-id :name name :access-status access-status}))

(defrecord Permissions
  [^Boolean is-inheritance-enabled
   ^PersistentVector locally-set])

(defn make-permissions [is-inheritance-enabled & locally-set]
  (map->Permissions {:is-inheritance-enabled is-inheritance-enabled
                     :locally-set            locally-set}))

(defn add-locally-set [^Permissions permissions ^LocallySet locally-set]
  (assoc-in permissions [:locally-set] (conj (:locally-set permissions) locally-set)))

(defrecord NodeBodyUpdate
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentArrayMap properties])

(defrecord NodeBodyCreate
  [^String name
   ^String node-type])

(defn make-node-body-create
  [^String name ^String node-type]
  (map->NodeBodyCreate {:name name :node-type node-type}))

(defn create-node
  "Create a node."
  [ticket parent-id ^NodeBodyCreate node-body-create]
  (client/post
    (str (config/get-url 'core) "/nodes/" parent-id "/children")
    {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
     :body         (json/write-str (utils/camel-case-stringify-keys node-body-create))
     :content-type :json}))

(defn update-node
  "Update a node."
  [ticket node-id body]
  (client/put
    (str (config/get-url 'core) "/nodes/" node-id)
    {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
     :body    body}))

(defn delete-node
  "Delete a node."
  [ticket node-id]
  (client/delete
    (str (config/get-url 'core) "/nodes/" node-id)
    {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}}
    )
  )

(defn upload-content
  [ticket node-id body])

