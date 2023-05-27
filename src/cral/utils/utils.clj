(ns cral.utils.utils
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [cral.alfresco.model]
            [taoensso.timbre :as timbre])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model Ticket)
           (java.util Base64)))

(defn kebab-case
  "Turn a camelCase string into kebab-case."
  [s]
  (->> s
       (#(str/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(str/lower-case %))
       (str/join "-")))

(defn camel-case
  "Turn a kebab-case string into camelCase."
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
  (let [f (fn [[k v]] [(keyword (*-case k)) v])]
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
  "Recursively transforms all map keys from kebab-case to camelCase and stringify them."
  [m]
  (let [f (fn [[k v]] [(camel-case (if (keyword? k) (subs (str k) 1) k)) v])]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn join-vector-vals
  "Recursively transforms all map vector values to comma separated string."
  [m]
  (let [f (fn [[k v]] [k (if (vector? v) (str/join "," v) v)])]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn ok-response
  "Build a successful response."
  [r return-headers]
  (timbre/trace (with-out-str (clojure.pprint/pprint r)))
  (let [response {:status (:status r)
                  :body   (if (and (not (nil? (:body r))) (not (empty? (:body r))) (string? (:body r)))
                            (kebab-keywordize-keys (json/read-str (:body r)))
                            (:body r))}]
    (if (true? return-headers)
      (assoc response :headers (:headers r))
      response)))

(defn ex-response
  "Build a response from a client exception."
  [e]
  (timbre/trace (with-out-str (clojure.pprint/pprint e)))
  (let [ex-data (ex-data e)
        body (kebab-keywordize-keys (json/read-str (:body ex-data)))]
    {:status  (:status ex-data)
     :message (get-in body [:error :brief-summary])
     :body    body}))

(defn- add-auth
  "Add authorization header from ticket."
  [ticket req]
  (if (nil? ticket)
    req
    (assoc-in req [:headers "Authorization"] (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (:id ticket)))))))

(defn call-rest
  ([method ^String url ^Ticket ticket]
   (call-rest method url ticket nil false))
  ([method ^String url ^Ticket ticket ^PersistentHashMap req]
   (call-rest method url ticket req false))
  ([method ^String url ^Ticket ticket ^PersistentHashMap req ^Boolean return-headers]
   (try
     (let [response (method url (add-auth ticket (join-vector-vals req)))]
       (ok-response response return-headers))
     (catch Exception e (ex-response e)))))