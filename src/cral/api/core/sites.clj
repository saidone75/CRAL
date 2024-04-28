;  CRAL
;  Copyright (C) 2023-2024 Saidone
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns cral.api.core.sites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)
           (cral.model.core ApproveSiteMembershipBody
                            CreateSiteBody
                            CreatePersonSiteMembershipRequestQueryParams
                            CreateSiteMembershipQueryParams
                            CreateSiteQueryParams
                            DeleteSiteQueryParams
                            GetPersonSiteMembershipRequestsQueryParams
                            GetSiteContainerQueryParams
                            GetSiteMembershipRequestsQueryParams
                            GetSiteMembershipsQueryParams
                            GetSiteQueryParams
                            ListGroupSiteMembershipsQueryParams
                            ListPersonSiteMembershipRequestsQueryParams
                            ListPersonSiteMembershipsQueryParams
                            ListSiteContainersQueryParams
                            ListSiteMembershipsQueryParams
                            ListSitesQueryParams
                            RejectSiteMembershipBody
                            UpdateSiteBody
                            UpdatePersonSiteMembershipRequestBody
                            UpdatePersonSiteMembershipRequestQueryParams
                            UpdateSiteMembershipBody
                            UpdateSiteMembershipQueryParams
                            UpdateSiteQueryParams)))

(def consumer "SiteConsumer")
(def collaborator "SiteCollaborator")
(def contributor "SiteContributor")
(def manager "SiteManager")

(defn list-person-site-membership-requests
  "Gets a list of the current site membership requests for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteMembershipRequestsForPerson)."
  ([^Ticket ticket ^String person-id]
   (list-person-site-membership-requests ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListPersonSiteMembershipRequestsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/site-membership-requests" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-person-site-membership-requests
  "Create a site membership request for yourself on the site with the identifier of id, specified in the `body`. The result of the request differs depending on the type of site.
  - For a **public** site, you join the site immediately as a SiteConsumer.
  - For a **moderated** site, your request is added to the site membership request list. The request waits for approval from the Site Manager.
  - You cannot request membership of a private site. Members are invited by the site administrator.\n\n
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/createSiteMembershipRequestForPerson)."
  ([^Ticket ticket ^String person-id ^PersistentVector body]
   (create-person-site-membership-requests ticket person-id body nil))
  ([^Ticket ticket ^String person-id ^PersistentVector body ^CreatePersonSiteMembershipRequestQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/people/%s/site-membership-requests" (config/get-url 'core) person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-person-site-membership-request
  "Gets the site membership request for site `site-id` for person `person-id`, if one exists.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSiteMembershipRequestForPerson)."
  ([^Ticket ticket ^String person-id ^String site-id]
   (get-person-site-membership-request ticket person-id site-id nil))
  ([^Ticket ticket ^String person-id ^String site-id ^GetPersonSiteMembershipRequestsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/site-membership-requests/%s" (config/get-url 'core) person-id site-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-person-site-membership-request
  "Updates the message for the site membership request to site `site-id` for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/updateSiteMembershipRequestForPerson)."
  ([^Ticket ticket ^String person-id ^String site-id ^UpdatePersonSiteMembershipRequestBody body]
   (update-person-site-membership-request ticket person-id site-id body nil))
  ([^Ticket ticket ^String person-id ^String site-id ^UpdatePersonSiteMembershipRequestBody body ^UpdatePersonSiteMembershipRequestQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/people/%s/site-membership-requests/%s" (config/get-url 'core) person-id site-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-person-site-membership-request
  "Deletes the site membership request to site `site-id` for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/deleteSiteMembershipRequestForPerson)."
  ([^Ticket ticket ^String person-id ^String site-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/people/%s/site-membership-requests/%s" (config/get-url 'core) person-id site-id)
     ticket
     nil
     opts)))

(defn list-person-site-memberships
  "Gets a list of site membership information for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.
  You can use the **where** parameter in `query-params` to filter the returned sites by **visibility** or site **preset**.\\
  Example to filter by **visibility**, use any one of:
  ```clojure
  (visibility='PRIVATE')
  (visibility='PUBLIC')
  (visibility='MODERATED')
  ```
  Example to filter by site **preset**:
  ```clojure
  (preset='site-dashboard')
  ```
  The default sort order for the returned list is for sites to be sorted by ascending title.\\
  You can override the default by using the **order-by** parameter in `query-params`. You can specify one or more of the following fields in the **order-by** parameter:
  - id
  - title
  - role\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteMembershipsForPerson)."
  ([^Ticket ticket ^String person-id]
   (list-person-site-memberships ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListPersonSiteMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/sites" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-person-site-membership
  "Gets site membership information for person `person-id` on site `site-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSiteMembershipForPerson)."
  [^Ticket ticket ^String person-id ^String site-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/people/%s/sites/%s" (config/get-url 'core) person-id site-id)
    ticket
    nil
    opts))

(defn delete-person-site-membership
  "Deletes person `person-id` as a member of site `site-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/deleteSiteMembershipForPerson)."
  [^Ticket ticket ^String person-id ^String site-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/people/%s/sites/%s" (config/get-url 'core) person-id site-id)
    ticket
    nil
    opts))

(defn list-sites
  "Gets a list of sites in this repository.
  You can use the **where** parameter in `query-params` to filter the returned sites by visibility or site preset.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSites)."
  ([^Ticket ticket]
   (list-sites ticket nil))
  ([^Ticket ticket ^ListSitesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn create-site
  "Creates a default site with the given details. Unless explicitly specified, the site id will be generated from the site title.
  The site id must be unique and only contain alphanumeric and/or dash characters.\\
  **Note:** the id of a site cannot be updated once the site has been created.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/createSite)."
  ([^Ticket ticket ^CreateSiteBody body]
   (create-site ticket body nil))
  ([^Ticket ticket ^CreateSiteBody body ^CreateSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/sites" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-site
  "Gets information for site `site-id`.
  You can use the **relations** parameter in `query-params` to include one or more relate entities in a single response and so reduce network traffic.
  The entity types in Alfresco are organized in a tree structure.
  The **sites** entity has two children, **containers** and **members**.
  The following relations parameter returns all the container and member objects related to the site `site-id`: `containers,members`\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSite)."
  ([^Ticket ticket ^String site-id]
   (get-site ticket site-id nil))
  ([^Ticket ticket ^String site-id ^GetSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-site
  "Update the details for the given site `site-id`. Site Manager or otherwise a (site) admin can update title, description or visibility.\\
  **Note:** the id of a site cannot be updated once the site has been created.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/updateSite)."
  ([^Ticket ticket ^String site-id ^UpdateSiteBody body]
   (update-site ticket site-id body nil))
  ([^Ticket ticket ^String site-id ^UpdateSiteBody body ^UpdateSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/sites/%s" (config/get-url 'core) site-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-site
  "Deletes the site with `site-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/deleteSite)."
  ([^Ticket ticket ^String site-id]
   (delete-site ticket site-id nil))
  ([^Ticket ticket ^String site-id ^DeleteSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/sites/%s" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-site-containers
  "Gets a list of containers for site `site-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteContainers)."
  ([^Ticket ticket ^String site-id]
   (list-site-containers ticket site-id nil))
  ([^Ticket ticket ^String site-id ^ListSiteContainersQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s/containers" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-site-container
  "Gets information on the container `container-id` in site `site-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSiteContainer)."
  ([^Ticket ticket ^String site-id ^String container-id]
   (get-site-container ticket site-id container-id nil))
  ([^Ticket ticket ^String site-id ^String container-id ^GetSiteContainerQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s/containers/%s" (config/get-url 'core) site-id container-id)
     ticket
     {:query-params query-params}
     opts)))

(defn get-site-membership-request
  "Get the list of site membership requests the user can action.
  You can use the **where** parameter in `query-params` to filter the returned site membership requests by `site-id`. For example:
  ```clojure
  (siteId=mySite)
  ```
  The **where** parameter can also be used to filter by `person-id`. For example:
  ```clojure
  where=(personId=person)
  ```
  This may be combined with the `site-id` filter, as shown below:
  ```clojure
  where=(siteId=mySite AND personId=person)
  ```
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSiteMembershipRequests)."
  ([^Ticket ticket]
   (get-site-membership-request ticket nil))
  ([^Ticket ticket ^GetSiteMembershipRequestsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/site-membership-requests" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn approve-site-membership-request
  "Approve a site membership request.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/approveSiteMembershipRequest)."
  ([^Ticket ticket ^String site-id ^String invitee-id ^ApproveSiteMembershipBody body & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/sites/%s/site-membership-requests/%s/approve" (config/get-url 'core) site-id invitee-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :content-type :json}
     opts)))

(defn reject-site-membership-request
  "Reject a site membership request.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/rejectSiteMembershipRequest)."
  ([^Ticket ticket ^String site-id ^String invitee-id ^RejectSiteMembershipBody body & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/sites/%s/site-membership-requests/%s/reject" (config/get-url 'core) site-id invitee-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :content-type :json}
     opts)))

(defn list-site-memberships
  "Gets a list of site memberships for site `site-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteMemberships)."
  ([^Ticket ticket ^String site-id]
   (list-site-memberships ticket site-id nil))
  ([^Ticket ticket ^String site-id ^ListSiteMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s/members" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-site-membership
  "Creates a site membership for person `person-id` on site `site-id`.
  You can set the **role** to one of four types:
  - SiteConsumer
  - SiteCollaborator
  - SiteContributor
  - SiteManager\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/createSiteMembership)."
  ([^Ticket ticket ^String site-id ^PersistentVector body]
   (create-site-membership ticket site-id body nil))
  ([^Ticket ticket ^String site-id ^PersistentVector body ^CreateSiteMembershipQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/sites/%s/members" (config/get-url 'core) site-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-site-membership
  "Gets site membership information for person `person-id` on site `site-id.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/getSiteMembership)."
  ([^Ticket ticket ^String site-id ^String person-id]
   (get-site-membership ticket site-id person-id nil))
  ([^Ticket ticket ^String site-id ^String person-id ^GetSiteMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s/members/%s" (config/get-url 'core) site-id person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-site-membership
  "Update the membership of person `person-id` in site `site-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  You can set the **role** to one of four types:
  - SiteConsumer
  - SiteCollaborator
  - SiteContributor
  - SiteManager\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/updateSiteMembership)."
  ([^Ticket ticket ^String site-id ^String person-id ^UpdateSiteMembershipBody body]
   (update-site-membership ticket site-id person-id body nil))
  ([^Ticket ticket ^String site-id ^String person-id ^UpdateSiteMembershipBody body ^UpdateSiteMembershipQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/sites/%s/members/%s" (config/get-url 'core) site-id person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn delete-site-membership
  "Deletes person `person-id` as a member of site `site-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/deleteSiteMembership)."
  ([^Ticket ticket ^String site-id ^String person-id & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/sites/%s/members/%s" (config/get-url 'core) site-id person-id)
     ticket
     nil
     opts)))

(defn list-group-site-membership
  "Gets a list of group membership for site `site-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteGroups)."
  ([^Ticket ticket ^String site-id]
   (list-group-site-membership ticket site-id nil))
  ([^Ticket ticket ^String site-id ^ListGroupSiteMembershipsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites/%s/group-members" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))