(ns cral.alfresco.core.people
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListPeopleQueryParams)))

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
