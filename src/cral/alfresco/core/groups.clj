(ns cral.alfresco.core.groups
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListGroupMembershipQueryParams)))

(defn list-group-memberships
  "List groups memberships."
  ([^Ticket ticket ^String person-id]
   (list-group-memberships ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListGroupMembershipQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/groups" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))