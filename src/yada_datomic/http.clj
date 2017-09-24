(ns yada-datomic.http
  (:require
   [aleph.http :as http]
   [bidi.bidi :as bidi]
   [bidi.bidi :refer [routes-context]]
   [bidi.vhosts :refer [vhosts-model make-handler]]
   [com.stuartsierra.component :refer [system-map Lifecycle system-using using]]
   [schema.core :as s]
   [taoensso.timbre :as timbre :refer [infof]]
   [yada.yada :refer [handler listener]]))

(defrecord ServerComponent [config api]
  Lifecycle
  (start [component]
    (let [routes (:routes api)
          port (:port config)]
      (infof "Starting HTTP server on port: %s" port)
      (assoc component
             :server (listener routes {:port port}))))
  (stop [component]
    (infof "Stopping HTTP server")
    (when-let [close (some-> component :server :close)]
      (close))
    (dissoc component :server)))

(defn new-server-component [config]
  (map->ServerComponent {:config config}))
