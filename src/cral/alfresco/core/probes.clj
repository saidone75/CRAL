(ns cral.alfresco.core.probes
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils]))

(defn probes
  "Check readiness and liveness of the repository.
  **Note:** this endpoint is available in Alfresco 6.0 and newer versions.
  Returns a status of 200 to indicate success and 503 for failure.
  The readiness probe is normally only used to check repository startup.
  The liveness probe should then be used to check the repository is still responding to requests.
  **Note:** No authentication is required to call this endpoint.
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/probes/getProbe)."
  ([^String probe-id]
   (utils/call-rest
     client/get
     (format "%s/probes/%s" (config/get-url 'core) probe-id)
     nil
     nil
     nil)))