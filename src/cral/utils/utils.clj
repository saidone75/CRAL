(ns cral.utils.utils)

(require '[clojure.string :as s])

(defn kebab-case [s]
  (->> s
       (#(s/split % #"(?<=[a-z])(?=[A-Z])"))
       (map #(s/lower-case %))
       (s/join "-")))

(defn keywordize-kebab [m]
  (into {}
        (for [[k v] m]
          [(keyword (kebab-case k)) v])))