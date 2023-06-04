(ns cral.alfresco.core.sites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model CreateSiteBody
                                CreateSiteQueryParams
                                DeleteSiteQueryParams
                                ListSitesQueryParams
                                Ticket)))

(defn list-sites
  "List sites."
  ([^Ticket ticket]
   (list-sites ticket nil))
  ([^Ticket ticket ^ListSitesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/sites" (config/get-url 'core))
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))

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
      :query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))
      :content-type :json}
     (:return-headers opts))))

(defn get-site
  []
  ;; TODO
  )

(defn update-site
  []
  ;; TODO
  )

(defn delete-site
  "Delete a site."
  ([^Ticket ticket ^String site-id]
   (delete-site ticket site-id nil))
  ([^Ticket ticket ^String site-id ^DeleteSiteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/sites/%s" (config/get-url 'core) site-id)
     ticket
     {:query-params (into {} (utils/camel-case-stringify-keys (remove #(nil? (val %)) query-params)))}
     (:return-headers opts))))
