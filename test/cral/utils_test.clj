;
;  CRAL
;  Copyright (C) 2023-2026 Saidone
;  SPDX-License-Identifier: GPL-3.0-or-later
;
;  This program is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  This program is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with this program.  If not, see <http://www.gnu.org/licenses/>.
;

(ns cral.utils-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [clj-http.lite.client :as client]
            [cral.config :as config]
            [cral.utils.utils :as utils]))

(deftest kebab-case
  (is (= (utils/kebab-case "kebabCase")
         "kebab-case")))

(deftest kebab-keywordize-keys
  (is (= (utils/kebab-keywordize-keys {"levelOne" {"levelTwo" "value"}})
         {:level-one {:level-two "value"}})))

(deftest camel-case-keywordize-keys
  (is (= (utils/camel-case-keywordize-keys {"level-one" {"level-two" "value"}})
         {:levelOne {:levelTwo "value"}})))

(deftest camel-case-stringify-keys
  (is (= (utils/camel-case-stringify-keys {"level-one" {"level-two" "value"}})
         {"levelOne" {"levelTwo" "value"}})))

(deftest configured-timeouts-apply-to-http-requests
  (with-redefs [config/alfresco (atom @config/alfresco)]
    (config/configure {:conn-timeout 5000 :socket-timeout 60000})
    (with-redefs [client/post (fn [_ req]
                               (is (= 5000 (:conn-timeout req)))
                               (is (= 60000 (:socket-timeout req)))
                               (is (= :json (:content-type req)))
                               {:status 201 :body "{}"})]
      (is (= 201 (:status (auth/create-ticket "user" "password")))))))

(deftest timeout-overrides-preserve-other-defaults
  (with-redefs [config/alfresco (atom {:conn-timeout 5000 :socket-timeout 60000})]
    (doseq [socket-timeout [120000 0]]
      (utils/call-rest
        (fn [_ req]
          (is (= 5000 (:conn-timeout req)))
          (is (= socket-timeout (:socket-timeout req)))
          (is (= {"maxItems" 10} (:query-params req)))
          (is (nil? (:return-headers req)))
          {:status 204 :body nil})
        "http://example.invalid" nil {:query-params {:max-items 10}}
        {:socket-timeout socket-timeout :return-headers true}))))

(deftest timeout-config-loading-and-connection-override
  (with-redefs [config/alfresco (atom {})]
    (config/load-config {:alfresco {:conn-timeout 5000 :socket-timeout 60000}})
    (utils/call-rest
      (fn [_ req]
        (is (= 1000 (:conn-timeout req)))
        (is (= 60000 (:socket-timeout req)))
        {:status 204 :body nil})
      "http://example.invalid" nil nil {:conn-timeout 1000})))
