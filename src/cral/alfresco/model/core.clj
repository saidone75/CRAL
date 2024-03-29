(ns cral.alfresco.model.core
  (:import (clojure.lang PersistentHashMap PersistentVector)))

;; keep same order as fn, bodies first then params

;; activities
(defrecord ListActivitiesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

;; comments
(defrecord ListCommentsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord CreateCommentBody
  [^String content])

(defrecord CreateCommentQueryParams
  [^PersistentVector fields])

(defrecord UpdateCommentBody
  [^String content])

(defrecord UpdateCommentQueryParams
  [^PersistentVector fields])

;; downloads
(defrecord CreateDownloadBody
  [^PersistentVector node-ids])

(defrecord CreateDownloadQueryParams
  [^PersistentVector fields])

(defrecord GetDownloadQueryParams
  [^PersistentVector fields])

;; favorites
(defrecord ListFavoritesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord CreateFavoriteBody
  [^PersistentHashMap target])

(defrecord CreateFavoriteQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

;; nodes
(defrecord GetNodeQueryParams
  [^PersistentVector include
   ^String relative-path
   ^PersistentVector fields])

(defrecord UpdateNodeBody
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

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

(defrecord CreateNodeBody
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defrecord CreateNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord UpdateNodeContentQueryParams
  [^Boolean major-version
   ^String comment
   ^String name
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord CreateSecondaryChildBody
  [^String child-id
   ^String assoc-type])

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

(defrecord ListParentsQueryParams
  [^String where
   ^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^Boolean include-source
   ^PersistentVector fields])

;; reorder progress bookmark

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

(defrecord LocallySet
  [^String authority-id
   ^String name
   ^String access-status])

(defrecord Permissions
  [^Boolean is-inheritance-enabled
   ^PersistentVector locally-set])

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

;; people

(defrecord CreatePersonQueryParams
  [^PersistentVector fields])

(defrecord ListPeopleQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord CreatePersonBody
  [^String id
   ^String first-name
   ^String last-name
   ^String description
   ^String email
   ^String skype-id
   ^String google-id
   ^String instant-message-id
   ^String job-title
   ^String location
   ^PersistentHashMap company
   ^String mobile
   ^String telephone
   ^String user-status
   ^Boolean enabled
   ^Boolean email-notifications-enabled
   ^String password
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

;; FIXME Company definition missing

(defrecord GetPersonQueryParams
  [^PersistentVector fields])

(defrecord UpdatePersonBody
  [^String first-name
   ^String last-name
   ^String description
   ^String email
   ^String skype-id
   ^String google-id
   ^String instant-message-id
   ^String job-title
   ^String location
   ^PersistentHashMap company
   ^String mobile
   ^String telephone
   ^String user-status
   ^Boolean enabled
   ^Boolean email-notifications-enabled
   ^String password
   ^String old-password
   ^PersistentVector aspect-names
   ^PersistentHashMap properties])

(defrecord UpdatePersonQueryParams
  [^PersistentVector fields])

;; groups

(defrecord ListGroupMembershipQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector include
   ^String where
   ^PersistentVector fields])

(defrecord ListGroupsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector include
   ^String where
   ^PersistentVector fields])

(defrecord CreateGroupBody
  [^String id
   ^String display-name
   ^PersistentVector parent-ids])

(defrecord CreateGroupQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord GetGroupDetailsQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord UpdateGroupBody
  [^String display-name])

(defrecord UpdateGroupQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord DeleteGroupQueryParams
  [^Boolean cascade])

(defrecord CreateGroupMembershipBody
  [^String id
   ^String member-type])

(defrecord CreateGroupMembershipQueryParams
  [^PersistentVector fields])

(defrecord ListGroupMembershipsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector fieds])

;; sites

(defrecord ListSiteMembershipRequestsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

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

(defrecord ListTagsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields
   ^PersistentVector include])

(defrecord GetTagQueryParams
  [^PersistentVector fields])

(defrecord UpdateTagBody
  [^String tag])

(defrecord UpdateTagQueryParams
  [^PersistentVector fields])