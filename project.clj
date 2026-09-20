(defproject aysylu/loom "1.2.0"
  :min-lein-version "2.0.0"
  :description "Graph library for Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.6" :scope "provided"]
                 [org.clojure/data.priority-map "1.2.1"]
                 [tailrecursion/cljs-priority-map "1.2.1"]]
  :url "https://github.com/clj-commons/loom"
  :scm {:name "git"
        :url "https://github.com/clj-commons/loom"
        :connection "scm:git:git://github.com/clj-commons/loom.git"
        :developerConnection "scm:git:ssh://git@github.com/clj-commons/loom.git"}
  :test-selectors {:default (fn [m] (not (:test-check-slow m)))
                   :all (constantly true)
                   :test-check-slow :test-check-slow}

  :aliases {"all" ["with-profile" "+clojure-1-10:+clojure-1-11:+clojure-1-12"]
            "release" ["do" "clean," "with-profile" "default" "deploy" "clojars"]}

  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.3"]]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.26.0"]]
                   :repl-options {:init (set! *print-length* 50)}}

             :clojure-1-10 {:dependencies [[org.clojure/clojure "1.10.3" :scope "provided"]]}
             :clojure-1-11 {:dependencies [[org.clojure/clojure "1.11.4" :scope "provided"]]}
             :clojure-1-12 {:dependencies [[org.clojure/clojure "1.12.6" :scope "provided"]]}})
