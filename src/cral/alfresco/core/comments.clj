(ns cral.alfresco.core.comments
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model CreateCommentQueryParams
                                ListCommentsQueryParams
                                Ticket
                                UpdateCommentBody
                                UpdateCommentQueryParams)))

(defn list-comments
  "List comments."
  ([^Ticket ticket ^String node-id]
   (list-comments ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListCommentsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/comments" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-comment
  "Create a comment."
  ([^Ticket ticket ^String node-id ^PersistentVector body]
   (create-comment ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^PersistentVector body ^CreateCommentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/comments" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn update-comment
  "Update a comment."
  ([^Ticket ticket ^String node-id ^String comment-id ^UpdateCommentBody body]
   (update-comment ticket node-id comment-id body nil))
  ([^Ticket ticket ^String node-id ^String comment-id ^UpdateCommentBody body ^UpdateCommentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/nodes/%s/comments/%s" (config/get-url 'core) node-id comment-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-comment
  "Delete a comment."
  [^Ticket ticket ^String node-id ^String comment-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/comments/%s" (config/get-url 'core) node-id comment-id)
    ticket
    {}
    opts))