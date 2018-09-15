(ns ^:figwheel-no-load react-tutorial.dev
  (:require
    [react-tutorial.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
