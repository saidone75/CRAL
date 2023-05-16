(ns cral.utils.utils
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.data.json :as json]))

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

(defn ok-response
  "Build a successful response."
  [r]
  {:status (:status r)
   :body   (if (and (not (nil? (:body r))) (not (empty? (:body r))))
             (kebab-keywordize-keys (json/read-str (:body r)))
             nil)})

(defn ex-response
  "Build a response from a client exception."
  [e]
  (let [ex-data (ex-data e)
        body (kebab-keywordize-keys (json/read-str (:body ex-data)))]
    {:status  (:status ex-data)
     :message (get-in body [:error :brief-summary])
     :body    body}))