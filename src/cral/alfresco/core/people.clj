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
                                     GetPersonQueryParams
                                     ListPeopleQueryParams UpdatePersonBody UpdatePersonQueryParams)))

(defn create-person
  "Create a person."
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

(defn get-person
  "Get a person."
  ([^Ticket ticket ^String person-id]
   (get-person ticket person-id nil))
  ([^Ticket ticket ^String person-id ^GetPersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-person
  "Update a person."
  ([^Ticket ticket ^String person-id ^UpdatePersonBody body]
   (update-person ticket person-id body nil))
  ([^Ticket ticket ^String person-id ^UpdatePersonBody body ^UpdatePersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/people/%s" (config/get-url 'core) person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))
