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
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.sites :as sites]
            [cral.alfresco.model.core :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

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