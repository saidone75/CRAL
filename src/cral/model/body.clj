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

(ns cral.model.body)

(defn supplied-fields
  "Returns request fields, omitting unprovided record fields that are still nil."
  [m]
  (let [omitted (when (record? m) (::omitted-fields (meta m)))]
    (remove (fn [[k v]] (and (contains? omitted k) (nil? v))) m)))

(defmacro defbody
  "Defines a body record whose map constructor remembers omitted fields.
  Positional constructors treat all declared fields as explicitly supplied."
  [record-name fields]
  (let [constructor (symbol (str "map->" record-name))
        field-keys (mapv (comp keyword name) fields)]
    `(do
       (defrecord ~record-name ~fields)
       (let [create# ~constructor]
         (defn ~constructor
           ~(str "Creates " record-name ", preserving omitted fields separately from explicit nil values.")
           [m#]
           (let [provided# (into {} (supplied-fields m#))]
             (with-meta (create# provided#)
               (assoc (meta m#) ::omitted-fields
                      (into #{} (remove #(contains? provided# %) ~field-keys))))))))))
