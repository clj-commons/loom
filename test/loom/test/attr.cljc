(ns loom.test.attr
  (:require [loom.graph :refer (add-edges digraph graph remove-edges)]
            [loom.attr :refer (add-attr add-attrs-to-all attr remove-attr add-attr-to-nodes add-attr-to-edges)]
            #?@(:clj [[clojure.test :refer [deftest is]]]))
  #?@(:cljs [(:require-macros [cljs.test :refer (deftest is)])]))

(deftest attr-graph-test
  (let [g (digraph [1 2] [2 3] [2 4] [3 5] [4 5])
        lg1 (-> g
                (add-attr 1 :label "node label")
                (add-attr 2 3 :label "edge label"))
        lg2 (-> g
                (add-attr-to-nodes
                 :label "node odd" [1 3 5])
                (add-attr-to-nodes
                 :label "node even" [2 4])
                (add-attr-to-edges
                 :label "edge from node 2" [[2 3] [2 4]])
                (add-attr-to-edges
                 :label "edge to node 5" [[3 5] [4 5]]))]
    (is (= "node label" (attr lg1 1 :label)))
    (is (= "edge label" (attr lg1 2 3 :label)))
    (is (= "node odd" (attr lg2 1 :label)))
    (is (= "node odd" (attr lg2 3 :label)))
    (is (= "node odd" (attr lg2 5 :label)))
    (is (= "node even" (attr lg2 2 :label)))
    (is (= "node even" (attr lg2 4 :label)))
    (is (= "edge from node 2" (attr lg2 2 3 :label)))
    (is (= "edge from node 2" (attr lg2 2 4 :label)))
    (is (= "edge to node 5" (attr lg2 3 5 :label)))
    (is (= "edge to node 5" (attr lg2 4 5 :label)))))

(deftest add-attrs-to-all-pairs-key-values
  (let [g (-> (digraph [1 2])
              (add-attrs-to-all :color :red :label "x"))]
    (is (= :red (attr g 1 :color)))
    (is (= "x" (attr g 1 :label)))
    (is (= :red (attr g 1 2 :color)))
    (is (= "x" (attr g 1 2 :label)))
    (is (nil? (attr g 1 :red)))
    (is (nil? (attr g 1 2 :red)))))

(deftest remove-attr-clears-both-directions-of-undirected-edge
  (let [g (-> (graph [1 2])
              (add-attr 1 2 :label "x")
              (remove-attr 1 2 :label))]
    (is (nil? (attr g 1 2 :label)))
    (is (nil? (attr g 2 1 :label)))))

(deftest remove-edges-prunes-edge-attrs
  (let [g (-> (graph [1 2] [2 3])
              (add-attr 1 2 :label "x")
              (add-attr 2 3 :label "y")
              (add-attr 1 :color :red)
              (remove-edges [1 2])
              (add-edges [1 2]))]
    (is (nil? (attr g 1 2 :label)))
    (is (nil? (attr g 2 1 :label)))
    (is (= "y" (attr g 2 3 :label)))
    (is (= :red (attr g 1 :color)))))

(deftest remove-edges-prunes-edge-attrs-in-directed-graph
  (let [g (-> (digraph [1 2] [2 1])
              (add-attr 1 2 :label "x")
              (add-attr 2 1 :label "y")
              (remove-edges [1 2])
              (add-edges [1 2]))]
    (is (nil? (attr g 1 2 :label)))
    (is (= "y" (attr g 2 1 :label)))))
