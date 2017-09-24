(ns yada-datomic.todo
  (:require
   [datomic.api :as d]
   [schema.core :as s]
   [yada.resource :refer [resource]]
   [yada.yada :as yada]
   [yada-datomic.db :as db]))

(s/defschema Todo
  {:title s/Str
   :completed s/Bool})

(defn datom-completed [id completed]
  (when (not (nil? completed))
    [:db/add id :todo/completed completed]))

(defn datom-title [id title]
  (when title
    [:db/add id :todo/title title]))

(defn make-todo-datoms [id title completed]
  (let [id (or id (d/tempid :db.part/app))]
    (keep identity
          [(datom-title id title)
           (datom-completed id completed)])))

(defn create-todo [database item]
  (let [{:keys [:title :completed]} item
        id (d/tempid :db.part/app)]
    @(d/transact (:conn database)
                 [{:db/id id
                   :todo/ident (d/squuid)
                   :todo/title title
                   :todo/completed completed}])))

(defn update-todo
  "Update a TODO entry to the database"
  [database id item]
  (let [{:keys [:title :completed]} item
        datoms (make-todo-datoms id title completed)]
    @(d/transact (:conn database) datoms)))

(defn delete-todo
  "Delete a TODO entry from the database"
  [database id]
  @(d/transact (:conn database)
               [[:db.fn/retractEntity [:todo/ident id]]]))

(defn get-todos [db]
  (if-let [todos (some-> (d/q '[:find [?todo ...]
                                :in $
                                :where
                                [?todo :todo/title]]
                              db))]
    todos))

(defn new-todo-resource [db]
  (resource
   {:description "TODO item"
    :parameters {:path {:uuid String}}
    :produces [{:media-type #{"text/html"
                              "application/edn;q=0.9"
                              "application/json"}
                :charset "UTF-8"}]
    :methods
    {:put
     {:parameters {:form Todo}
      :produces "application/json"
      :consumes "application/x-www-form-urlencoded"
      :response (fn [ctx]
                  (let [uuid (get-in ctx [:parameters :path :uuid])
                        item (get-in ctx [:parameters :form])]
                    (assert uuid)
                    (assert item)
                    (let [tx-result (update-todo db uuid item)
                          db-after (:db-after tx-result)
                          id (d/q '[:find ?id .
                                    :in $
                                    :where
                                    [?e :todo/ident ?id]
                                    [?e :todo/title ?title]]
                                  (:db-after tx-result))]
                      {:data
                       {:type "todo"
                        :id id
                        :attributes
                        (merge
                         {:title (:title item)
                          :completed (:completed item)})}})))}
     :delete
     {:produces "text/plain"
      :response (fn [ctx]
                  (let [id (get-in ctx [:parameters :path :uuid])]
                    (assert id)
                    (let [tx-result (delete-todo db id)
                          db-after (:db-after tx-result)]
                      nil)))}}}))

(defn new-todo-root-resource [db]
  (resource
   {:description "TODO items"
    :methods
    {:post
     {:parameters {:form Todo}
      :produces "application/json"
      :consumes "application/x-www-form-urlencoded"
      :response (fn [ctx]
                  (let [item (get-in ctx [:parameters :form])]
                    (assert item)
                    (let [tx-result (create-todo db item)
                          db-after (:db-after tx-result)
                          id (d/q '[:find ?id .
                                    :in $
                                    :where
                                    [?e :todo/ident ?id]
                                    [?e :todo/title ?title]]
                                  (:db-after tx-result))]
                      {:data
                       {:type "todo"
                        :id id
                        :attributes
                        (merge
                         {:title (:title item)
                          :completed (:completed item)})}})))}
     :get
     {:produces [{:media-type #{"text/html"
                                "application/edn;q=0.9"
                                "application/json"}
                  :charset    "UTF-8"}]
      :response (fn [ctx]
                  (let [q     (get-in ctx [:parameters :query :q])
                        limit (get-in ctx [:parameters :query :limit])
                        skip  (get-in ctx [:parameters :query :skip])
                        todos (db/paginate skip limit (get-todos ((:db db))))]
                    {:jsonapi {:version "1.0"}
                     :data [(for [t todos]
                              (let [db ((:db db))
                                    e  (d/entity db t)]
                                {:type "todo"
                                 :id   (:todo/ident e)
                                 :attributes
                                 (merge
                                  {:title     (:todo/title e)
                                   :completed (:todo/completed e)})}))]}))}}}))
