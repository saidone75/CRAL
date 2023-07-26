(ns cral.alfresco.auth
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)))

(defn create-ticket
  "Logs in and returns the new authentication ticket.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Authentication%20API#/authentication/createTicket)."
  [^String username ^String password & [^PersistentHashMap opts]]
  (utils/call-rest
    client/post
    (format "%s/tickets" (config/get-url 'auth))
    nil
    {:content-type :json
     :body         (json/write-str {
                                    :userId   username
                                    :password password})}
    opts))

(defn- *-ticket [method ^Ticket ticket ^PersistentHashMap opts]
  (utils/call-rest
    method
    (format "%s/tickets/-me-" (config/get-url 'auth))
    ticket
    opts))

(defn validate-ticket
  "Check if the specified ticket is still valid.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Authentication%20API#/authentication/validateTicket)."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/get ticket opts))

(defn delete-ticket
  "Deletes logged in ticket (logout).
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Authentication%20API#/authentication/deleteTicket)."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/delete ticket opts))