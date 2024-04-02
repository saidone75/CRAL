(ns cral.trashcan-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.core.trashcan :as trashcan]
            [cral.core :refer :all]
            [taoensso.timbre :as timbre]))

(def user "admin")
(def password "admin")

(timbre/set-config! {:min-level :info})

(deftest list-deleted-nodes
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])]
    (is (= (:status (trashcan/list-deleted-nodes ticket)) 200))))
