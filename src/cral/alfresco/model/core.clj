(ns cral.alfresco.model.core
  (:import (clojure.lang PersistentHashMap PersistentVector)))

;; comments

(defrecord ListCommentsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord CreateCommentQueryParams
  [^PersistentVector fields])

(defrecord UpdateCommentQueryParams
  [^PersistentVector fields])

(defrecord CreateCommentBody
  [^String content])

(defrecord UpdateCommentBody
  [^String content])

;; nodes

(defrecord GetNodeQueryParams
  [^PersistentVector include
   ^String relative-path
   ^PersistentVector fields])

(defrecord UpdateNodeQueryParams
  [^Boolean permanent])

(defrecord DeleteNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord ListNodeChildrenQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector include
   ^String relative-path
   ^Boolean include-source
   ^PersistentVector fields])

(defrecord UpdateNodeContentQueryParams
  [^Boolean major-version
   ^String comment
   ^String name
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord CreateSecondaryChildQueryParams
  [^PersistentVector fields])

(defrecord ListSecondaryChildrenQueryParams
  [^String where
   ^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^Boolean include-source
   ^PersistentVector fields])

(defrecord DeleteSecondaryChildQueryParams
  [^String assoc-type])

(defrecord CreateNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord CopyNodeQueryParams
  [^Boolean auto-rename
   ^Boolean major-version
   ^Boolean versioning-enabled
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord LockNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord UnLockNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord MoveNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord ListParentsQueryParams
  [^String where
   ^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^Boolean include-source
   ^PersistentVector fields])

(defrecord CreateNodeAssocsQueryParams
  [^PersistentVector fields])

(defrecord ListTargetAssocsQueryParams
  [^String where
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord DeleteNodeAssocsQueryParams
  [^String assoc-type])

(defrecord ListSourceAssocsQueryParams
  [^String where
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord UpdateNodeBody
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

(defrecord CreateSecondaryChildBody
  [^String child-id
   ^String assoc-type])

(defrecord LocallySet
  [^String authority-id
   ^String name
   ^String access-status])

(defrecord Permissions
  [^Boolean is-inheritance-enabled
   ^PersistentVector locally-set])

(defrecord CreateNodeBody
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defrecord CopyNodeBody
  [^String target-parent-id
   ^String name])

(defrecord LockNodeBody
  [^Integer time-to-expire
   ^String type
   ^String lifetime])

(defrecord MoveNodeBody
  [^String target-parent-id
   ^String name])

(defrecord CreateNodeAssocsBody
  [^String target-id
   ^String assoc-type])

;; sites

(defrecord ListSitesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector relations
   ^PersistentVector fields
   ^String where])

(defrecord CreateSiteQueryParams
  [^Boolean skip-configuration
   ^Boolean skip-add-to-favorites
   ^PersistentVector fields])

(defrecord UpdateSiteQueryParams
  [^PersistentVector fields])

(defrecord DeleteSiteQueryParams
  [^Boolean permanent])

(defrecord GetSiteQueryParams
  [^PersistentHashMap relations
   ^PersistentVector fields])

(defrecord CreateSiteBody
  [^String id
   ^String title
   ^String description
   ^String visibility])

(defrecord UpdateSiteBody
  [^String title
   ^String description
   ^String visibility])

;; tags

(defrecord ListNodeTagsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord CreateNodeTagQueryParams
  [^PersistentVector fields])

(defrecord CreateNodeTagBody
  [^String tag])