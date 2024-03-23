(ns cral.alfresco.core.favorites
  (:require [clj-http.lite.client :as client]
            [cral.alfresco.config :as config]
            [cral.alfresco.model.auth]
            [cral.alfresco.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap)
           (cral.alfresco.model.auth Ticket)
           (cral.alfresco.model.core ListFavoritesQueryParams)))

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
