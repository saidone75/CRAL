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

(ns cral.utils.utils
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [cral.model.auth]
            [taoensso.telemere :as t])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (java.util Base64)))

(defn- clean
  [s]
  (str/replace s #"^:+" ""))

(defn kebab-case
  "Turns a camelCase string into kebab-case."
  [s]
  (->> s
       (#(str/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(str/lower-case %))
       (str/join "-")))

(defn camel-case
  "Turns a kebab-case string into camelCase."
  [s]
  (let [s (str/split s #"-")]
    (if (> (count s) 1)
      (->> s
           (map #(str/capitalize %))
           (str/join)
           (#(str (str/lower-case (subs % 0 1)) (subs % 1))))
      (first s))))

(defn- *-keywordize-keys
  [m *-case]
  (let [f (fn [[k v]] [(keyword (*-case (clean k))) v])]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn kebab-keywordize-keys
  "Recursively transforms all map keys from camelCase to kebab-case and keywordize them."
  [m]
  (*-keywordize-keys m kebab-case))

(defn camel-case-keywordize-keys
  "Recursively transforms all map keys from kebab-case to camelCase and keywordize them."
  [m]
  (*-keywordize-keys m camel-case))

(defn camel-case-stringify-keys
  "Recursively transforms all map keys from kebab-case to camelCase and stringify them eventually excluding keys in
  `exclude`."
  ([m]
   (camel-case-stringify-keys m #{}))
  ([m exclude]
   (let [f (fn [[k v]] [(if-not (contains? exclude k) (camel-case (if (keyword? k) (subs (str k) 1) k)) (name k)) v])]
     ;; only apply to maps
     (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m))))

(defn join-vector-vals
  "Recursively transforms all map vector values to comma separated strings."
  [m]
  (let [f (fn [[k v]] [k (if (vector? v) (str/join "," v) v)])]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn ok-response
  "Builds a successful response."
  [r return-headers]
  (let [response {:status (:status r)
                  :body   (if (and (not (nil? (:body r))) (not (empty? (:body r))) (string? (:body r)))
                            (kebab-keywordize-keys (json/read-str (:body r)))
                            (:body r))}]
    (if (true? return-headers)
      (assoc response :headers (:headers r))
      response)))

(defn ex-response
  "Builds a response from a client exception."
  [^Exception e]
  (t/trace! e)
  (let [ex-data (ex-data e)]
    (if-not (nil? ex-data)
      (let [message (get-in (kebab-keywordize-keys (json/read-str (:body ex-data))) [:error :error-key])]
        (t/log! :debug message)
        {:status  (:status ex-data)
         :message message})
      (do
        (t/log! :debug (.getMessage e))
        {:status  500
         :message (.getMessage e)}))))

(defn- add-auth
  "Adds authorization header from ticket."
  [ticket req]
  (if (nil? ticket)
    req
    (assoc-in req [:headers "Authorization"] (str "Basic " (.encodeToString (Base64/getEncoder) (byte-array (map (comp byte int) (:id ticket))))))))

(defn call-rest
  ([method ^String url ^Ticket ticket]
   (call-rest method url ticket nil false))
  ([method ^String url ^Ticket ticket ^PersistentHashMap req]
   (call-rest method url ticket req false))
  ([method ^String url ^Ticket ticket ^PersistentHashMap req ^PersistentHashMap opts]
   (try
     (let [req (assoc req :query-params (join-vector-vals (camel-case-stringify-keys (into {} (remove #(nil? (val %)) (:query-params req))))))
           _ (t/log! :trace (format "method => %s" method))
           _ (t/log! :trace (format "url => %s" url))
           _ (t/log! :trace (format "req => %s" req))
           res (method url (add-auth ticket req))
           _ (t/log! :trace (format "res => %s" res))]
       (ok-response res (:return-headers opts)))
     (catch Exception e (ex-response e)))))