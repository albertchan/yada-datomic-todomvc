(ns yada-datomic.db
  (:require
   [clojure.java.io :as io]
   [com.stuartsierra.component :refer [system-map Lifecycle system-using using]]
   [datomic.api :as d]
   [datomic-schema.schema :as ds]
   [io.rkn.conformity :as c]
   [taoensso.timbre :as timbre :refer [errorf infof]]
   [yada-datomic.config :refer [datomic-uri datomic-extra-schemas]]
   [yada-datomic.schema :refer [schema parts]]))

(:import datomic.Datom)

(defn load-schema [conn schema]
  (c/ensure-conforms conn schema))

(defrecord Database [config]
  Lifecycle
  (start [component]
    (let [uri           (datomic-uri config)
          extra-schemas (datomic-extra-schemas config)]
      (try
        (infof "Creating database with uri: %s" uri)
        (assert uri)
        (d/create-database uri)

        (let [conn (d/connect uri)]
          (infof "Applying schema...")
          (load-schema conn
                       {:yada-datomic/init
                        {:txes
                         (into [] (map (fn [m] [m])
                                       (concat
                                        (ds/generate-parts parts)
                                        (ds/generate-schema schema))))}})
          (for [es extra-schemas]
            (load-schema conn (io/reader (io/resource es))))
          (assoc component :conn conn :db #(d/db conn)))
        (catch Exception e
          (errorf e "Failed to start database")
          (throw e)))))

  (stop [component]
    (infof "Stopping database component")
    (assoc component :conn nil :db nil)))

(defn new-database
  "Creating a new database component"
  [config]
  (->Database config))

(defn paginate [skip limit coll]
  (cond
    (or (nil? skip) (nil? skip)) coll
        (= skip 0) coll
        :else (->> coll
                   (drop (* skip limit))
                   (take limit))))
