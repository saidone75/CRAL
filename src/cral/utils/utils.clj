(ns cral.utils.utils)

(require '[clojure.string :as s]
         '[clojure.walk :as w])

(defn kebab-case
  "Turn a camelCase string into kebab-case."
  [s]
  (->> s
       (#(s/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(s/lower-case %))
       (s/join "-")))

(defn kebab-keywordize-keys
  "Recursively transforms all map keys from camelCase to kebab-case and keywordize them."
  [m]
  (let [f (fn [[k v]] (if (string? k) [((comp keyword kebab-case) k) v] [k v]))]
    ;; only apply to maps
    (w/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))