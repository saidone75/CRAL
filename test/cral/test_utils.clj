;  CRAL
;  Copyright (C) 2023-2024 user
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

(ns cral.test-utils
  (:require [clojure.test :refer :all]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.people :as people]
            [cral.model.core :as model]))

(defn get-guest-home
  [ticket]
  (get-in (nodes/get-node ticket "-root-" (model/map->GetNodeQueryParams {:relative-path "/Guest Home"})) [:body :entry :id]))

(defn create-test-user
  [^String ticket ^String user]
  (->> (model/map->CreatePersonBody {:id         user
                                     :first-name user
                                     :last-name  user
                                     :email      (format "%s@saidone.org" user)
                                     :password   user})
       (people/create-person ticket)))