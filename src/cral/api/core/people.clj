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

(ns cral.api.core.people
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core CreatePersonBody
                            CreatePersonQueryParams
                            GetPersonQueryParams
                            ListPeopleQueryParams UpdatePersonBody UpdatePersonQueryParams)))

(defn create-person
  "Create a person.
  If applicable, the given person's login access can also be optionally disabled.
  You must have admin rights to create a person.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/people/createPerson)."
  ([^Ticket ticket ^CreatePersonBody body]
   (create-person ticket body nil))
  ([^Ticket ticket ^CreatePersonBody body ^CreatePersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/people" (config/get-url 'core))
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-people
  "List people.
  You can use the **include** parameter to return any additional information.
  The default sort order for the returned list is for people to be sorted by ascending id.
  You can override the default by using the **order-by** parameter.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/people/listPeople)."
  ([^Ticket ticket]
   (list-people ticket nil))
  ([^Ticket ticket ^ListPeopleQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-person
  "Gets information for the person **person-id**.
  You can use the `-me-` string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/people/getPerson)."
  ([^Ticket ticket ^String person-id]
   (get-person ticket person-id nil))
  ([^Ticket ticket ^String person-id ^GetPersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-person
  "Update the given person's details.
  You can use the `-me-` string in place of `person-id` to specify the currently authenticated user.
  If applicable, the given person's login access can also be optionally disabled or re-enabled.
  You must have admin rights to update a person — unless updating your own details.
  If you are changing your password, as a non-admin user, then the existing password must also be supplied (using the old-password field in addition to the new password value).
  Admin users cannot be disabled by setting enabled to false.
  Non-admin users may not disable themselves.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/people/updatePerson)."
  ([^Ticket ticket ^String person-id ^UpdatePersonBody body]
   (update-person ticket person-id body nil))
  ([^Ticket ticket ^String person-id ^UpdatePersonBody body ^UpdatePersonQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/people/%s" (config/get-url 'core) person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))