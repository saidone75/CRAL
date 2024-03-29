(ns cral.test-utils-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.config :as config]
            [cral.test-utils :as test-utils]))

(defonce user "admin")
(defonce password "admin")

(deftest get-guest-home
  (config/set-log-level :trace)
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    (is (= (:name (test-utils/get-guest-home ticket)) "Guest Home"))))


