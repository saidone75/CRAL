(ns cral.test-utils-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.config :as config]
            [cral.alfresco.core.nodes :as nodes]
            [cral.test-utils :as test-utils]))

(defonce user "admin")
(defonce password "admin")

(deftest get-guest-home
  (config/set-log-level :trace)
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        guest-home-id (test-utils/get-guest-home ticket)]
    (is (= (get-in (nodes/get-node ticket guest-home-id) [:body :entry :properties :cm:title]) "Guest Home"))))