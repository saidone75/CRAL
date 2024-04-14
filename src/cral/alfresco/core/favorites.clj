(ns cral.alfresco.core.favorites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateFavoriteQueryParams GetFavoriteQueryParams ListFavoritesQueryParams)))

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
    {}
    opts))