(ns cral.alfresco.model
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.model]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.model ListAspectsQueryParams ListTypesQueryParams)))

(defn list-aspects
  "Gets a list of aspects from the data dictionary. The System aspects will be ignored by default.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/aspects/listAspects)."
  ([^Ticket ticket]
   (list-aspects ticket nil))
  ([^Ticket ticket ^ListAspectsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/aspects" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-aspect
  "Get information for aspect `aspect-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/aspects/getAspect)."
  [^Ticket ticket ^String aspect-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/aspects/%s" (config/get-url 'core) (name aspect-id))
    ticket
    nil
    opts))

(defn list-types
  "Gets a list of types from the data dictionary. The System types will be ignored by default.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/types/listTypes)."
  ([^Ticket ticket]
   (list-types ticket nil))
  ([^Ticket ticket ^ListTypesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/types" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))