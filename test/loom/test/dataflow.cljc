(ns loom.test.dataflow
  (:require [loom.dataflow :refer [dataflow-analysis]]
            [loom.graph :refer (digraph)]
            [clojure.set :as set]
            #?@(:clj [[clojure.test :refer [deftest is testing]]]
                :cljs [cljs.test]))
  #?@(:cljs [(:require-macros [cljs.test :refer (deftest testing is)])]))

(deftest dataflow-analysis-test
  (testing "reaching definitions around a loop"
    (let [g (digraph [:entry :cond] [:cond :body] [:body :cond]
                     [:cond :exit] [:exit :after])]
      (is (= {:entry #{:entry}
              :cond  #{:entry :cond :body}
              :body  #{:entry :cond :body}
              :exit  #{:entry :cond :body :exit}
              :after #{:entry :cond :body :exit :after}}
             (dataflow-analysis {:start :entry
                                 :graph g
                                 :join #(apply set/union %)
                                 :transfer (fn [node in] (conj (set in) node))}))))))
