(ns cral.alfresco.core.activities
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListActivitiesQueryParams)))

(defn list-activities
  "Gets a list of activities for person **person-id**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/activities/listActivitiesForPerson)."
  ([^Ticket ticket ^String person-id]
   (list-activities ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListActivitiesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/activities" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

