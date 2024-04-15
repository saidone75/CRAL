(ns cral.discovery-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.discovery :as discovery]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest get-discovery-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; get discovery
        get-discovery-response (discovery/get-discovery ticket)]
    (is (= (:status get-discovery-response) 200))))