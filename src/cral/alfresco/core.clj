(ns cral.alfresco.core
  (:import (java.util Base64))
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

(defn create-node
  "Create a node."
  [ticket parent-id body & [query-params]]
  (client/post
    (str (config/get-url 'core) "/nodes/" parent-id)
    ))

(defn upload-content
  [ticket node-id body])

