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
            [cral.api.core.people :as people]
            [cral.api.core.sites :as sites]
            [cral.model.core :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")
(def saidone "saidone")

(deftest list-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (->> (model/map->CreatePersonBody {:id         saidone
                                             :first-name saidone
                                             :email      "saidone@saidone.org"
                                             :password   saidone})
               (people/create-person ticket))
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create site membership request
        _ (->> [(model/map->CreateSiteMembershipRequestBody {:message "Please can you add me"
                                                             :id      site-id
                                                             :title   (format "Request for %s site" site-id)})]
               (sites/create-site-membership-requests saidone-ticket "-me-"))]
    ;; list site membership request
    (is (= (:status (sites/list-site-membership-requests saidone-ticket "-me-")) 200))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest create-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (->> (model/map->CreatePersonBody {:id         saidone
                                             :first-name saidone
                                             :email      "saidone@saidone.org"
                                             :password   saidone})
               (people/create-person ticket))
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])]
    (is (= (:status (->> [(model/map->CreateSiteMembershipRequestBody {:message "Please can you add me"
                                                                       :id      site-id
                                                                       :title   (format "Request for %s site" site-id)})]
                         (sites/create-site-membership-requests saidone-ticket "-me-")) 201)))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

(deftest get-site-membership-requests-test
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        ;; create a moderated site
        _ (->> (model/map->CreateSiteBody {:title site-id :id site-id :visibility "MODERATED"})
               (sites/create-site ticket))
        ;; create user if not exist
        _ (->> (model/map->CreatePersonBody {:id         saidone
                                             :first-name saidone
                                             :email      "saidone@saidone.org"
                                             :password   saidone})
               (people/create-person ticket))
        ;; create a personal ticket
        saidone-ticket (get-in (auth/create-ticket saidone saidone) [:body :entry])
        ;; create membership request
        _ (->> [(model/map->CreateSiteMembershipRequestBody {:message "Please can you add me"
                                                             :id      site-id
                                                             :title   (format "Request for %s site" site-id)})]
               (sites/create-site-membership-requests saidone-ticket "-me-"))
        ;; get site membership request
        get-site-membership-requests-response (sites/get-site-membership-request ticket saidone site-id)]
    (is (= (:status get-site-membership-requests-response) 200))
    (is (= (get-in get-site-membership-requests-response [:body :entry :site :id]) site-id))
    ;; clean up
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))

;; old tests
(deftest create-then-list-then-update-then-get-then-delete-site
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        create-site-body (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
        ;; create site
        create-site-response (sites/create-site ticket create-site-body)]
    (is (= (:status create-site-response) 201))
    ;; list-sites
    (let [list-sites-response (sites/list-sites ticket)]
      (is (= (:status list-sites-response) 200))
      ;; check if site is present
      (is (some true?
                (map
                  #(= site-id (get-in % [:entry :id]))
                  (get-in list-sites-response [:body :list :entries])))))
    ;; update site
    (let [update-site-body (model/map->UpdateSiteBody {:title (.toString (UUID/randomUUID)) :visibility "PRIVATE"})
          update-site-response (sites/update-site ticket site-id update-site-body)]
      (is (= (:status update-site-response) 200))
      ;; check if title and visibility have been updated
      (is (= (get-in update-site-response [:body :entry :title]) (:title update-site-body)))
      (is (= (get-in update-site-response [:body :entry :visibility]) (:visibility update-site-body))))
    ;; get-site
    (is (= (:status (sites/get-site ticket site-id)) 200))
    ;; delete site
    (is (= (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true}))) 204))))