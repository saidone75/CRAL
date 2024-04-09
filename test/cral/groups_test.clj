(ns cral.groups-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.groups :as groups]
            [cral.alfresco.model.core :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest list-user-group-memberships-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; list user group membership
        list-user-group-memberships-response (groups/list-user-group-memberships ticket "-me-")]
    (is (= (:status list-user-group-memberships-response) 200))
    (is (= (count (filter #(= (get-in % [:entry :id]) "GROUP_ALFRESCO_ADMINISTRATORS") (get-in list-user-group-memberships-response [:body :list :entries]))) 1))))

(deftest list-groups-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; list groups
        list-groups-response (groups/list-groups ticket)]
    (is (= (:status list-groups-response) 200))
    (is (not (empty? (get-in list-groups-response [:body :list :entries]))))))

(deftest create-group-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        ;; create group
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))]
    (is (= (:status create-group-response) 201))
    (is (= (get-in create-group-response [:body :entry :display-name]) group-id))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))))

(deftest get-group-details-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; get group details
        get-group-details-response (groups/get-group-details ticket "GROUP_ALFRESCO_ADMINISTRATORS")]
    (is (= (:status get-group-details-response) 200))
    (is (get-in get-group-details-response [:body :entry :is-root]))))

(deftest update-group-details-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        ;; create group
        _ (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
               (groups/create-group ticket))
        new-display-name (.toString (UUID/randomUUID))
        ;; update group details
        update-group-details-response (->> (model/map->UpdateGroupBody {:display-name new-display-name})
                                           (groups/update-group-details ticket (format "GROUP_%s" group-id)))]
    (is (= (:status update-group-details-response) 200))
    (is (= (get-in update-group-details-response [:body :entry :display-name]) new-display-name))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (format "GROUP_%s" group-id))) 204))))

(deftest delete-group-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        group-id (.toString (UUID/randomUUID))
        ;; create group
        create-group-response (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
                                   (groups/create-group ticket))]
    ;; delete group
    (is (= (:status (groups/delete-group ticket (get-in create-group-response [:body :entry :id]))) 204))))

(deftest create-group-membership-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-group-id "GROUP_ALFRESCO_ADMINISTRATORS"
        group-id (.toString (UUID/randomUUID))
        ;; create group
        _ (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
               (groups/create-group ticket))]
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
          (Thread/sleep 100)
          (recur (groups/list-group-memberships ticket parent-group-id)))))
    ;; clean up
    (is (= (:status (groups/delete-group-membership ticket parent-group-id (str "GROUP_" group-id))) 204))
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))))

(deftest list-group-memberships-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        ;; list group membership
        list-group-membership-response (groups/list-group-memberships ticket "GROUP_ALFRESCO_ADMINISTRATORS")]
    (is (= (:status list-group-membership-response) 200))
    (is (not (empty? (get-in list-group-membership-response [:body :list :entries]))))))

(deftest delete-group-membership-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        parent-group-id "GROUP_ALFRESCO_ADMINISTRATORS"
        group-id (.toString (UUID/randomUUID))
        ;; create group
        _ (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
               (groups/create-group ticket))]
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
          (Thread/sleep 100)
          (recur (groups/list-group-memberships ticket parent-group-id)))))
    ;; delete group membership
    (is (= (:status (groups/delete-group-membership ticket parent-group-id (str "GROUP_" group-id))) 204))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))))