(ns cral.alfresco.search
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defrecord RequestQuery [^String language ^String user-query ^String query])
(defrecord Paging [^Integer max-items ^Integer skip-count])
(defrecord SearchRequest [^RequestQuery query ^Paging paging])

(defn search
  "Searches Alfresco"
  [ticket ^SearchRequest search-request]
  (utils/call-rest
    client/post
    (str (config/get-url 'search) "/search")
    ticket
    {:body         (json/write-str (utils/camel-case-stringify-keys search-request))
     :content-type :json}))