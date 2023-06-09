(ns cral.search-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model.search :as search-model]
            [cral.alfresco.search :as search]
            [cral.core :refer :all]))

(deftest search
  (let [ticket (get-in (auth/create-ticket "admin" "admin") [:body :entry])
        response
        (->> (search-model/map->RequestQuery {:query "PATH:'app:company_home'"})
             (#(search-model/map->SearchBody {:query %}))
             (#(search/search ticket %)))]
    (is (= 200 (:status response)))
    (is (= "Company Home") (:name (first (get-in response [:body :list :entries]))))))