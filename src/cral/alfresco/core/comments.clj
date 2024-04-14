(ns cral.alfresco.core.comments
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateCommentQueryParams
                                     ListCommentsQueryParams
                                     UpdateCommentBody
                                     UpdateCommentQueryParams)))

(defn list-comments
  "Gets a list of comments for the node `node-id`, sorted chronologically with the newest comment first.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/comments/listComments)."
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
  "Creates a comment on node `node-id`. You specify the comment in a `body` like this:
  ```clojure
  (model/map->CreateCommentBody {:content \"This is a comment\"))})
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/comments/createComment)."
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
  "Updates an existing comment `comment-id` on node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/comments/updateComment)."
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
  "Deletes the comment `comment-id` from node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/comments/deleteComment)."
  [^Ticket ticket ^String node-id ^String comment-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/comments/%s" (config/get-url 'core) node-id comment-id)
    ticket
    {}
    opts))