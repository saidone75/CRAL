(ns cral.alfresco.search
  (:import (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defrecord Query [language user-query query])
(defn make-query
  [query & [language user-query]]
  (map->Query {:query query :language language :user-query user-query}))

(defrecord Paging [max-items skip-count])
(defn make-paging
  [max-items skip-count]
  (map->Paging {:max-items max-items :skip-count skip-count}))

(defrecord QueryBody [^Query query ^Paging paging])
(defn make-query-body
  [^Query query ^Paging & paging]
  (map->QueryBody {:query query :paging paging}))

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
