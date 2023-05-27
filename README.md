# CRAL
♫ faccio il tifo per la S.P.A.L. / a tressette gioco al C.R.A.L. ♫

CRAL is a pure Clojure client library for consuming Alfresco Content Services public REST API in an idiomatic way.
## Usage
Require the relevant namespaces:
```clojure
(:require [cral.alfresco.model :as model]
          [cral.alfresco.core :as core]
          [cral.alfresco.search :as search]
          [cral.alfresco.auth :as auth])
```
Results are returned as maps that can be pretty easily handled in Clojure, please note that all map keys are converted to proper kebab-case keywords and query parameters as well as POST bodies JSON keys are automatically converted to camel case before they are sent: 
```clojure
(auth/create-ticket "admin" "admin")
=> {:status 201, :body {:entry {:id "TICKET_5905a2c41cf63c003a2044ebdae69aa48691fdc8", :user-id "admin"}}}

(let [ticket (model/map->Ticket (get-in (auth/create-ticket user pass) [:body :entry]))
      search-request (search/map->SearchRequest {:query (search/map->RequestQuery {:query "PATH:'app:company_home/app:guest_home'"})})]
  (search/search ticket search-request))
=>
{:status 200,
 :body {:list {:pagination {:count 1, :has-more-items false, :total-items 1, :skip-count 0, :max-items 100},
               :context {:consistency {:last-tx-id 277}},
               :entries [{:entry {:is-file false,
                                  :is-folder true,
                                  :created-by-user {:id "System", :display-name "System"},
                                  :name "Guest Home",
                                  :modified-at "2023-05-27T12:33:33.089+0000",
                                  :node-type "cm:folder",
                                  :search {:score 0.0},
                                  :id "443fd8b1-c135-48a2-8c1d-1567777aaa19",
                                  :parent-id "6b4fa714-3a58-411a-8839-179f8e01f728",
                                  :modified-by-user {:id "admin", :display-name "Administrator"},
                                  :created-at "2023-05-21T16:46:55.651+0000",
                                  :location "nodes"}}]}}}
```
