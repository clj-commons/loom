(ns cljs-runner
  (:require [cljs.build.api :as build]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def test-dir "test/loom/test")
(def output-dir "target/cljs")
(def generated-ns 'loom.test.generated-runner)
(def output-file (str output-dir "/loom-test.js"))

(defn clean-output! []
  (when (.exists (io/file output-dir))
    (doseq [file (reverse (file-seq (io/file output-dir)))]
      (io/delete-file file true))))

(defn test-namespaces []
  (->> (file-seq (io/file test-dir))
       (filter #(.isFile %))
       (filter #(str/ends-with? (.getName %) ".cljc"))
       (map #(-> (.getName %)
                 (str/replace #"\.cljc$" "")
                 (str/replace "_" "-")))
       (map #(symbol (str "loom.test." %)))
       sort))

(defn generated-source [namespaces]
  (str "(ns " generated-ns "\n"
       "  (:require [loom.test.runner]\n"
       "            [cljs.test]\n"
       (apply str (map #(str "            [" % "]\n") namespaces))
       "            [cljs.nodejs :as nodejs]))\n"
       "\n(nodejs/enable-util-print!)\n"
       "(defn -main []\n"
       "  (cljs.test/run-tests "
       (str/join " " (map #(str "'" %) namespaces)) "))\n"
       "\n(set! *main-cli-fn* -main)\n"))

(defn -main [& _]
  (let [namespaces (test-namespaces)
        generated-file (io/file output-dir "loom/test/generated_runner.cljs")]
    (clean-output!)
    (.mkdirs (.getParentFile generated-file))
    (spit generated-file (generated-source namespaces))
    (build/build (str output-dir)
                 {:main generated-ns
                  :output-to output-file
                  :target :nodejs
                  :output-dir (str output-dir "/compiled")
                  :optimizations :none
                  :source-paths ["src" "test" output-dir]})
    (let [process (-> (ProcessBuilder. (into-array String ["node" output-file]))
                      (.inheritIO)
                      (.start))
          exit-code (.waitFor process)]
      (System/exit exit-code))))
