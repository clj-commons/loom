(ns loom.test.io
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [loom.attr :refer [add-attr-to-edges add-attr]]
            [loom.graph :refer [graph]]
            [loom.io :refer [dot-str]]))

(defn- dot-unescape
  "Reads a DOT quoted-string body back into the text Graphviz would see."
  [s]
  (str/replace s #"\\(.)" "$1"))

(defn- edge-attr-value
  "Returns the text Graphviz would read for the :a attribute in dot-str output."
  [dot]
  (dot-unescape (second (re-find #"\"a\"=\"((?:\\.|[^\"\\])*)\"" dot))))

(deftest collection-attr-values-are-escaped-test
  (testing "quotes inside a collection value survive as one quoted DOT string"
    (let [v ["AA.\"bb\""]
          g (add-attr-to-edges (graph [1 2]) :a v [[1 2]])]
      (is (= (str v) (edge-attr-value (dot-str g))))))
  (testing "a backslash inside a collection value is kept"
    (let [v ["a\\b"]
          g (add-attr-to-edges (graph [1 2]) :a v [[1 2]])]
      (is (= (str v) (edge-attr-value (dot-str g)))))))

(deftest string-attr-values-keep-graphviz-escapes-test
  (testing "a string value is passed through, so a literal backslash-n stays a Graphviz line break"
    (let [g (add-attr-to-edges (graph [1 2]) :a "x\\ny" [[1 2]])]
      (is (re-find #"\"a\"=\"x\\ny\"" (dot-str g))))))
