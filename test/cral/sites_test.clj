;  CRAL
;  Copyright (C) 2023-2024 Saidone
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

(ns cral.sites-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.groups :as groups]
            [cral.api.core.sites :as sites]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.core :as model]
            [cral.test-utils :as tu])
  (:import (java.util UUID)))

(use-fixtures :once fixtures/setup)
(def saidone "saidone")

(deftest list-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))]
    ;; list membership requests
    (is (= (:status (sites/list-person-site-membership-requests saidone-ticket "-me-")) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest create-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])]
    (is (= (:status (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                             :id      site-id
                                                                             :title   (format "Request for %s site" site-id)})]
                         (sites/create-person-site-membership-requests saidone-ticket "-me-")) 201)))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))
        ;; get membership request
        get-site-membership-requests-response (sites/get-person-site-membership-request ticket saidone site-id)]
    (is (= (:status get-site-membership-requests-response) 200))
    (is (= (get-in get-site-membership-requests-response [:body :entry :site :id]) site-id))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest update-site-membership-request-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))]
    ;; update membership request
    (is (= (:status (->> (model/map->UpdatePersonSiteMembershipRequestBody {:message "New message"})
                         (sites/update-person-site-membership-request saidone-ticket "-me-" site-id))) 200))
    ;; check if request has been updated
    (is (= (get-in (sites/get-person-site-membership-request ticket saidone site-id) [:body :entry :message]) "New message"))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest delete-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))]
    ;; delete membership request
    (is (= (:status (sites/delete-person-site-membership-request saidone-ticket "-me-" site-id)) 204))
    ;; check if request has been deleted
    (is (= (:status (sites/get-person-site-membership-request ticket saidone site-id)) 404))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest list-site-memberships-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])]
    ;; check if list is empty
    (is (= (empty? (get-in (sites/list-person-site-memberships saidone-ticket "-me-") [:body :list :entries])) true))
    ;; join site
    (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                             :id      site-id
                                                             :title   (format "Request for %s site" site-id)})]
         (sites/create-person-site-membership-requests saidone-ticket "-me-"))
    ;; check if list is not empty
    (is (= (not (empty? (get-in (sites/list-person-site-memberships saidone-ticket "-me-") [:body :list :entries]))) true))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])]
    ;; join site
    (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                             :id      site-id
                                                             :title   (format "Request for %s site" site-id)})]
         (sites/create-person-site-membership-requests saidone-ticket "-me-"))
    ;; get site membership
    (is (= (:status (sites/get-person-site-membership saidone-ticket "-me-" site-id)) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest delete-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])]
    ;; join site
    (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                             :id      site-id
                                                             :title   (format "Request for %s site" site-id)})]
         (sites/create-person-site-membership-requests saidone-ticket "-me-"))
    ;; get site membership
    (is (= (:status (sites/get-person-site-membership saidone-ticket "-me-" site-id)) 200))
    ;; delete site membership
    (is (= (:status (sites/delete-person-site-membership saidone-ticket "-me-" site-id)) 204))
    ;; check if membership has been deleted
    (is (= (:status (sites/get-person-site-membership saidone-ticket "-me-" site-id)) 404))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest list-sites-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        list-sites-response (sites/list-sites ticket)]
    ;; list sites
    (is (= (:status list-sites-response) 200))
    (is (some #(= (get-in % [:entry :id]) site-id) (get-in list-sites-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest create-site-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))]
    ;; create a public site
    (is (= (:status (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
                         (sites/create-site ticket))) 201))
    ;; check if site has been created
    (is (some #(= (get-in % [:entry :id]) site-id) (get-in (sites/list-sites ticket) [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        get-site-response (sites/get-site ticket site-id)]
    (is (= (:status get-site-response) 200))
    (is (= (get-in get-site-response [:body :entry :id]) site-id))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest update-site-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        new-title (.toString (UUID/randomUUID))]
    ;; update site with a new title
    (is (= (:status (->> (model/map->UpdateSiteBody {:title new-title :visibility "PUBLIC"})
                         (sites/update-site ticket site-id))) 200))
    ;; check if title has been changed
    (is (= (get-in (sites/get-site ticket site-id) [:body :entry :title]) new-title))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest delete-site-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))]
    (is (= (:status (sites/get-site ticket site-id)) 200))
    ;; delete site
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))
    ;; check if site has been deleted
    (is (= (:status (sites/get-site ticket site-id)) 404))))

(deftest list-site-containers-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))]
    (is (= (:status (sites/list-site-containers ticket site-id)) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-container-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        containers (get-in (sites/list-site-containers ticket site-id) [:body :list :entries])]
    (is (= (:status (sites/get-site-container ticket site-id (get-in (rand-nth containers) [:entry :folder-id]))) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create a membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))
        ;; get site membership requests
        get-site-membership-requests-response (sites/get-site-membership-request ticket)]
    (is (= (:status get-site-membership-requests-response) 200))
    (is (some #(= (get-in % [:entry :person :id]) saidone) (get-in get-site-membership-requests-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest approve-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create a membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))]
    (is (= (:status (sites/approve-site-membership-request ticket site-id saidone (model/map->ApproveSiteMembershipBody {:role sites/contributor}))) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest reject-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create a membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))]
    (is (= (:status (sites/reject-site-membership-request ticket site-id saidone (model/map->RejectSiteMembershipBody {:comment "Rejected"}))) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest list-site-memberships-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a public site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create a membership request
        _ (->> [(model/map->CreatePersonSiteMembershipRequestBody {:message "Please can you add me"
                                                                   :id      site-id
                                                                   :title   (format "Request for %s site" site-id)})]
               (sites/create-person-site-membership-requests saidone-ticket "-me-"))
        list-site-memberships-response (sites/list-site-memberships ticket site-id)]
    (is (= (:status list-site-memberships-response) 200))
    (is (some #(= (get-in % [:entry :id]) saidone) (get-in list-site-memberships-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest create-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create site membership
        create-site-membership-response (->> (model/map->CreateSiteMembershipBody {:role sites/contributor :id saidone})
                                             (sites/create-site-membership ticket site-id))]
    (is (= (:status create-site-membership-response) 201))
    (is (some #(= (get-in % [:entry :id]) saidone) (get-in (sites/list-site-memberships ticket site-id) [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create site membership
        _ (->> (model/map->CreateSiteMembershipBody {:role sites/contributor :id saidone})
               (sites/create-site-membership ticket site-id))
        get-site-membership-response (sites/get-site-membership ticket site-id saidone)]
    (is (= (:status get-site-membership-response) 200))
    (is (= (get-in get-site-membership-response [:body :entry :person :id]) saidone))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest update-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create site membership
        _ (->> (model/map->CreateSiteMembershipBody {:role sites/collaborator :id saidone})
               (sites/create-site-membership ticket site-id))
        update-site-membership-response (->> (model/map->UpdateSiteMembershipBody {:role sites/consumer})
                                             (sites/update-site-membership ticket site-id saidone))]
    (is (= (:status update-site-membership-response) 200))
    (is (= (get-in update-site-membership-response [:body :entry :role]) sites/consumer))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest delete-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (tu/create-test-user ticket saidone)
        ;; create site membership
        _ (->> (model/map->CreateSiteMembershipBody {:role sites/collaborator :id saidone})
               (sites/create-site-membership ticket site-id))]
    (is (= (:status (sites/delete-site-membership ticket site-id saidone)) 204))
    (is (not-any? #(= (get-in % [:entry :id]) saidone) (get-in (sites/list-site-memberships ticket site-id) [:body :list :entries])))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest list-group-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create group
        group-id (.toString (UUID/randomUUID))
        _ (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
               (groups/create-group ticket))
        ;; create group site membership
        _ (->> (model/map->CreateGroupSiteMembershipBody {:role sites/contributor :id (str "GROUP_" group-id)})
               (sites/create-group-site-membership ticket site-id))
        list-group-site-membership-response (sites/list-group-site-membership ticket site-id)]
    (is (= (:status list-group-site-membership-response) 200))
    (is (some #(= (get-in % [:entry :id]) (str "GROUP_" group-id)) (get-in list-group-site-membership-response [:body :list :entries])))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest create-group-site-membership-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create group
        group-id (.toString (UUID/randomUUID))
        _ (->> (model/map->CreateGroupBody {:id group-id :display-name group-id})
               (groups/create-group ticket))
        ;; create group site membership
        create-group-site-membership-response (->> (model/map->CreateGroupSiteMembershipBody {:role sites/contributor :id (str "GROUP_" group-id)})
                                                   (sites/create-group-site-membership ticket site-id))]
    (is (= (:status create-group-site-membership-response) 201))
    (is (= (get-in create-group-site-membership-response [:body :entry :id] (str "GROUP_" group-id))))
    ;; clean up
    (is (= (:status (groups/delete-group ticket (str "GROUP_" group-id))) 204))
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))