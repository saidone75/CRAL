(ns cral.alfresco.core.sites
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model ListSitesQueryParams Ticket)))

(defn list-sites
  "List sites."
  ([^Ticket ticket]
   (list-sites ticket nil))
  ([^Ticket ticket ^ListSitesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites" (config/get-url 'core))
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))
