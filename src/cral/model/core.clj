;
;  CRAL
;  Copyright (C) 2023-2026 Saidone
;  SPDX-License-Identifier: GPL-3.0-or-later
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.
;

(ns cral.model.core
  (:require [cral.model.body :refer [defbody]])
  (:import (clojure.lang PersistentHashMap PersistentVector)))

;; permissions
(defbody LocallySet
  [^String authority-id
   ^String name
   ^String access-status])

(defbody Permissions
  [^Boolean is-inheritance-enabled
   ^PersistentVector locally-set])

;; keep same order as fn, bodies first then params

;; audit
(defrecord ListAuditApplicationsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord GetAuditApplicationInfoQueryParams
  [^PersistentVector fields
   ^PersistentVector include])

(defbody UpdateAuditApplicationInfoBody
  [^Boolean is-enabled])

(defrecord UpdateAuditApplicationInfoQueryParams
  [^PersistentVector fields])

(defrecord ListAuditApplicationEntriesQueryParams
  [^Integer skip-count
   ^Boolean omit-total-items
   ^PersistentVector order-by
   ^Integer max-items
   ^String where
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord DeleteAuditApplicationEntriesQueryParams
  [^String where])

(defrecord GetAuditEntryQueryParams
  [^PersistentVector fields])

(defrecord ListNodeAuditEntriesQueryParams
  [^Integer skip-count
   ^PersistentVector order-by
   ^Integer max-items
   ^String where
   ^PersistentVector include
   ^PersistentVector fields])

;; actions
(defrecord ListNodeActionsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector fields])

(defrecord ListAvailableActionsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector fields])

(defbody ExecuteActionBody
  [^String action-definition-id
   ^String target-id
   ^PersistentHashMap params])

;; activities
(defrecord ListActivitiesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^String who
   ^String site-id
   ^PersistentVector fields])

;; comments
(defrecord ListCommentsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody CreateCommentBody
  [^String content])

(defrecord CreateCommentQueryParams
  [^PersistentVector fields])

(defbody UpdateCommentBody
  [^String content])

(defrecord UpdateCommentQueryParams
  [^PersistentVector fields])

;; downloads
(defbody CreateDownloadBody
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

(defbody CreateFavoriteBody
  [^PersistentHashMap target])

(defrecord CreateFavoriteQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord GetFavoriteQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

;; nodes
(defrecord GetNodeQueryParams
  [^PersistentVector include
   ^String relative-path
   ^PersistentVector fields])

(defbody UpdateNodeBody
  [^String name
   ^String node-type
   ^PersistentVector aspect-names
   ^PersistentHashMap properties
   ^Permissions permissions])

(defrecord UpdateNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord DeleteNodeQueryParams
  [^Boolean permanent])

(defrecord ListNodeChildrenQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector include
   ^String relative-path
   ^Boolean include-source
   ^PersistentVector fields])

(defbody CreateNodeBody
  [^String name
   ^String node-type
   ^PersistentHashMap properties])

(defrecord CreateNodeQueryParams
  [^Boolean auto-rename
   ^Boolean major-version
   ^Boolean versioning-enabled
   ^PersistentVector include
   ^PersistentVector fields])

(defbody CopyNodeBody
  [^String target-parent-id
   ^String name])

(defrecord CopyNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defbody LockNodeBody
  [^Integer time-to-expire
   ^String type
   ^String lifetime])

(defrecord LockNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord UnLockNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defbody MoveNodeBody
  [^String target-parent-id
   ^String name])

(defrecord MoveNodeQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord GetNodeContentQueryParams
  [^Boolean attachment])

(defrecord UpdateNodeContentQueryParams
  [^Boolean major-version
   ^String comment
   ^String name
   ^PersistentVector include
   ^PersistentVector fields])

(defbody RequestDirectAccessUrlBody
  [^Boolean attachment])

(defbody CreateSecondaryChildBody
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

(defbody CreateNodeAssocsBody
  [^String target-id
   ^String assoc-type])

(defrecord CreateNodeAssocsQueryParams
  [^PersistentVector fields])

(defrecord ListTargetAssocsQueryParams
  [^String where
   ^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord DeleteNodeAssocsQueryParams
  [^String assoc-type])

(defrecord ListSourceAssocsQueryParams
  [^String where
   ^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

;; people
(defbody CreatePersonBody
  ;; TODO Company definition missing
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

(defrecord CreatePersonQueryParams
  [^PersistentVector fields])

(defrecord ListPeopleQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord GetPersonQueryParams
  [^PersistentVector fields])

(defbody UpdatePersonBody
  ;; TODO Company definition missing
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

(defbody CreateGroupBody
  [^String id
   ^String display-name
   ^String description
   ^PersistentVector parent-ids])

(defrecord CreateGroupQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord GetGroupDetailsQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defbody UpdateGroupBody
  [^String display-name
   ^String description])

(defrecord UpdateGroupQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord DeleteGroupQueryParams
  [^Boolean cascade])

(defbody CreateGroupMembershipBody
  [^String id
   ^String member-type])

(defrecord CreateGroupMembershipQueryParams
  [^PersistentVector fields])

(defrecord ListGroupMembershipsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^String where
   ^PersistentVector fields])

;; preferences
(defrecord ListPreferencesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord GetPreferenceQueryParams
  [^PersistentVector fields])

;; queries
(defrecord FindNodesQueryParams
  [^String term
   ^String root-node-id
   ^Integer skip-count
   ^Integer max-items
   ^String node-type
   ^PersistentVector include
   ^PersistentVector order-by
   ^PersistentVector fields])

(defrecord FindSitesQueryParams
  [^String term
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector fields])

(defrecord FindPeopleQueryParams
  [^String term
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields
   ^PersistentVector order-by])

;; ratings
(defrecord ListRatingsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody CreateRatingBody
  [^String id
   ^Object my-rating])

(defrecord CreateRatingQueryParams
  [^PersistentVector fields])

(defrecord GetRatingQueryParams
  [^PersistentVector fields])

;; renditions
(defbody CreateRenditionBody
  [^String id])

(defrecord ListRenditionsQueryParams
  [^String where])

(defrecord GetRenditionContentQueryParams
  [^Boolean attachment
   ^Boolean placeholder])

;; shared-links
(defbody CreateSharedLinkBody
  [^String node-id
   ^String expires-at])

(defrecord CreateSharedLinkQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord ListSharedLinksQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^String where
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord GetSharedLinkQueryParams
  [^PersistentVector include])

(defrecord GetSharedLinkContentQueryParams
  [^Boolean attachment])

(defrecord GetSharedLinkRenditionContentQueryParams
  [^Boolean attachment])

(defbody EmailSharedLinkBody
  [^String client
   ^String message
   ^String locale
   ^PersistentVector recipient-emails])

;; sites
(defrecord ListPersonSiteMembershipRequestsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody CreatePersonSiteMembershipRequestBody
  [^String message
   ^String id
   ^String title
   ^String client])

(defrecord CreatePersonSiteMembershipRequestQueryParams
  [^PersistentVector fields])

(defrecord GetPersonSiteMembershipRequestsQueryParams
  [^PersistentVector fields])

(defbody UpdatePersonSiteMembershipRequestBody
  [^String message])

(defrecord UpdatePersonSiteMembershipRequestQueryParams
  [^PersistentVector fields])

(defrecord ListPersonSiteMembershipsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector relations
   ^PersistentVector fields
   ^String where])

(defrecord ListSitesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector order-by
   ^PersistentVector relations
   ^PersistentVector fields
   ^String where])

(defbody CreateSiteBody
  [^String id
   ^String title
   ^String description
   ^String visibility])

(defrecord CreateSiteQueryParams
  [^Boolean skip-configuration
   ^Boolean skip-add-to-favorites
   ^PersistentVector fields])

(defrecord GetSiteQueryParams
  [^PersistentHashMap relations
   ^PersistentVector fields])

(defbody UpdateSiteBody
  [^String title
   ^String description
   ^String visibility])

(defrecord UpdateSiteQueryParams
  [^PersistentVector fields])

(defrecord DeleteSiteQueryParams
  [^Boolean permanent])

(defrecord ListSiteContainersQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defrecord GetSiteContainerQueryParams
  [^PersistentVector fields])

(defrecord GetSiteMembershipRequestsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^String where
   ^PersistentVector fields])

(defbody ApproveSiteMembershipBody
  [^String role])

(defbody RejectSiteMembershipBody
  [^String comment])

(defrecord ListSiteMembershipsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields
   ^String where])

(defbody CreateSiteMembershipBody
  [^String role
   ^String id])

(defrecord CreateSiteMembershipQueryParams
  [^PersistentVector fields])

(defrecord GetSiteMembershipsQueryParams
  [^PersistentVector fields])

(defbody UpdateSiteMembershipBody
  [^String role])

(defrecord UpdateSiteMembershipQueryParams
  [^PersistentVector fields])

(defrecord ListGroupSiteMembershipsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody CreateGroupSiteMembershipBody
  [^String role
   ^String id])

(defrecord CreateGroupSiteMembershipQueryParams
  [^PersistentVector fields])

(defrecord GetGroupSiteMembershipQueryParams
  [^PersistentVector fields])

(defbody UpdateGroupSiteMembershipBody
  [^String role])

(defrecord UpdateGroupSiteMembershipQueryParams
  [^PersistentVector fields])

;; tags
(defrecord ListNodeTagsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody CreateNodeTagBody
  [^String tag])

(defrecord CreateNodeTagQueryParams
  [^PersistentVector fields])

(defrecord ListTagsQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields
   ^PersistentVector include
   ^PersistentVector order-by
   ^String where])

(defbody CreateTagBody
  [^String tag])

(defrecord CreateTagQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord GetTagQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defbody UpdateTagBody
  [^String tag])

(defrecord UpdateTagQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

;; categories
(defrecord ListNodeCategoriesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector include
   ^PersistentVector fields])

(defrecord GetCategoryQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defbody UpdateCategoryBody
  [^String name])

(defrecord UpdateCategoryQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

(defrecord ListCategoriesQueryParams
  [^PersistentVector include
   ^Integer skip-count
   ^Integer max-items
   ^PersistentVector fields])

(defbody AssignNodeCategoryBody
  [^String category-id])

(defbody CreateCategoryBody
  [^String name])

(defrecord CreateCategoryQueryParams
  [^PersistentVector include
   ^PersistentVector fields])

;; trashcan
(defrecord ListDeletedNodesQueryParams
  [^Integer skip-count
   ^Integer max-items
   ^PersistentVector include])

(defrecord GetDeletedNodeQueryParams
  [^PersistentVector include])

(defrecord GetDeletedNodeContentQueryParams
  [^Boolean attachment])

(defbody RestoreDeletedNodeBody
  [^String target-parent-id
   ^String assoc-type])

(defrecord RestoreDeletedNodeQueryParams
  [^PersistentVector fields])

(defrecord ListDeletedNodeRenditionsQueryParams
  [^String where])

(defrecord GetDeletedNodeRenditionContentQueryParams
  [^Boolean attachment
   ^Boolean placeholder])

;; versions
(defrecord ListVersionHistoryQueryParams
  [^PersistentVector include
   ^PersistentVector fields
   ^Integer skip-count
   ^Integer max-items])

(defrecord GetVersionContentQueryParams
  [^Boolean attachment])

(defbody RevertVersionBody
  [^Boolean major-version
   ^String comment])

(defrecord RevertVersionQueryParams
  [^PersistentVector fields])

(defbody CreateVersionRenditionBody
  [^String id])

(defrecord ListVersionRenditionsQueryParams
  [^String where])

(defrecord GetVersionRenditionContentQueryParams
  [^Boolean attachment
   ^Boolean placeholder])