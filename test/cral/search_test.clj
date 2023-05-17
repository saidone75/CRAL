(ns cral.search-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.search :as search]
            [cral.core :refer :all]))

(deftest search
  (let [ticket (get-in (auth/get-ticket "admin" "admin") [:body :entry])
        response
        (->> (search/map->RequestQuery {:query "PATH:'app:company_home'"})
             (#(search/map->SearchRequest {:query %}))
             (#(search/search ticket %)))]
    (is (= 200 (:status response)))
    (is (= "Company Home") (:name (first (get-in response [:body :list :entries]))))))