(ns cral.groups-test
  (:import (java.util UUID))
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.groups :as groups]
            [cral.alfresco.model.core :as model]))

(def user "admin")
(def pass "admin")

;; incomplete

(deftest create-then-list-then-delete-group-memberships
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))
        create-group-membership-response (groups/create-group-membership ticket "GROUP_ALFRESCO_ADMINISTRATORS" (model/map->CreateGroupMembershipBody {:id (str "GROUP_" group-id) :member-type "GROUP"}))]
    (is (= 201 (:status create-group-membership-response)))
    ;(println create-group-membership-response)
    (->> (get-in (groups/list-group-memberships ticket "GROUP_ALFRESCO_ADMINISTRATORS") [:body :list :entries])
         (map #(get-in % [:entry]))
         ;(some #(= group-id %))
         )))

(deftest create-then-delete-group
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))]
    (is (= 201 (:status create-group-response)))
    ;; clean up
    (is (= 204 (:status (groups/delete-group ticket group-id))))))

(deftest list-groups
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        list-groups-response (groups/list-groups ticket)]
    (is (= 200 (:status list-groups-response)))
    (is (->> list-groups-response
             (#(get-in % [:body :list :entries]))
             (map #(get-in % [:entry :id]))
             (some #(= "GROUP_ALFRESCO_ADMINISTRATORS" %))))))

(deftest get-group-details
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        get-group-details-response (groups/get-group-details ticket "GROUP_ALFRESCO_ADMINISTRATORS")]
    (is (= 200 (:status get-group-details-response)))
    (is (get-in get-group-details-response [:body :entry :is-root]))))