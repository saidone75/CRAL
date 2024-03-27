(ns cral.alfresco.core.favorites
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.alfresco.config :as config]
            [cral.utils.utils :as utils]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core CreateFavoriteQueryParams ListFavoritesQueryParams)))

(defn list-favorites
  "Gets a list of favorites for person **person-id**.
  You can use the -me- string in place of **personId** to specify the currently authenticated user.
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
  You can use the -me- string in place of **personId** to specify the currently authenticated user.
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