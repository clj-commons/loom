(ns loom.test.io
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [loom.attr :refer [add-attr-to-edges]]
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

(deftest dot-str-options-are-statements-test
  (testing ":graph and :node options each end their own line"
    (let [out (dot-str (graph [1 2]) :graph {:size "3,3"} :node {:shape :box})]
      (is (re-find #"(?m)^  graph \[\"size\"=\"3,3\"\]$" out))
      (is (re-find #"(?m)^  node \[\"shape\"=\"box\"\]$" out))))
  (testing "an empty options map adds nothing"
    (let [out (dot-str (graph [1 2]) :node {})]
      (is (not (re-find #"null" out)))
      (is (not (re-find #"(?m)^  node" out))))))

(defn- open-data-file
  "Runs loom.io/open-data with the viewer stubbed out and returns the temp file
  it wrote."
  [data ext]
  (let [opened (atom nil)]
    (with-redefs [loom.io/open (fn [f] (reset! opened f) nil)]
      (#'loom.io/open-data data ext))
    @opened))

(deftest open-data-short-extension-test
  (testing "a two-letter extension such as :ps no longer trips the temp-file prefix minimum"
    (let [f (open-data-file "%!PS" :ps)]
      (is (.exists f))
      (is (str/starts-with? (.getName f) "loom-"))
      (is (str/ends-with? (.getName f) ".ps"))
      (is (= "%!PS" (slurp f)))))
  (testing "bytes are written as-is and a leading dot in the extension is kept once"
    (let [data (.getBytes "<svg/>" "UTF-8")
          f (open-data-file data ".svg")]
      (is (str/ends-with? (.getName f) ".svg"))
      (is (not (str/ends-with? (.getName f) "..svg")))
      (is (= (seq data) (seq (java.nio.file.Files/readAllBytes (.toPath f))))))))

(deftest open-data-temp-file-is-owner-only-test
  (testing "on POSIX file systems the rendered graph is readable only by its owner"
    (when (.contains (.supportedFileAttributeViews (java.nio.file.FileSystems/getDefault)) "posix")
      (let [f (open-data-file "data" :png)
            perms (set (map str (java.nio.file.Files/getPosixFilePermissions
                                 (.toPath f) (make-array java.nio.file.LinkOption 0))))]
        (is (= #{"OWNER_READ" "OWNER_WRITE"} perms))))))

(deftest open-data-rejects-path-in-extension-test
  (testing "an extension carrying a path separator cannot place the file outside the temp directory"
    (let [opened (atom nil)]
      (with-redefs [loom.io/open (fn [f] (reset! opened f) nil)]
        (is (thrown? Exception (#'loom.io/open-data "x" "/../../loom-escape"))))
      (is (nil? @opened)))))
