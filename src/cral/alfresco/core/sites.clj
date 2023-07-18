(ns cral.alfresco.core.sites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateSiteBody
                                     CreateSiteQueryParams
                                     DeleteSiteQueryParams
                                     GetSiteQueryParams
                                     ListSiteMembershipRequestsQueryParams
                                     ListSitesQueryParams
                                     UpdateSiteBody
                                     UpdateSiteQueryParams)))

(defn list-site-membership-requests
  "Gets a list of the current site membership requests for person **person-id**.
  You can use the `-me-` string in place of `person-id` to specify the currently authenticated user.
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

(defn list-sites
  "List sites."
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
  "Create a site."
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
  "Get a site."
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
  "Delete a site."
  ([^Ticket ticket ^String site-id]
   (delete-site ticket site-id nil))
  ([^Ticket ticket ^String site-id ^DeleteSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/sites/%s" (config/get-url 'core) site-id)
     ticket
     {:query-params query-params}
     opts)))
