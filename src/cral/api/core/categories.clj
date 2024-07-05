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

(ns cral.api.core.categories
  (:require [clj-http.lite.client :as client]
            [clojure.data.json :as json]
            [cral.config :as config]
            [cral.model.core]
            [cral.utils.utils :as utils])
  (:import (clojure.lang PersistentHashMap PersistentVector)
           (cral.model.auth Ticket)
           (cral.model.core CreateCategoryQueryParams
                            ListCategoriesQueryParams
                            ListNodeCategoriesQueryParams)))

(defn list-node-categories
  "Gets a list of categories for node `node-id`.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API)."
  ([^Ticket ticket ^String node-id]
   (list-node-categories ticket node-id nil))
  ([^Ticket ticket ^String node-id ^ListNodeCategoriesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/nodes/%s/category-links" (config/get-url 'core) node-id)
     ticket
     {:query-params query-params}
     opts)))

(defn list-categories
  "Gets a list of subcategories within the category `category-id`.
  The parameter `category-id` can be set to the alias -root- to obtain a list of top level categories.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API)."
  ([^Ticket ticket ^String category-id]
   (list-categories ticket category-id nil))
  ([^Ticket ticket ^String category-id ^ListCategoriesQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/get
     (format "%s/categories/%s/subcategories" (config/get-url 'core) category-id)
     ticket
     {:query-params query-params}
     opts)))

(defn create-category
  "Creates a new category within the category `category-id`.
  The parameter `category-id` can be set to the alias -root- to create a new top level category.
  You must have admin rights to create a category.\\
  More info [here](https://api-explorer.alfresco.com/api-explorer/?urls.primaryName=Core%20API)."
  ([^Ticket ticket ^String category-id ^PersistentVector body]
   (create-category ticket category-id body nil))
  ([^Ticket ticket ^String category-id ^PersistentVector body ^CreateCategoryQueryParams query-params & [^PersistentHashMap opts]]
   (utils/call-rest
     client/post
     (format "%s/categories/%s/subcategories" (config/get-url 'core) category-id)
     ticket
     {:body         (json/write-str (utils/camel-case-stringify-keys body))
      :query-params query-params
      :content-type :json}
     opts)))