(ns cral.alfresco.search
  (:import (java.util Base64)))

(require '[clojure.string :as str]
         '[clojure.data.json :as json]
         '[clj-http.lite.client :as client]
         '[cral.alfresco.config :as config]
         '[cral.utils.utils :as utils])

(defn search
  "Searches Alfresco"
  [ticket body]
  (utils/kebab-keywordize-keys
    (json/read-str
      (:body
        (client/post
          (str (config/get-url 'search) "/search")
          {:headers      {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}
           :body         (json/write-str body)
           :content-type :json})))))