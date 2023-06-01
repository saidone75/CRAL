(ns cral.alfresco.core.comments
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model ListCommentsQueryParams
                                Ticket)))

(defn list-comments
  "List comments."
  ([^Ticket ticket ^String node-id]
   (list-comments ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListCommentsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/comments" (config/get-url 'core) node-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

(defn create-comment
  "Create a comment."
  ([^Ticket ticket ^String node-id ^PersistentVector body]
   (create-comment ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^PersistentVector body ^CreateCommentQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/comments" (config/get-url 'core) node-id)
     ticket
     {:body         body
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))
