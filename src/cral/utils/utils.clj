(ns cral.utils.utils)

(require '[clojure.string :as s])

(defn kebab-case
  "Turn a camelCase string into kebab-case"
  [s]
  (->> s
       (#(s/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(s/lower-case %))
       (s/join "-")))

(defn keywordize-kebab [m]
  "Turn a camelCase string into kebab-case and keywordize it"
  (into {}
        (for [[k v] m]
          [(keyword (kebab-case k)) v])))