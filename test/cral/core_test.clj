(ns cral.core-test
  (:require [clojure.data.json :as json]
            [clojure.test :refer :all]
            [cral.core :refer :all]
            [cral.alfresco.core :as core]
            [cral.alfresco.search :as search]
            [cral.alfresco.auth :as auth]
            [cral.utils.utils :as utils]))

(deftest get-ticket
  (let [ticket (auth/get-ticket "admin" "admin")]
    (is (not (nil? (:id ticket))))))

(deftest get-node
  (let [ticket (auth/get-ticket "admin" "admin")]
    (core/get-node ticket "ff7eab38-1bea-4285-bd7d-7dcfdee17edc" {:include ["path"]})))

(deftest update-node
  (let [ticket (auth/get-ticket "admin" "admin")]
    (core/update-node ticket "ff7eab38-1bea-4285-bd7d-7dcfdee17edc" (json/write-str {:properties {"cm:title" "ciao"}}))))

(defn get-guest-home
  []
  (:entry (first
            (get-in
              (let [ticket (auth/get-ticket "admin" "admin")
                    query (search/make-query "PATH:'app:company_home/app:guest_home'")
                    query-body (search/make-query-body query)]
                (search/search ticket query-body))
              [:list :entries]))))