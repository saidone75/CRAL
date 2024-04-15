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
