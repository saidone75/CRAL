# CRAL
*♫ faccio il tifo per la S.P.A.L. / a tressette gioco al C.R.A.L. ♫*

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
Post bodies and request parameters with well known keys are modeled as records:
```clojure
(model/map->GetNodeQueryParams {:include ["path" "permissions"]})
=> #cral.alfresco.model.GetNodeQueryParams{:include ["path" "permissions"], :relative-path nil, :fields nil}

(model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
=>
#cral.alfresco.model.CreateNodeBody{:name "50075900-f0ef-461c-8534-116945f29b58",
                                    :node-type "cm:content",
                                    :properties nil}
```
but plain maps can be used as well if desired:
```clojure
(let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
      parent-id (:id (get-guest-home))]
  (select-keys
    (get-in (core/create-node ticket parent-id {:name "test" :node-type "cm:content"}) [:body :entry])
    [:name :node-type :id :parent-id]))
=>
{:name "test",
 :node-type "cm:content",
 :id "68d9d0e3-8830-427e-b04e-c0109b02a539",
 :parent-id "a29adc70-0639-416a-9660-4ed7f247b5a9"}
```
## License
Copyright (c) 2023 Saidone

Distributed under the GNU General Public License v3.0