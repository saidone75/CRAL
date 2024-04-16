(ns cral.model-test
  (:require [clojure.test :refer :all]
            [cral.alfresco.auth :as auth]
            [cral.alfresco.model :as model]
            [cral.alfresco.model.alfresco.cm :as cm]
            [cral.alfresco.model.core]))

(def user "admin")
(def password "admin")

(deftest list-aspects-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; list aspects
        list-aspects-response (model/list-aspects ticket)]
    (is (= (:status list-aspects-response) 200))))

(deftest get-aspect-test
  (let [ticket (get-in (auth/create-ticket user password) [:body :entry])
        ;; get aspect
        get-aspect-response (model/get-aspect ticket (name cm/asp-titled))]
    (is (= (get-in get-aspect-response [:body :entry :id]) (name cm/asp-titled)))
    (is (= (:status get-aspect-response) 200))))