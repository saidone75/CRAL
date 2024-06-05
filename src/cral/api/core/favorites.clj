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

(ns cral.api.core.favorites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)
           (cral.model.core CreateFavoriteQueryParams GetFavoriteQueryParams ListFavoritesQueryParams)))

(defn list-favorites
  "Gets a list of favorites for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/favorites/listFavorites)."
  ([^Ticket ticket ^String person-id]
   (list-favorites ticket person-id nil))
  ([^Ticket ticket ^String person-id ^ListFavoritesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/favorites" (config/get-url 'core) person-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-favorite
  "Favorite a site, file, or folder in the repository.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/favorites/createFavorite)."
  ([^Ticket ticket ^String person-id ^PersistentVector body]
   (create-favorite ticket person-id body nil))
  ([^Ticket ticket ^String person-id ^PersistentVector body ^CreateFavoriteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/people/%s/favorites" (config/get-url 'core) person-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))

(defn get-favorite
  "Gets favorite `favorite-id` for person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/favorites/getFavorite)."
  ([^Ticket ticket ^String person-id ^String favorite-id]
   (get-favorite ticket person-id favorite-id nil))
  ([^Ticket ticket ^String person-id ^String favorite-id ^GetFavoriteQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/people/%s/favorites/%s" (config/get-url 'core) person-id favorite-id)
     ticket
     {:query-params query-params}
     opts)))

(defn delete-favorite
  "Deletes `favorite-id` as a favorite of person `person-id`.
  You can use the **-me-** string in place of `person-id` to specify the currently authenticated user.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API#/favorites/deleteFavorite)."
  [^Ticket ticket ^String person-id ^String favorite-id & [^PersistentHashMap opts]]
  (utils/call-rest
    client/delete
    (format "%s/people/%s/favorites/%s" (config/get-url 'core) person-id favorite-id)
    ticket
    nil
    opts))