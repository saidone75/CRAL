(ns cral.test-utils
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model :as model]
            [cral.alfresco.search :as search]))

(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (model/map->Ticket (get-in (auth/create-ticket user pass) [:body :entry]))
                    search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))
