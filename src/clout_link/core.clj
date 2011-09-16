(ns clout-link.core
  (:use [clojure.contrib.str-utils :only [re-partition]]))

(def ^{:private true
       :doc "Regular expression matching variable parts of a route"}
  route-re
  #"\*|:([\p{L}_][\p{L}_0-9-]*)")

(defn parse-route
  "Parse a clout-style route into components.
Returns a seq of strings (constant parts) and keywords (variable parts)."
  [r]
  (remove #(= "" %)
          (map #(if (vector? %)
                  (if (= "*" (first %))
                    :*
                    (keyword (second %)))
                  %)
               (re-partition route-re r))))

(defn insert-args
  "Given a parsed route and a seq of values, return a route vector
with the values inserted."
  [parsed-route args]
  (loop [out []
         [route-head & route-rest] parsed-route
         arg args]
    (if route-head
      (if (string? route-head)
        (recur (conj out route-head)
               route-rest
               arg)
        (if (not args)
          (throw (Exception. (str "Too few arguments for route " (print-str parsed-route) (print-str args))))
          (recur (conj out (first arg))
                route-rest
                (next arg))))
      (if (seq arg)
        (throw (Exception. (str "Too many arguments for route" (print-str parsed-route) (print-str args))))
        out))))

(defn link-for-route
  "Given a route spec (string or parsed route) and arguments, return a
url string for that route"
  [route & args]
  (let [parts (if (vector? route)
                route
                (parse-route route))]
    (apply str (insert-args parts args))))


