(ns cral.permissions-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.model.core :as model]))

(let [permissions (model/map->Permissions {:is-inheritance-enabled false})]
  (->> permissions
       (#(update % :locally-set conj (model/map->LocallySet {:authority-id "admin" :name "Contributor" :access-status "ALLOWED"})))
       (#(update % :locally-set conj (model/map->LocallySet {:authority-id "guest" :name "Consumer" :access-status "ALLOWED"})))))