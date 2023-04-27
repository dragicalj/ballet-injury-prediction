(ns ballet-injury-prediction.core
  (:import [java.lang Integer])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ballet-injury-prediction.outliers :as o]
            [ballet-injury-prediction.standardization :as s]))

(defn load-csv [file]
  (with-open [reader (io/reader file)]
    (->> (csv/read-csv reader)
         (mapv vec))))

(def my-csv-file "data/ballet-dancers.csv")

(load-csv my-csv-file)

(def ballet-data (load-csv my-csv-file))

(println (rest ballet-data))
(doseq [row (take 10 ballet-data)]
  (println row))

(defn parse-numeric-data [data]
  (map #(mapv (fn [x] (Integer/parseInt x)) %)
       data))

(def ballet-data-without-first (rest ballet-data))

(def ballet-data-vectors (mapv vec ballet-data-without-first))

(def ballet-data-numeric-vectors (parse-numeric-data ballet-data-vectors))

(println (o/count-outliers-for-all ballet-data-numeric-vectors))

(def outlier-counts (o/count-outliers-for-all ballet-data-numeric-vectors))

(def column-names (first ballet-data))

(doseq [pair (map vector column-names outlier-counts)]
  (let [count (second pair)]
    (if (pos? count)
      (println (str "Number of outliers in " (first pair) " column: " count))
      (println (str "No outliers in " (first pair) " column.")))))


(def ballet-data-numeric-standardized (s/standardize ballet-data-numeric-vectors))
(println ballet-data-numeric-standardized)