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

(ns cral.alfresco.auth
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)))

(defn create-ticket
  "Logs in and returns the new authentication ticket.\\
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
  "Check if the specified ticket is still valid.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Authentication%20API#/authentication/validateTicket)."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/get ticket opts))

(defn delete-ticket
  "Deletes logged in ticket (logout).\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Authentication%20API#/authentication/deleteTicket)."
  [^Ticket ticket & [^PersistentHashMap opts]]
  (*-ticket client/delete ticket opts))