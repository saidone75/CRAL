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

(ns cral.pagination)

(defn entries
  "Lazily reads Alfresco list entries using (fetch-page query-params).
  Preserves query parameters, advancing :skip-count by the returned entry count.
  HTTP failures and invalid pagination throw ex-info with :response and :skip-count
  when the failing page is realized. Entries retain their {:entry ...} wrapper."
  ([fetch-page]
   (entries fetch-page {}))
  ([fetch-page query-params]
   (let [skip-count (or (:skip-count query-params) 0)]
     (when-not (and (integer? skip-count) (not (neg? skip-count)))
       (throw (ex-info "Invalid pagination offset" {:skip-count skip-count})))
     (letfn [(page [offset]
               (lazy-seq
                 (let [response (fetch-page (assoc query-params :skip-count offset))
                       status (:status response)
                       entries (get-in response [:body :list :entries])
                       pagination (get-in response [:body :list :pagination])
                       more? (:has-more-items pagination)
                       fail (fn [message]
                              (throw (ex-info message {:response response :skip-count offset})))]
                   (when-not (and (integer? status) (<= 200 status 299))
                     (fail "Failed to fetch page"))
                   (when-not (and (sequential? entries) (boolean? more?)
                                  (= (:skip-count pagination) offset)
                                  (or (not more?) (seq entries)))
                     (fail "Invalid pagination response"))
                   (concat entries
                           (when more?
                             (page (+ offset (count entries))))))))]
       (page skip-count)))))
