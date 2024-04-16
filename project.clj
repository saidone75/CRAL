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

(defproject org.saidone/cral "0.2.3-SNAPSHOT"
  :description "A library for consuming Alfresco Content Services public REST API"
  :url "https://saidone.org"
  :license {:name "GNU General Public License v3.0"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [org.clojure/data.json "2.5.0"]
                 [org.clj-commons/clj-http-lite "1.0.13"]
                 [com.taoensso/timbre "6.5.0"]]
  :repl-options {:init-ns cral.alfresco})