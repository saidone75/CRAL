(ns cral.alfresco.model.search)

(defrecord RequestQuery [^String language ^String user-query ^String query])
(defrecord Paging [^Integer max-items ^Integer skip-count])
(defrecord SearchBody [^RequestQuery query ^Paging paging])