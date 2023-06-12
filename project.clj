(defproject org.saidone/cral "0.1.1"
  :description "A library for consuming Alfresco Content Services public REST API"
  :url "https://saidone.org"
  :license {:name "GNU General Public License v3.0"
            :url  "https://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clj-commons/clj-http-lite "1.0.13"]
                 [com.taoensso/timbre "6.1.0"]]
  :repl-options {:init-ns cral.alfresco})