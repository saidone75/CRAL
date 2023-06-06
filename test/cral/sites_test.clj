(ns cral.sites-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.sites :as sites]
            [cral.alfresco.model :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest create-list-and-delete-site
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
    ;; delete site
    (is (= 204 (:status (sites/delete-site ticket site-id (model/map->DeleteSiteQueryParams {:permanent true})))))))