(ns cral.alfresco.search
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.search SearchBody)))

(defn search
  "Searches Alfresco"
  [^Ticket ticket ^SearchBody search-request & [^PersistentHashMap opts]]
  (utils/call-rest
    client/post
    (format "%s/search" (config/get-url 'search))
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys search-request))
     :content-type :json}
    opts))