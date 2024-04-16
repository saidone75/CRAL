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

(ns cral.api.model
  (:require [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.model.model]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.model ListAspectsQueryParams ListTypesQueryParams)))

(defn list-aspects
  "Gets a list of aspects from the data dictionary. The System aspects will be ignored by default.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/aspects/listAspects)."
  ([^Ticket ticket]
   (list-aspects ticket nil))
  ([^Ticket ticket ^ListAspectsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/aspects" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-aspect
  "Get information for aspect `aspect-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/aspects/getAspect)."
  [^Ticket ticket ^String aspect-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/aspects/%s" (config/get-url 'core) (name aspect-id))
    ticket
    nil
    opts))

(defn list-types
  "Gets a list of types from the data dictionary. The System types will be ignored by default.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/types/listTypes)."
  ([^Ticket ticket]
   (list-types ticket nil))
  ([^Ticket ticket ^ListTypesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/types" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))

(defn get-type
  "Get information for type `type-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Model%20API#/types/getType)."
  [^Ticket ticket ^String type-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/get
    (format "%s/types/%s" (config/get-url 'core) (name type-id))
    ticket
    nil
    opts))