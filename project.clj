(defproject ballet-injury-prediction "0.1.0-SNAPSHOT"
  :description "A ballet injury prediction app using K-Nearest Neighbor(KNN) algorithm"
  :url "https://github.com/dragicalj/ballet-injury-prediction"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/data.csv "1.0.1"]
                 [org.clojure/clojure "1.10.1"]
                 [net.mikera/core.matrix "0.63.0"]]
  :repositories [["clojars" "https://clojars.org/repo/"]
                 ["central" "https://repo1.maven.org/maven2/"]]
  :repl-options {:init-ns ballet-injury-prediction.core})
