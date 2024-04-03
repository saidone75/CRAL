(ns cral.groups-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.groups :as groups]
            [cral.alfresco.model.core :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-list-then-delete-group-memberships
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-group-id "GROUP_ALFRESCO_ADMINISTRATORS"
        group-id (.toString (UUID/randomUUID))
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))]
    (is (= (:status create-group-response) 201))
    ;; create group membership
    (is (= (:status (->> (model/map->CreateGroupMembershipBody {:id (str "GROUP_" group-id) :member-type "GROUP"})
                             (groups/create-group-membership ticket parent-group-id))) 201))
    ;; ensure that the group membership has been created
    (loop [list-group-membership-response (groups/list-group-memberships ticket parent-group-id)]
      (if-not (= true
                 (->> (get-in list-group-membership-response [:body :list :entries])
                      (map #(get-in % [:entry :display-name]))
                      (some #(= group-id %))))
        (do
          (Thread/sleep 1000)
          (recur (groups/list-group-memberships ticket parent-group-id)))))
    ;; delete group membership
    (is (= (:status (groups/delete-group-membership ticket parent-group-id (str "GROUP_" group-id))) 204))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))))

(deftest create-then-delete-group
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))]
    (is (= (:status create-group-response) 201))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))))

(deftest list-groups
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        list-groups-response (groups/list-groups ticket)]
    (is (= (:status list-groups-response) 200))
    (is (->> list-groups-response
             (#(get-in % [:body :list :entries]))
             (map #(get-in % [:entry :id]))
             (some #(= "GROUP_ALFRESCO_ADMINISTRATORS" %))))))

(deftest get-group-details
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        get-group-details-response (groups/get-group-details ticket "GROUP_ALFRESCO_ADMINISTRATORS")]
    (is (= (:status get-group-details-response) 200))
    (is (get-in get-group-details-response [:body :entry :is-root]))))