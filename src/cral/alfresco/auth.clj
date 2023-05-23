(ns cral.alfresco.auth
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defrecord Ticket
  [^String id
   ^String user-id])

(defn create-ticket
  "Create a ticket."
  [^String username ^String password]
  (utils/call-rest
    client/post
    (format "%s/tickets" (config/get-url 'auth))
    nil
    {:content-type :json
     :body         (json/write-str {
                                    :userId   username
                                    :password password})}))

(defn- *-ticket [method ^Ticket ticket]
  (utils/call-rest
    method
    (format "%s/tickets/-me-" (config/get-url 'auth))
    ticket))

(defn validate-ticket
  "Validate a ticket."
  [^Ticket ticket]
  (*-ticket client/get ticket))

(defn delete-ticket
  "Delete a ticket."
  [^Ticket ticket]
  (*-ticket client/delete ticket))