(ns cral.alfresco.core
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defn- ok-response
  [r]
  {:status (:status r)
   :body   (if (and (not (nil? (:body r))) (not (empty? (:body r))))
             (utils/kebab-keywordize-keys (json/read-str (:body r)))
             nil)})

(defn- ex-response
  [e]
  (let [ex-data (ex-data e)
        body (utils/kebab-keywordize-keys (json/read-str (:body ex-data)))]
    {:status  (:status ex-data)
     :message (get-in body [:error :brief-summary])
     :body    body}))

(defn get-node
  "Get node metadata."
  [ticket node-id & [query-params]]
  (try
    (let [response (client/get
                     (str (config/get-url 'core) "/nodes/" node-id)
                     {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
                      :query-params query-params})]
      (ok-response response))
    (catch Exception e (ex-response e))))

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

(defrecord NodeBodyCreate
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defn make-node-body-create
  ([^String name ^String node-type]
   (map->NodeBodyCreate {:name name :node-type node-type :properties nil}))
  ([^String name ^String node-type ^PersistentHashMap properties]
   (map->NodeBodyCreate {:name name :node-type node-type :properties properties})))

(defn create-node
  "Create a node."
  [ticket parent-id ^NodeBodyCreate node-body-create]
  (try
    (let [response (client/post
                     (str (config/get-url 'core) "/nodes/" parent-id "/children")
                     {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
                      :body         (json/write-str (utils/camel-case-stringify-keys node-body-create))
                      :content-type :json})]
      (ok-response response))
    (catch Exception e (ex-response e))))

(defn delete-node
  "Delete a node."
  [ticket node-id]
  (try
    (let [response (client/delete
                     (str (config/get-url 'core) "/nodes/" node-id)
                     {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}})]
      (ok-response response))
    (catch Exception e (ex-response e))))

(defrecord NodeBodyUpdate
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

(defn update-node
  "Update a node."
  [ticket node-id body]
  (client/put
    (str (config/get-url 'core) "/nodes/" node-id)
    {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
     :body    body}))



(defn upload-content
  [ticket node-id body])

