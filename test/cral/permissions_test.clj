(ns cral.permissions-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.core :as core]))

(let [permissions (core/map->Permissions {:is-inheritance-enabled false})]
  (->> permissions
       (#(update % :locally-set conj (core/map->LocallySet {:authority-id "admin" :name "Contributor" :access-status "ALLOWED"})))
       (#(update % :locally-set conj (core/map->LocallySet {:authority-id "guest" :name "Consumer" :access-status "ALLOWED"})))))