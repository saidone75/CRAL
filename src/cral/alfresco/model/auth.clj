(ns cral.alfresco.model.auth)

(defrecord Ticket
  [^String id
   ^String user-id])