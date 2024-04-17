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
           (cral.model.core CreateSiteBody
                            CreateSiteQueryParams
                            DeleteSiteQueryParams
                            GetSiteQueryParams
                            ListSiteMembershipRequestsQueryParams
                            ListSitesQueryParams
                            UpdateSiteBody
                            UpdateSiteQueryParams)))

(defn list-site-membership-requests
  "Gets a list of the current site membership requests for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/listSiteMembershipRequestsForPerson)."
  ([^Ticket ticket ^String person-id]
   (list-site-membership-requests ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListSiteMembershipRequestsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/site-membership-requests" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-site-membership-requests
  "Create a site membership request for yourself on the site with the identifier of id, specified in the `body`. The result of the request differs depending on the type of site.
  - For a **public** site, you join the site immediately as a SiteConsumer.
  - For a **moderated** site, your request is added to the site membership request list. The request waits for approval from the Site Manager.
  - You cannot request membership of a private site. Members are invited by the site administrator.\n\n
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/sites/createSiteMembershipRequestForPerson)."
  ([^Ticket ticket ^String person-id ^PersistentVector body]
   (create-site-membership-requests ticket person-id body nil))
  ([^Ticket ticket ^String person-id ^PersistentVector body ^ListSiteMembershipRequestsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/people/%s/site-membership-requests" (config/get-url 'core) person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

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
  The following relations parameter returns all the container and member objects related to the site `site-id`:
  `containers,members`\\
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