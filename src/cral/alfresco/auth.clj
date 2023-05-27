(ns cral.alfresco.auth
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model Ticket)))

(defn create-ticket
  "Create a ticket."
  [^String username ^String password & [^PersistentHashMap opts]]
  (utils/call-rest
    client/post
    (format "%s/tickets" (config/get-url 'auth))
    nil
    {:content-type :json
     :body         (json/write-str {
                                    :userId   username
                                    :password password})}
    (:return-headers opts)))

(defn- *-ticket [method ^Ticket ticket ^PersistentHashMap opts]
  (utils/call-rest
    method
    (format "%s/tickets/-me-" (config/get-url 'auth))
    ticket
    (:return-headers opts)))

(defn validate-ticket
  "Validate a ticket."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/get ticket opts))

(defn delete-ticket
  "Delete a ticket."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/delete ticket opts))