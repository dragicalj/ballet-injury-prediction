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

(defn create-data-partition [data p]
  (let [n (count data)
        size (int (* n p))
        shuffled-data (shuffle data)]
    {:train (subvec shuffled-data 0 size)
     :test (subvec shuffled-data size n)}))
(defn transpose-vectors [vectors]
  (let [matrix (vec vectors)]
    (->> matrix
         (apply mapv vector)
         (mapv vec))))
(def ballet-data-numeric-standardized-trans (transpose-vectors ballet-data-numeric-standardized))
(def split (create-data-partition ballet-data-numeric-standardized-trans 0.8))

(println "Training data:" (:train split))
(println "Test data:" (:test split))

(defn transform-data [data]
  (mapv (fn [sample]
          (let [attributes (subvec sample 0 7)
                injury-risk (if (= (nth sample 7) -1) :no :yes)]
            {:attributes attributes
             :injury-risk injury-risk}))
        data))

(def transformed-train-data (transform-data (:train split)))
(def transformed-test-data (transform-data (:test split)))

(println transformed-train-data)

(println transformed-test-data)

(defn transform-data-without-class [data]
  (map #(hash-map :attributes (:attributes %)) data))

(def transformed-test-data-without-class
  (transform-data-without-class transformed-test-data))

(println transformed-test-data-without-class)






