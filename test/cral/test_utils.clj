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

(ns cral.test-utils
  (:require [clojure.test :refer :all]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.people :as people]
            [cral.model.core :as model]))

(defn get-guest-home
  [ticket]
  (get-in (nodes/get-node ticket "-root-" (model/map->GetNodeQueryParams {:relative-path "/Guest Home"})) [:body :entry :id]))

(defn poll
  "Calls `f` until `done?` accepts its response, or throws with the last result.
  HTTP errors and responses accepted by `failed?` stop polling immediately.
  Timeout and interval are in milliseconds; each call uses its own HTTP timeout."
  ([f done?]
   (poll f done? {}))
  ([f done? {:keys [timeout-ms interval-ms failed?]
             :or   {timeout-ms 60000 interval-ms 1000 failed? (constantly false)}}]
   {:pre [(not (neg? timeout-ms)) (pos? interval-ms)]}
   (let [deadline (+ (System/nanoTime) (* timeout-ms 1000000))
         timeout (fn [result]
                   (throw (ex-info (format "Polling timed out after %s ms; last result: %s" timeout-ms (pr-str result))
                                   {:timeout-ms timeout-ms :last-result result})))]
     (loop [result (f)]
       (cond
         (or (and (integer? (:status result)) (not (<= 200 (:status result) 299)))
             (failed? result))
         (throw (ex-info (format "Polling failed; last result: %s" (pr-str result))
                         {:last-result result}))

         (done? result) result

         (>= (System/nanoTime) deadline) (timeout result)

         :else
         (do
           (Thread/sleep (long (min interval-ms (max 1 (quot (- deadline (System/nanoTime)) 1000000)))))
           (if (>= (System/nanoTime) deadline)
             (timeout result)
             (recur (f)))))))))

(defn rendition-created?
  "Checks whether the requested rendition has reached CREATED in an entry or list response."
  [response rendition-id]
  (some #(and (= (get-in % [:entry :id]) rendition-id)
              (= (get-in % [:entry :status]) "CREATED"))
        (if (get-in response [:body :entry])
          [(:body response)]
          (get-in response [:body :list :entries]))))

(defn create-test-user
  [^String ticket ^String user]
  (->> (model/map->CreatePersonBody {:id         user
                                     :first-name user
                                     :last-name  user
                                     :email      (format "%s@saidone.org" user)
                                     :password   user})
       (people/create-person ticket)))
