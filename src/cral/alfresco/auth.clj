(ns cral.alfresco.auth
  (:import (java.util Base64))
  (:require [clojure.data.json :as json]
            [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]))

(defn get-ticket
  "Create a ticket."
  [username password]
  (try
    (let [response
          (client/post (str (config/get-url 'auth) "/tickets")
                       {:content-type :json
                        :body         (json/write-str {
                                                       :userId   username
                                                       :password password})})]
      (utils/ok-response response))
    (catch Exception e (utils/ex-response e))))

(defn- *-ticket [method ticket]
  (method (str (config/get-url 'auth) "/tickets/-me-")
          {:headers {"Authorization" (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket))))}}))

(defn validate-ticket
  "Validate a ticket."
  [ticket]
  (try
    (let [response
          (*-ticket client/get ticket)]
      (utils/ok-response response))
    (catch Exception e (utils/ex-response e))))

(defn delete-ticket
  "Delete a ticket."
  [ticket]
  (try
    (let [response
          (*-ticket client/delete ticket)]
      (utils/ok-response response))
    (catch Exception e (utils/ex-response e))))