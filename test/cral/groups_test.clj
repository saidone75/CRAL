(ns cral.groups-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.groups :as groups]
            [cral.alfresco.model.core]))

(def user "admin")
(def pass "admin")

(deftest list-group-memberships
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])]
    (groups/list-group-memberships ticket "admin")))

(deftest list-groups
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        list-groups-response (groups/list-groups ticket)]
    (is (= 200 (:status list-groups-response)))
    (is (->> list-groups-response
             (#(get-in % [:body :list :entries]))
             (map #(get-in % [:entry :id]))
             (some #(= "GROUP_ALFRESCO_ADMINISTRATORS" %))))))

(deftest get-group-details
  (let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
        get-group-details-response (groups/get-group-details ticket "GROUP_ALFRESCO_ADMINISTRATORS")]
    (is (= 200 (:status get-group-details-response)))
    (is (get-in get-group-details-response [:body :entry :is-root]))))