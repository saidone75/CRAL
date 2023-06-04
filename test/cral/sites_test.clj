(ns cral.sites-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.sites :as sites]
            [cral.alfresco.model :as model])
  (:import (java.util UUID)))

(def user "admin")
(def pass "admin")

(deftest list-sites
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        list-sites-response (sites/list-sites ticket)]
    (is (= 200 (:status list-sites-response)))
    list-sites-response))

(deftest create-site
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        create-site-body (model/map->CreateSiteBody {:title (.toString (UUID/randomUUID)) :visibility "PUBLIC"})
        create-site-response (sites/create-site ticket create-site-body)]
    (is (= 201 (:status create-site-response)))
    create-site-response))
