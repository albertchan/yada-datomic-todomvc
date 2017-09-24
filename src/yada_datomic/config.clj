(ns yada-datomic.config)

(defn datomic-uri [config]
  (get-in config [:database :uri]))

(defn datomic-extra-schemas [config]
  (get-in config [:database :extra-schemas]))
