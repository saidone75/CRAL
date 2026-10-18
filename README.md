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
Default configuration is stored in the `alfresco` atom defined in `cral.config`:
```clojure
@config/alfresco
=>
{:scheme "http",
 :host "localhost",
 :port 8080,
 :conn-timeout 5000,
 :socket-timeout 60000,
 :core-path "alfresco/api/-default-/public/alfresco/versions/1",
 :search-path "alfresco/api/-default-/public/search/versions/1",
 :auth-path "alfresco/api/-default-/public/authentication/versions/1"}
```
and can be overwritten by passing a map with the new values to `config/configure`:
```clojure
(config/configure {:scheme "https" :port 8983})
```

Timeouts are in milliseconds. `:conn-timeout` limits connection establishment;
`:socket-timeout` limits waiting for data during a read, including streaming
downloads. It is not a limit on the total download duration. Defaults are 5000 ms
for connection establishment and 60000 ms for reads. Set either value to `0` to
disable that timeout. Configure them globally:

```clojure
(config/configure {:conn-timeout 5000 :socket-timeout 60000})
```

The same keys are available under `:alfresco` in `resources/config.edn` when
loading that file with `config/load-config`. Override either value for one call
through the existing options argument:

```clojure
(nodes/get-node-content ticket node-id nil
                        {:as :stream :socket-timeout 120000})
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
## Streaming downloads

Content downloads return byte arrays by default. Pass `{:as :stream}` as the
options argument to receive a `java.io.InputStream` without buffering the whole
file in memory. Close the stream after use, preferably with `with-open`:

```clojure
(require '[clojure.java.io :as io])

(let [{:keys [status body]} (nodes/get-node-content ticket node-id nil {:as :stream})]
  (if (= 200 status)
    (with-open [stream body]
      (io/copy stream (io/file "document.pdf")))
    (throw (ex-info "Download failed" {:status status :body body}))))
```

## Development
Requires a JDK (CI uses Java 21) and the [Clojure CLI](https://clojure.org/guides/install_clojure).
Dependencies are declared in `deps.edn`; library coordinates and version are in `build.clj`.

```sh
clojure -M:repl                         # REPL in cral.core, with test resources
clojure -M:test -n cral.utils-test       # unit tests without Alfresco
clojure -M:test                         # full suite, requires Alfresco
clojure -T:build jar                    # target/cral-0.4.1-SNAPSHOT.jar and POM
clojure -T:build install                # build and install into ~/.m2/repository
clojure -T:build clean                  # delete target/
```

The full test suite uses the Alfresco environment configured in `resources/config.edn`.
Start the Docker environment with `./alfresco.sh start` (requires Docker Compose),
and wait for Alfresco to be ready before running integration tests.
CI runs the unit tests and builds the library JAR. Test sources and fixtures are excluded from the JAR.
For editor REPLs, enable the `:dev` alias to include `test` and `test-resources`.

To preview documentation locally, run `./cljdoc-preview.sh` (requires Docker).
It installs the library locally, ingests it into cljdoc, and serves the preview at
<http://localhost:9000>. Each run prints its temporary data directory, which can be
removed after stopping the preview.

## Publishing

The library coordinates and version are defined in `build.clj`. For a release,
set the version without `-SNAPSHOT` (for example, `0.4.1`), run the tests, and
commit the release changes. Push the commit and a matching Git tag such as
`v0.4.1` before deploying, so cljdoc can access the release sources.

Create a [Clojars deploy token](https://clojars.org/tokens) with permission to
publish `org.saidone/cral`. Run the Bash script to enter your username and token
(the token is hidden while typing) and deploy:

```bash
./deploy.sh
```

`deploy` builds the JAR and POM before uploading them to Clojars. Artifact paths
are derived from the version in `build.clj`; credentials are read from the
environment. `clojure -T:build install` only installs locally and does not publish.

The generated POM includes the Git repository URL and current commit SHA for
cljdoc. Build from the committed release sources to keep the JAR and Git sources
in sync. cljdoc picks up releases from Clojars; check the
[CRAL documentation](https://cljdoc.org/d/org.saidone/cral) and request a build
through cljdoc if needed. No separate documentation upload is required.

## License
Copyright (c) 2023-2026 Saidone

Distributed under the GNU General Public License v3.0
