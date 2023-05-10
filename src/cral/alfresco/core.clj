(ns cral.alfresco.core
  (:import (java.util Base64)))

(require '[clojure.string :as s]
         '[clojure.data.json :as json]
         '[clj-http.lite.client :as client]
         '[cral.utils.utils :as utils])

(defonce config (atom {:scheme "http"
                       :host   "localhost"
                       :port   8080
                       :path   "alfresco/api/-default-/public/alfresco/versions/1"}))

(defn- get-url []
  (str (:scheme @config) "://" (:host @config) ":" (:port @config) "/" (:path @config)))

(defn get-node
  "Get node metadata"
  [ticket node-id & [query-params]]
  (utils/keywordize-kebab
    (get
      (json/read-str
        (:body
          (client/get
            (str (get-url) "/nodes/" node-id)
            {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
             :query-params query-params}
            )))
      "entry")))

(defn update-node
  "Update a node"
  [ticket node-id body]
  (client/put
    (str (get-url) "/nodes/" node-id)
    {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
     :body body}))

(defn delete-node
  "Delete a node"
  [ticket node-id]
  (client/delete
    (str (get-url) "/nodes/" node-id)
    {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}}
    )
  )

(defn create-node
  "Create a node"
  [ticket parent-id body & [query-params]]
  (client/post
    (str (get-url) "/nodes/" parent-id)
    ))

(defn upload-content
  [ticket node-id body])

