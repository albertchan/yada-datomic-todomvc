(ns yada-datomic.api
  (:require
   [bidi.bidi :refer [routes-context]]
   [bidi.bidi :refer [RouteProvider]]
   [com.stuartsierra.component :refer [system-map Lifecycle system-using using]]
   [schema.core :as s]
   [taoensso.timbre :as timbre :refer [infof]]
   [yada.yada :refer [yada] :as yada]
   [yada-datomic.todo :as todo]))

(defn api [db]
  ["/api"
   [["/todos"
     [["" (-> (todo/new-todo-root-resource db))]
      [["/" :uuid] (-> (todo/new-todo-resource db))]]]]])

(s/defrecord ApiComponent [db routes]
  Lifecycle
  (start [component]
    (infof "Starting api component")
    (assoc component
           :routes (api db)))
  (stop [component]
    (infof "Stopping api component")))

(defn new-api-component []
  (->
   (map->ApiComponent {})
   (using [:db])))
