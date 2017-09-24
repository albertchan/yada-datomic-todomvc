(defproject yada-datomic "0.1.0-SNAPSHOT"
  :description "Boilerplate for yada + datomic"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[aero "1.1.2"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [com.datomic/datomic-pro "0.9.5561.56"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [io.rkn/conformity "0.5.1"]
                 [aleph "0.4.3"]
                 [cheshire "5.8.0"]
                 [datomic-schema "1.3.0"]
                 [yada "1.2.6"]]

  :source-paths ["src" "dev"]

  :repl-options {:init-ns user
                 :welcome (println "Type (dev) to start")}

  :profiles
  {:dev
   {:dependencies [[org.clojure/test.check "0.9.0"]]
    :source-paths ["dev"]}}

  :repositories
  {"my.datomic.com" {:url "https://my.datomic.com/repo"
                     :creds :gpg}})
