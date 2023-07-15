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

(defn list-user-group-memberships
  "Gets a list of group membership information for person **person-id**.
  You can use the `-me-` string in place of `person-id` to specify the currently authenticated user.
  You can use the **include** parameter to return additional information.
  You can use the **where** parameter to filter the returned groups by **is-root**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/listGroupMembershipsForPerson)."
  ([^Ticket ticket ^String person-id]
   (list-user-group-memberships ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListGroupMembershipQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/groups" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-groups
  "Gets a list of groups.
  You can use the **include** parameter to return additional information.
  You can use the **where** parameter to filter the returned groups by **is-root**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/listGroups)."
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
  "Create a group.
  The group id must start with \"GROUP_\". If this is omitted it will be added automatically.
  This format is also returned when listing groups or group memberships. It should be noted that the other group-related operations also expect the id to start with \"GROUP_\".
  If one or more **parent-ids** are specified then the group will be created and become a membe of each of the specified parent groups.
  If no **parent-ids** are specified then the group will be created as a root group.
  The group will be created in the **APP.DEFAULT** and **AUTH.ALF** zones.
  You must have admin rights to create a group.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/createGroup)."
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
  "Get details for **group group-id**.
  You can use the **include** parameter to return additional information.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/getGroup)."
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
  "Update details (display-name) for group **group-id**.
  You must have admin rights to update a group.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/updateGroup)."
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
  "Delete group **group-id**.
  The option to cascade delete applies this recursively to any hierarchy of group members.
  In this case, removing a group member does not delete the person or sub-group itself.
  If a removed sub-group no longer has any parent groups then it becomes a root group.
  You must have admin rights to delete a group.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/deleteGroup)."
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
  "Create a group membership (for an existing person or group) within a group **group-id**.
  If the added group was previously a root group then it becomes a non-root group since it now has a parent.
  It is an error to specify an id that does not exist.
  You must have admin rights to create a group membership.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/createGroupMembership)."
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
  "Gets a list of the group memberships for the group **group-id**.
  You can use the where parameter to filter the returned groups by **member-type**.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/listGroupMemberships)."
  ([^Ticket ticket ^String group-id]
   (list-group-memberships ticket group-id nil))
  ([^Ticket ticket ^String group-id ^ListGroupMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/groups/%s/members" (config/get-url 'core) group-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-group-membership
  "Delete group member **group-member-id** (person or sub-group) from group **group-id**.
  Removing a group member does not delete the person or sub-group itself.
  If a removed sub-group no longer has any parent groups then it becomes a root group.
  You must have admin rights to delete a group membership.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/groups/deleteGroupMembership)."
  [^Ticket ticket ^String group-id ^String group-member-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/groups/%s/members/%s" (config/get-url 'core) group-id group-member-id)
    ticket
    nil
    opts))