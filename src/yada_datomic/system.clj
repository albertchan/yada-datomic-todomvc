(ns yada-datomic.system
  (:require
   [aleph.http :as http]
   [bidi.bidi :as bidi]
   [com.stuartsierra.component :refer [system-map Lifecycle system-using using]]
   [schema.core :as s]
   [yada-datomic.api :refer [new-api-component]]
   [yada-datomic.db :refer [new-database]]
   [yada-datomic.http :refer [new-server-component]]
   [yada-datomic.schema :refer [Config]]
   [yada.yada :refer [handler listener]]))

(defn new-system-map [config]
  (system-map
   :database (new-database config)
   :api (new-api-component)
   :http (new-server-component config)))

(defn new-dependency-map []
  {:api {:db :database}
   :http [:api]})

(s/defn new-system [config :- Config]
  (-> (new-system-map config)
      (system-using (new-dependency-map))))
