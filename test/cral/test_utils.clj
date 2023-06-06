(ns cral.test-utils
  (:require [clojure.test :refer :all]
            [cral.alfresco.search :as search]))

(defn get-guest-home
  [ticket]
  (:entry (first
            (get-in
              (let [search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
                (search/search ticket search-request))
              [:body :list :entries]))))
