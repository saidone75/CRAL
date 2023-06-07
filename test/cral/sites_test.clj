(ns cral.sites-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.sites :as sites]
            [cral.alfresco.model :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-then-list-then-update-then-get-then-delete-site
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        site-id (.toString (UUID/randomUUID))
        create-site-body (model/map->CreateSiteBody {:title site-id :id site-id :visibility "PUBLIC"})
        ;; create site
        create-site-response (sites/create-site ticket create-site-body)]
    (is (= 201 (:status create-site-response)))
    ;; list-sites
    (let [list-sites-response (sites/list-sites ticket)]
      (is (= 200 (:status list-sites-response)))
      ;; check if site is present
      (is (some true?
                (map
                  #(= site-id (get-in % [:entry :id]))
                  (get-in list-sites-response [:body :list :entries])))))
    ;; update site
    (let [update-site-body (model/map->UpdateSiteBody {:title (.toString (UUID/randomUUID)) :visibility "PRIVATE"})
          update-site-response (sites/update-site ticket site-id update-site-body)]
      (is (= 200 (:status update-site-response)))
      ;; check if title and visibility have been updated
      (is (= (:title update-site-body) (get-in update-site-response [:body :entry :title])))
      (is (= (:visibility update-site-body) (get-in update-site-response [:body :entry :visibility]))))
    ;; get-site
    (is (= 200 (:status (sites/get-site ticket site-id))))
    ;; delete site
    (is (= 204 (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true})))))))
