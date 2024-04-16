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

(ns cral.alfresco.core.probes
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils]))

(defn probes
  "Check readiness and liveness of the repository.\\
  **Note:** this endpoint is available in Alfresco 6.0 and newer versions.\\
  Returns a status of 200 to indicate success and 503 for failure.
  The readiness probe is normally only used to check repository startup.
  The liveness probe should then be used to check the repository is still responding to requests.\\
  **Note:** No authentication is required to call this endpoint.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/probes/getProbe)."
  ([^String probe-id]
   (utils/call-rest
     client/get
     (format "%s/probes/%s" (config/get-url 'core) probe-id)
     nil
     nil
     nil)))