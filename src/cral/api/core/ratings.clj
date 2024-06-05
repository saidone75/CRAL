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

(ns cral.api.core.ratings
  (:require [clj-http.lite.client :as client]
            [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.model.auth Ticket)
           (cral.model.core CreateRatingBody
                            CreateRatingQueryParams
                            GetRatingQueryParams
                            ListRatingsQueryParams)))

(defn list-ratings
  "Gets a list of ratings for node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/ratings/listRatings)."
  ([^Ticket ticket ^String node-id]
   (list-ratings ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListRatingsQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/ratings" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-rating
  "Create a rating for the node with identifier `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/ratings/createRating)."
  ([^Ticket ticket ^String node-id ^CreateRatingBody body]
   (create-rating ticket node-id body nil))
  ([^Ticket ticket ^String node-id ^CreateRatingBody body ^CreateRatingQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/nodes/%s/ratings" (config/get-url 'core) node-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-rating
  "Get the specific rating `rating-id` on node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/ratings/getRating)."
  ([^Ticket ticket ^String node-id ^String rating-id]
   (get-rating ticket node-id rating-id nil))
  ([^Ticket ticket ^String node-id ^String rating-id ^GetRatingQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/ratings/%s" (config/get-url 'core) node-id rating-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-rating
  "Deletes rating `rating-id` from node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/ratings/deleteRating)."
  [^Ticket ticket ^String node-id ^String rating-id]
  (utils/call-rest
    client/delete
    (format "%s/nodes/%s/ratings/%s" (config/get-url 'core) node-id rating-id)
    ticket
    nil
    nil))