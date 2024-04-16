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
    (is (= (:status response) 200))
    (is (= (get-in (first (get-in response [:body :list :entries])) [:entry :name]) "Company Home"))))