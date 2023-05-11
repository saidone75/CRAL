(ns cral.utils.utils)

(require '[clojure.string :as str]
         '[clojure.walk :as walk])

(defn kebab-case
  "Turn a camelCase string into kebab-case."
  [s]
  (->> s
       (#(str/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(str/lower-case %))
       (str/join "-")))

(defn kebab-keywordize-keys
  "Recursively transforms all map keys from camelCase to kebab-case and keywordize them."
  [m]
  (let [f (fn [[k v]] (if (string? k) [((comp keyword kebab-case) k) v] [k v]))]
    ;; only apply to maps
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))