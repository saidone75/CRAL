(ns cral.alfresco.search
  (:import (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defrecord Query [^String language ^String user-query ^String query])
(defrecord Paging [^Integer max-items ^Integer skip-count])
(defrecord QueryBody [^Query query ^Paging paging])

(defn search
  "Searches Alfresco"
  [ticket ^QueryBody query-body]
  (utils/kebab-keywordize-keys
    (json/read-str
      (:body
        (client/post
          (str (config/get-url 'search) "/search")
          {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
           :body         (json/write-str (utils/camel-case-stringify-keys query-body))
           :content-type :json})))))
