(ns yada-datomic.schema
  (:require
   [datomic-schema.schema :as ds]
   [schema.core :as s]))

(def parts [(ds/part "app")])

(def schema
  [(ds/schema todo
             (ds/fields
              [ident :uuid :unique-identity]
              [title :string "TODO item's title" :indexed]
              [completed :boolean "A flag to denote whether the TODO item is completed"]))])

(s/defschema Database {:uri String :extra-schemas [s/Str]})

(s/defschema UserPort (s/both s/Int (s/pred #(<= 1024 % 65535))))

(s/defschema Config
  {:port UserPort
   :database Database})
