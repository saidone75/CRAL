(ns cral.alfresco.model
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.model]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.model ListAspectsQueryParams)))


(defn list-aspects
  ""
  ([^Ticket ticket]
   (list-aspects ticket nil))
  ([^Ticket ticket ^ListAspectsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/aspects" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))