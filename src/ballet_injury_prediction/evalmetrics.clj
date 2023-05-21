(ns ballet-injury-prediction.evalmetrics)

(defn true-positive [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual :yes) (= predicted :yes))) (map vector actual-values predicted-values))))

(defn false-positive [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual :no) (= predicted :yes))) (map vector actual-values predicted-values))))

(defn false-negative [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual :yes) (= predicted :no))) (map vector actual-values predicted-values))))

(defn true-negative [actual-values predicted-values]
  (count (filter (fn [[actual predicted]] (and (= actual :no) (= predicted :no))) (map vector actual-values predicted-values))))

(defn compute-eval-metrics [actual predicted]
  (let [fp (false-positive actual predicted)
        tp (true-positive actual predicted)
        fn (false-negative actual predicted)
        tn (true-negative actual predicted)]
    (let [accuracy (/ (+ tp tn) (+ tp tn fp fn))
          precision (if (zero? (+ tp fp)) 0 (/ tp (+ tp fp)))
          recall (if (zero? (+ tp fn)) 0 (/ tp (+ tp fn)))
          f1 (* 2 (/ (* precision recall) (+ precision recall)))]
      (println "Accuracy:" accuracy)
      (println "Precision:" precision)
      (println "Recall:" recall)
      (println "F1:" f1))))