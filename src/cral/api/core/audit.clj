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

(ns cral.api.core.audit
  (:require [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core ListAuditApplicationsQueryParams)))

(defn list-audit-applications
  "Gets a list of audit applications in this repository.
  This list may include pre-configured audit applications, if enabled, such as:
  - alfresco-access
  - CMISChangeLog
  - Alfresco Tagging Service
  - Alfresco Sync Service (used by Enterprise Cloud Sync)\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/audit/listAuditApps)."
  ([^Ticket ticket]
   (list-audit-applications ticket nil))
  ([^Ticket ticket ^ListAuditApplicationsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/audit-applications" (config/get-url 'core))
     ticket
     {:query-params query-params}
     opts)))