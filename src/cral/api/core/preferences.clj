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

(ns cral.api.core.preferences
  (:require [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core ListPreferencesQueryParams)))

(defn list-preferences
  "Gets a list of preferences for person `person-id`.\\
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  Note that each preference consists of an **id** and a **value**.\\
  The value can be of any JSON type.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/preferences/listPreferences)."
  ([^Ticket ticket ^String person-id]
   (list-preferences ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListPreferencesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/preferences" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))