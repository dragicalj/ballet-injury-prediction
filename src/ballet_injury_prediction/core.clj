(ns ballet-injury-prediction.core)
(require '[clojure.data.csv :as csv])
(require '[clojure.java.io :as io])

(defn load-csv [file]
  (with-open [reader (io/reader file)]
    (->> (csv/read-csv reader)
         (mapv vec))))

(def my-csv-file "data/ballet-dancers.csv")

(load-csv my-csv-file)

(def ballet-data (load-csv my-csv-file))

(doseq [row (take 10 ballet-data)]
  (println row))






