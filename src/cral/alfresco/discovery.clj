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

(ns cral.alfresco.discovery
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (cral.alfresco.model.auth Ticket)))

(defn get-repo-info
  "Retrieves the capabilities and detailed version information from the repository.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Discovery%20API#/discovery/getRepositoryInformation)."
  [^Ticket ticket]
  (utils/call-rest
    client/get
    (config/get-url 'discovery)
    ticket
    nil
    nil))