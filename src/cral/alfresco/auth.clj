(ns cral.alfresco.auth
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defn create-ticket
  "Create a ticket."
  [username password]
  (utils/call-rest
    client/post
    (str (config/get-url 'auth) "/tickets")
    nil
    {:content-type :json
     :body         (json/write-str {
                                    :userId   username
                                    :password password})}))

(defn- *-ticket [method ticket]
  (utils/call-rest
    method
    (str (config/get-url 'auth) "/tickets/-me-")
    ticket
    {}))

(defn validate-ticket
  "Validate a ticket."
  [ticket]
  (*-ticket client/get ticket))

(defn delete-ticket
  "Delete a ticket."
  [ticket]
  (*-ticket client/delete ticket))