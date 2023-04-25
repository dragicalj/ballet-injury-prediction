(ns ballet-injury-prediction.outliers
  (:import [java.lang Integer])
  (:require [clojure.core.matrix :as m]))

(defn quantile [xs q]
  (let [n (count xs)
        i (-> (* n q)
              (+ 1/2)
              (int))]
    (nth (sort xs) i)))

(defn count-outliers [data]
  (let [q1 (quantile data 0.25)
        q3 (quantile data 0.75)
        iqr (- q3 q1)
        upper-thresh (+ q3 (* 1.5 iqr))
        lower-thresh (- q1 (* 1.5 iqr))
        outliers (filter #(or (< % lower-thresh) (> % upper-thresh)) data)]
    (count outliers)))


(defn count-outliers-for-all [data]
  (let [matrix (m/matrix data)]
    (->> (m/transpose matrix)
         (mapv #(let [outlier-count (count-outliers %)]
                  outlier-count))
         (m/to-nested-vectors))))
