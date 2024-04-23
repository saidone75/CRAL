;  CRAL
;  Copyright (C) 2023-2024 Saidone
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns cral.config
  (:require [taoensso.telemere :as t]))

(defonce config (atom {:scheme         "http"
                       :host           "localhost"
                       :port           8080
                       :core-path      "alfresco/api/-default-/public/alfresco/versions/1"
                       :search-path    "alfresco/api/-default-/public/search/versions/1"
                       :auth-path      "alfresco/api/-default-/public/authentication/versions/1"
                       :discovery-path "alfresco/api/discovery"
                       :user           "admin"
                       :password       "admin"}))

(def user "admin")
(def password "admin")

(defn configure [& [m]]
  (swap! config merge m)
  (alter-var-root #'user (constantly (:user @config)))
  (alter-var-root #'password (constantly (:password @config))))

(defn set-log-level
  [level]
  (t/set-min-level! level))

(defn get-url [path]
  (let [path (keyword (str path "-path"))]
    (format "%s://%s:%s/%s" (:scheme @config) (:host @config) (:port @config) (path @config))))