(ns cral.alfresco.discovery
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (cral.alfresco.model.auth Ticket)))

(defn get-discovery
  ""
  [^Ticket ticket]
  (utils/call-rest
    client/get
    (config/get-url 'discovery)
    ticket
    nil
    nil))
