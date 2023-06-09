(ns cral.alfresco.core.tags
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListTagsQueryParams)))

(defn list-tags
  "List tags for a node."
  ([^Ticket ticket ^String node-id]
   (list-tags ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListTagsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/tags" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))
