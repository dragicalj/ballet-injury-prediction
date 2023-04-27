(ns ballet-injury-prediction.standardization
  (:require [clojure.core.matrix :as m]))

(defn mean [xs]
  (/ (reduce + xs)
     (count xs)))

(defn median [xs]
  (let [n   (count xs)
        mid (int (/ n 2))]
    (if (odd? n)
      (nth (sort xs) mid)
      (->> (sort xs)
           (drop (dec mid))
           (take 2)
           (mean)))))

(defn quantile [q xs]
  (let [n (dec (count xs))
        i (-> (* n q)
              (+ 1/2)
              (int))]
    (nth (sort xs) i)))

(defn iqr [xs]
  (- (quantile 0.75 xs) (quantile 0.25 xs)))

(defn standardize [data]
  (let [matrix (m/matrix data)]
    (->> (m/transpose matrix)
         (mapv #(let [median (median %)
                      iqr (iqr %)]
                  (m/div (m/sub % median) iqr)))
         (m/to-nested-vectors))))