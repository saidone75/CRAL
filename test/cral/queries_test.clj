;  CRAL
;  Copyright (C) 2023-2024 Saidone
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

(ns cral.queries-test
  (:require [clojure.test :refer :all]
            [cral.api.auth :as auth]
            [cral.api.core.queries :as queries]
            [cral.config :as c]
            [cral.fixtures :as fixtures]
            [cral.model.core :as model]))

(use-fixtures :once fixtures/setup)

(deftest find-nodes-test
  (let [ticket (get-in (auth/create-ticket c/user c/password) [:body :entry])]
    (println (->> (model/map->FindNodesQueryParams {:term "readme"})
                  (queries/find-nodes ticket)))))