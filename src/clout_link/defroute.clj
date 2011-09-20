(ns clout-link.defroute
  (:use clout-link.route)
  (:require [clout.core :as clout]
            [clout-link.spec :as spec]))

(defmacro defroute
  "Define a named route"
  [n method spec & regexs]
  `(def ~(with-meta n {:doc (str "Route: " method " '" spec "'")})
     (route  ~method ~spec ~@regexs)))





