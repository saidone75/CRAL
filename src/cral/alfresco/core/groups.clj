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
                                     CreateGroupMembershipBody
                                     CreateGroupMembershipQueryParams
                                     CreateGroupQueryParams
                                     DeleteGroupQueryParams
                                     GetGroupDetailsQueryParams
                                     ListGroupMembershipQueryParams
                                     ListGroupMembershipsQueryParams
                                     ListGroupsQueryParams
                                     UpdateGroupBody
                                     UpdateGroupQueryParams)))

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

(defn update-group-details
  "Update group details."
  ([^Ticket ticket ^String group-id ^UpdateGroupBody body]
   (update-group-details ticket group-id body nil))
  ([^Ticket ticket ^String group-id ^UpdateGroupBody body ^UpdateGroupQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/groups/%s" (config/get-url 'core) group-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-group
  "Delete a group."
  ([^Ticket ticket ^String group-id]
   (delete-group ticket group-id nil))
  ([^Ticket ticket ^String group-id ^DeleteGroupQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/groups/%s" (config/get-url 'core) group-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-group-membership
  "Create a group membership."
  ([^Ticket ticket ^String group-id ^CreateGroupMembershipBody body]
   (create-group-membership ticket group-id body nil))
  ([^Ticket ticket ^String group-id ^CreateGroupMembershipBody body ^CreateGroupMembershipQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/groups/%s/members" (config/get-url 'core) group-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-group-memberships
  "List memberships of a group."
  ([^Ticket ticket ^String group-id]
   (list-group-memberships ticket group-id nil))
  ([^Ticket ticket ^String group-id ^ListGroupMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/groups/%s/members" (config/get-url 'core) group-id)
     ticket
     {:query-params query-params}
     opts)))