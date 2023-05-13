(ns cral.alfresco.auth
  (:import (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defn get-ticket
  "Create a ticket."
  [username password]
  (utils/kebab-keywordize-keys
    (get
      (json/read-str
        (:body (client/post (str (config/get-url 'auth) "/tickets")
                            {:content-type :json
                             :body         (json/write-str {
                                                            :userId   username
                                                            :password password})})))
      "entry")))

(defn- *-ticket [method ticket]
  (:status (method (str (config/get-url 'auth) "/tickets/-me-")
                   {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}})))

(defn validate-ticket
  "Validate a ticket."
  [ticket]
  (*-ticket client/get ticket))

(defn delete-ticket
  "Delete a ticket."
  [ticket]
  (*-ticket client/delete ticket))