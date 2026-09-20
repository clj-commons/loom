(ns loom.test.runner
  (:require [cljs.test :as t]))

(defmethod t/report [:cljs.test/default :end-run-tests]
  [{:keys [fail error]}]
  (when (pos? (+ fail error))
    (.exit js/process 1)))
