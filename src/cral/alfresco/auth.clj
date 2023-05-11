(ns cral.alfresco.auth
  (:import (java.util Base64)))

(require '[clojure.string :as s]
         '[clojure.data.json :as json]
         '[clj-http.lite.client :as client]
         '[cral.utils.utils :as utils])

(defonce config (atom {:scheme "http"
                       :host   "localhost"
                       :port   8080
                       :path   "alfresco/api/-default-/public/authentication/versions/1"}))

(defn configure [& [m]]
  (swap! config merge m))

(defn- get-url []
  (str (:scheme @config) "://" (:host @config) ":" (:port @config) "/" (:path @config)))

(defn get-ticket
  "Create a ticket."
  [username password]
  (utils/keywordize-kebab
    (get
      (json/read-str
        (:body (client/post (str (get-url) "/tickets")
                            {:content-type :json
                             :body         (json/write-str {
                                                            :userId   username
                                                            :password password})})))
      "entry")))

(defn- *-ticket [method ticket]
  (:status (method (str (get-url) "/tickets/-me-")
                   {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}})))

(defn validate-ticket
  "Validate a ticket."
  [ticket]
  (*-ticket client/get ticket))

(defn delete-ticket
  "Delete a ticket."
  [ticket]
  (*-ticket client/delete ticket))