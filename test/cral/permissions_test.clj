(ns cral.permissions-test
  (:require [clojure.test :refer :all]
            [cral.utils.utils :as utils]
            [cral.alfresco.core :as core]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]))

(let [permissions (core/make-permissions true)]
  (utils/camel-case-stringify-keys
    (-> permissions
        (core/add-locally-set (core/make-locally-set "admin" "Contributor" true))
        (core/add-locally-set (core/make-locally-set "guest" "Consumer" true)))))
