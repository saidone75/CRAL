;
;  CRAL
;  Copyright (C) 2023-2026 Saidone
;  SPDX-License-Identifier: GPL-3.0-or-later
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
;

(ns cral.fixtures
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.config :as c]
            [clojure.edn :as edn]
            [taoensso.telemere :as t]
            [cral.api.auth :as auth]
            [cral.api.core.nodes :as nodes]
            [cral.api.core.trashcan :as trashcan]
            [cral.api.core.groups :as groups]
            [cral.api.core.sites :as sites]
            [cral.api.core.tags :as tags]
            [cral.api.core.categories :as categories]
            [cral.api.core.shared-links :as shared-links]
            [cral.api.core.downloads :as downloads]
            [cral.api.core.people :as people]))

(def config-file "resources/config.edn")

(defn setup [f]
  (let [config @c/alfresco
        user c/user
        password c/password]
    (try
      (if (.exists (io/file config-file))
        (c/load-config (edn/read-string (slurp config-file)))
        (t/log! :warn (format "unable to find %s, using defaults" config-file)))
      (f)
      (finally
        (reset! c/alfresco config)
        (alter-var-root #'c/user (constantly user))
        (alter-var-root #'c/password (constantly password))))))

(def ^:dynamic *cleanup* nil)

(defn on-cleanup
  "Registers cleanup immediately after acquiring a test resource."
  [f]
  (assert *cleanup* "Resource creation requires the resources fixture")
  (swap! *cleanup* conj f))

(defn temp-file
  [prefix suffix]
  (let [file (java.io.File/createTempFile prefix suffix)]
    (on-cleanup #(do (io/delete-file file true) {:status 204}))
    file))

(defn- delete-node
  [ticket id]
  (let [response (nodes/delete-node ticket id {:permanent true})]
    (if (= 404 (:status response))
      (trashcan/delete-deleted-node ticket id)
      response)))

(defn resources
  "Tracks resources acquired by one test and releases them in reverse order.
  Explicit deletion assertions in tests remain unchanged."
  [f]
  (let [cleanup (atom [])
        tickets (atom [])
        creators {#'nodes/create-node delete-node
                  #'nodes/copy-node delete-node
                  #'groups/create-group groups/delete-group
                  #'sites/create-site #(sites/delete-site %1 %2 {:permanent true})
                  #'tags/create-tag tags/delete-tag
                  #'categories/create-category categories/delete-category
                  #'shared-links/create-shared-link shared-links/delete-shared-link
                  #'downloads/create-download delete-node
                  #'people/create-person #(people/update-person %1 %2 {:enabled false})}
        login auth/create-ticket
        replacements (into {}
                           (for [[v destroy] creators
                                 :let [create @v]]
                             [v (fn [& args]
                                  (let [response (apply create args)
                                        id (get-in response [:body :entry :id])]
                                    (when (and id (#{201 202} (:status response)))
                                      (on-cleanup #(destroy (first args) id)))
                                    response))]))
        release (fn [action]
                  (try
                    (let [response (action)]
                      (when-not (#{200 202 204 404} (:status response))
                        (do-report {:type :fail :message "Resource cleanup failed"
                                    :expected :successful-cleanup :actual response})))
                    (catch Exception e
                      (do-report {:type :error :message "Resource cleanup failed"
                                  :expected :successful-cleanup :actual e}))))]
    (binding [*cleanup* cleanup]
      (try
        (with-redefs-fn
          (assoc replacements #'auth/create-ticket
                 (fn [& args]
                   (let [response (apply login args)
                         ticket (get-in response [:body :entry])]
                     (when (= 201 (:status response))
                       (swap! tickets conj ticket))
                     response)))
          f)
        (finally
          (doseq [action (reverse @cleanup)] (release action))
          (doseq [ticket (distinct @tickets)]
            (release #(let [response (auth/delete-ticket ticket)]
                        ;; A test may already have invalidated its ticket.
                        (if (= 401 (:status response)) {:status 204} response)))))))))
