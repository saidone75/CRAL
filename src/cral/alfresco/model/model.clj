(ns cral.alfresco.model.model
  (:import (clojure.lang PersistentVector)))

(defrecord ListAspectsQueryParams
  [^String where
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector include])