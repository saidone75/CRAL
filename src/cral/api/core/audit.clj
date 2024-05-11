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
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core DeleteAuditApplicationEntriesQueryParams
                            GetAuditApplicationInfoQueryParams
                            ListAuditApplicationEntriesQueryParams
                            ListAuditApplicationsQueryParams
                            UpdateAuditApplicationInfoBody
                            UpdateAuditApplicationInfoQueryParams)))

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

(defn get-audit-application-info
  "Get status of an audit application `audit-application-id`.
  You must have admin rights to retrieve audit information.
  You can use the include parameter to return the minimum and/or maximum audit record id for the application.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/audit/getAuditApp)."
  ([^Ticket ticket ^String audit-application-id]
   (get-audit-application-info ticket audit-application-id nil))
  ([^Ticket ticket ^String audit-application-id ^GetAuditApplicationInfoQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/audit-applications/%s" (config/get-url 'core) audit-application-id)
     ticket
     {:query-params query-params}
     opts)))

(defn update-audit-application-info
  "Disable or re-enable the audit application `audit-application-id`.
  New audit entries will not be created for a disabled audit application until it is re-enabled (and system-wide auditing is also enabled).
  Note, it is still possible to query &/or delete any existing audit entries even if auditing is disabled for the audit application.
  You must have admin rights to update audit application.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/audit/updateAuditApp)."
  ([^Ticket ticket ^String audit-application-id ^UpdateAuditApplicationInfoBody body]
   (update-audit-application-info ticket audit-application-id body nil))
  ([^Ticket ticket ^String audit-application-id ^UpdateAuditApplicationInfoBody body ^UpdateAuditApplicationInfoQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/put
     (format "%s/audit-applications/%s" (config/get-url 'core) audit-application-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn list-audit-application-entries
  "Gets a list of audit entries for audit application `auditApplication-id`.
  You can use the include parameter in `query-params` to return additional values information.
  The list can be filtered by one or more of:
  - **created-by-user** person id
  - **created-at** inclusive time period
  - **id** inclusive range of ids
  - **values-key** audit entry values contains the exact matching key
  - **values-value** audit entry values contains the exact matching value
  - SiteContributor\n\n
  The default sort order is **created-at** ascending, but you can use an optional **ASC** or **DESC** modifier to specify an ascending or descending sort order.
  For example, specifying `order-by=created-at DESC` returns audit entries in descending **created-at** order.
  You must have admin rights to retrieve audit information.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/audit/listAuditEntriesForAuditApp)."
  ([^Ticket ticket ^String audit-application-id]
   (list-audit-application-entries ticket audit-application-id nil))
  ([^Ticket ticket ^String audit-application-id ^ListAuditApplicationEntriesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/audit-applications/%s/audit-entries" (config/get-url 'core) audit-application-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-audit-application-entries
  "Permanently delete audit entries for an audit application `auditApplication-id`.
  The `where` clause must be specified, either with an inclusive time period or for an inclusive range of ids. The delete is within the context of the given audit application.
  For example:
  - ```where=(createdAt BETWEEN ('2024-01-01T00:00:00' , '2024-02-01T00:00:00'))```
  - ```where=(id BETWEEN ('1234', '4321'))```\n\n
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/audit/deleteAuditEntriesForAuditApp)."
  ([^Ticket ticket ^String ^String audit-application-it ^DeleteAuditApplicationEntriesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/delete
     (format "%s/audit-applications/%s/audit-entries" (config/get-url 'core) audit-application-it)
     ticket
     {:query-params query-params}
     opts)))