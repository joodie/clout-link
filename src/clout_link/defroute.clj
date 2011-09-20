(ns clout-link.defroute
  (:use clout-link.route)
  (:require [clout.core :as clout]))

(defmacro defroute
  "Define a named route"
  [n method spec & regexs]
  `(def ~n (route ~method ~spec ~@regexs)))





