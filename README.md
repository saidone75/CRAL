# CRAL
*♫ faccio il tifo per la S.P.A.L. / a tressette gioco al C.R.A.L. ♫*

[![Clojars Version](https://img.shields.io/clojars/v/org.saidone/cral)](https://clojars.org/org.saidone/cral)
[![cljdoc badge](https://cljdoc.org/badge/org.saidone/cral)](https://cljdoc.org/d/org.saidone/cral)

CRAL is a Clojure library for consuming Alfresco Content Services public REST API in an idiomatic way.
## Outline
- Lean and readable code
- pure Clojure
- 100% test coverage
- Tuned for GraalVM, can be compiled to native binary (e.g. [graalf](https://github.com/saidone75/graalf))
## Usage
Require the relevant namespaces, e.g.:
```clojure
(:require
  [cral.api.auth :as auth]
  [cral.api.search :as search]
  [cral.api.core.nodes :as nodes]
  [cral.model.core :as model])
```
Default configuration is stored in `config` atom defined in `cral.config`:
```clojure
@config/config
=>
{:scheme "http",
 :host "localhost",
 :port 8080,
 :core-path "alfresco/api/-default-/public/alfresco/versions/1",
 :search-path "alfresco/api/-default-/public/search/versions/1",
 :auth-path "alfresco/api/-default-/public/authentication/versions/1"}
```
and can be overwritten by passing a map with the new values to `config/configure` fn:
```clojure
(config/configure {:scheme "https" :port 8983})
=>
{:scheme "https",
 :host "localhost",
 :port 8983,
 :core-path "alfresco/api/-default-/public/alfresco/versions/1",
 :search-path "alfresco/api/-default-/public/search/versions/1",
 :auth-path "alfresco/api/-default-/public/authentication/versions/1"}
```
Results are returned as maps that can be easily handled in Clojure, please note that all map keys are converted to proper kebab-case keywords and query parameters as well as POST bodies JSON keys are automatically converted to camel case before they are sent: 
```clojure
(auth/create-ticket "admin" "admin")
=>
{:status 201, :body {:entry {:id "TICKET_5905a2c41cf63c003a2044ebdae69aa48691fdc8", :user-id "admin"}}}

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
POST/PUT bodies and request parameters with well known keys are modeled as records:
```clojure
(model/map->GetNodeQueryParams {:include ["path" "permissions"]})
=>
#cral.model.core.GetNodeQueryParams{:include ["path" "permissions"], :relative-path nil, :fields nil}

(model/map->CreateNodeBody {:name (.toString (UUID/randomUUID)) :node-type "cm:content"})
=>
#cral.model.core.CreateNodeBody{:name "cfa7c592-e357-4efc-9d91-f5dd98ab910c", :node-type "cm:content", :properties nil}
```
but plain maps can be used as well if desired:
```clojure
(let [ticket (get-in (auth/create-ticket user pass) [:body :entry])
      parent-id (:id (get-guest-home))]
  (select-keys
    (get-in (nodes/create-node ticket parent-id {:name "test" :node-type "cm:content"}) [:body :entry])
    [:name :node-type :id :parent-id]))
=>
{:name "test",
 :node-type "cm:content",
 :id "68d9d0e3-8830-427e-b04e-c0109b02a539",
 :parent-id "a29adc70-0639-416a-9660-4ed7f247b5a9"}
```
## License
Copyright (c) 2023-2024 Saidone

Distributed under the GNU General Public License v3.0
