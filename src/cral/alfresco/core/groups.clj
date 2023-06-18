(ns cral.alfresco.core.groups
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateGroupBody
                                     CreateGroupQueryParams
                                     GetGroupDetailsQueryParams
                                     ListGroupMembershipQueryParams
                                     ListGroupsQueryParams)))

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

(defn list-groups
  "List groups."
  ([^Ticket ticket]
   (list-groups ticket nil))
  ([^Ticket ticket ^ListGroupsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/groups" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn create-group
  "Create a group."
  ([^Ticket ticket ^CreateGroupBody body]
   (create-group ticket body nil))
  ([^Ticket ticket ^CreateGroupBody body ^CreateGroupQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/groups" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-group-details
  "Get group details."
  ([^Ticket ticket ^String group-id]
   (get-group-details ticket group-id nil))
  ([^Ticket ticket ^String group-id ^GetGroupDetailsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/groups/%s" (config/get-url 'core) group-id)
     ticket
     {:query-params query-params}
     opts)))