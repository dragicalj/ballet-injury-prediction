(ns ballet-injury-prediction.core
  (:import [java.lang Integer])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ballet-injury-prediction.outliers :as o]
            Implemented calculation of evaluation metrics            [ballet-injury-prediction.standardization :as s]
            [ballet-injury-prediction.evalmetrics :as e]))

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

(def split (create-data-partition ballet-data-numeric-standardized-trans 0.7))

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

(defn euclidean-distance [first-vector second-vector]
  (Math/sqrt (reduce + (map #(* % %) (map - first-vector second-vector)))))

(defn nearest-neighbors [train-data new-data k]
  (take k
        (sort-by :distance
                 (map #(assoc % :distance (euclidean-distance (:attributes %) new-data)) train-data))))

(defn knn [train-data new-data k]
  (let [nearest-neighbors (nearest-neighbors train-data new-data k)
        classes (map :injury-risk nearest-neighbors)
        frequencies (frequencies classes)]
    (first (first (sort-by val > frequencies)))))

(def train-data transformed-train-data)
(def test-data transformed-test-data-without-class)

(doseq [ballet-dancer test-data]
  (let [k 3
        predicted-class (knn train-data (:attributes ballet-dancer) k)]
    (println "Ballet dancer characteristics:")
    (println ballet-dancer)
    (println "----------------------------------------------")
    (println "Prediction:")
    (if (= predicted-class :yes)
      (println "There is a potential risk of injury for this ballet dancer. It is recommended not to perform this week.")
      (println "There is no apparent risk of injury for this ballet dancer. Feel free to perform."))
    (println "----------------------------------------------")))

(def actual (map :injury-risk transformed-test-data))

(println actual)

(def predicted
  (map #(knn train-data (:attributes %) 3) test-data))

(println predicted)

(e/compute-eval-metrics actual predicted)