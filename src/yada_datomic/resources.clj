(ns yada-datomic.resources
  (:require
   [datomic.api :as d]
   [schema.core :as s]
   [yada.resource :refer [resource]]
   [yada.yada :as yada]
   [yada-datomic.db :as db]))

(defn new-todo-resource [db]
  (resource
   {:description "TODO items"
    :produces [{:media-type #{"text/html"
                              "application/edn;q=0.9"
                              "application/json"}
                :charset "UTF-8"}]
    :methods
    {:get
     {:response
      (fn [_] {:greeting "Hello"})}}}))
