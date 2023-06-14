(ns cral.alfresco.core.people
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreatePersonBody
                                     CreatePersonQueryParams
                                     ListPeopleQueryParams)))

(defn create-person
  "Create person."
  ([^Ticket ticket ^CreatePersonBody body]
   (create-person ticket body nil))
  ([^Ticket ticket ^CreatePersonBody body ^CreatePersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/people" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-people
  "List people."
  ([^Ticket ticket]
   (list-people ticket nil))
  ([^Ticket ticket ^ListPeopleQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))
