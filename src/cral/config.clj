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

;; defaults
(defonce alfresco (atom {:scheme         "http"
                         :host           "localhost"
                         :port           8080
                         :core-path      "alfresco/api/-default-/public/alfresco/versions/1"
                         :search-path    "alfresco/api/-default-/public/search/versions/1"
                         :auth-path      "alfresco/api/-default-/public/authentication/versions/1"
                         :discovery-path "alfresco/api/discovery"}))

;; shorthand used in tests
(def user "admin")
(def password "admin")

(defn configure
  ""
  [& [m]]
  ;; configure alfresco
  (swap! alfresco merge m)
  (if-not (nil? (:user m)) (alter-var-root #'user (constantly (:user @alfresco))))
  (if-not (nil? (:password m)) (alter-var-root #'password (constantly (:password @alfresco)))))

;; configure telemere
(defn configure-logging
  ""
  [& [m]]
  (t/set-kind-filter! (:kind-filter m))
  (t/set-ns-filter! (:ns-filter m)))

;; load global config map
(defn load-config
  ""
  [m]
  (configure (:alfresco m))
  (configure-logging (:telemere m)))

(defn get-url
  ""
  [path]
  (let [path (keyword (str path "-path"))]
    (format "%s://%s:%s/%s" (:scheme @alfresco) (:host @alfresco) (:port @alfresco) (path @alfresco))))