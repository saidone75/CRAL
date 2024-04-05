(ns cral.test-utils
  (:require [clojure.test :refer :all]
            [cral.alfresco.core.nodes :as nodes]
            [cral.alfresco.model.core :as model]))

(defn get-guest-home
  [ticket]
  (get-in (nodes/get-node ticket "-root-" (model/map->GetNodeQueryParams {:relative-path "/Guest Home"})) [:body :entry :id]))