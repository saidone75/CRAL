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

(ns cral.fixtures
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [cral.config :as c]
            [immuconf.config :as immu]
            [taoensso.telemere :as t]))

(def config-file "resources/config.edn")

(defn setup [f]
  (if (.exists (io/file config-file))
    ;; load configuration
    (let [config (immu/load config-file)]
      (c/configure (:alfresco config))
      (t/set-kind-filter! (get-in config [:telemere :kind-filter]))
      (t/set-ns-filter! (get-in config [:telemere :ns-filter])))
    (t/log! :warn (format "unable to find %s, using defaults" config-file)))
  (f))