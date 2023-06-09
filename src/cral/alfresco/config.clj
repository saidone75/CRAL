(ns cral.alfresco.config
  (:require [taoensso.timbre :as timbre]))

(defonce config (atom {:scheme      "http"
                       :host        "localhost"
                       :port        8080
                       :core-path   "alfresco/api/-default-/public/alfresco/versions/1"
                       :search-path "alfresco/api/-default-/public/search/versions/1"
                       :auth-path   "alfresco/api/-default-/public/authentication/versions/1"}))

(defn configure [& [m]]
  (swap! config merge m))

(defn set-log-level
  [level]
  (timbre/set-config! {:min-level level}))

(defn get-url [path]
  (let [path (keyword (str path "-path"))]
    (format "%s://%s:%s/%s" (:scheme @config) (:host @config) (:port @config) (path @config))))