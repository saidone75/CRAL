(ns cral.sites-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.sites :as sites]))

(def user "admin")
(def pass "admin")

(deftest list-sites
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])]
    (sites/list-sites ticket)))
