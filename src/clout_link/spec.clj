(ns clout-link.spec
  "Parsing of clout/ring-style route strings"
  (:use [clojure.contrib.str-utils :only [re-partition]]))

(def ^{:doc "Regular expression matching variable parts of a route"}
  spec-re
  #"\*|:([\p{L}_][\p{L}_0-9-]*)")

(defn parse
  "Parse a clout-style spec into components.
Returns a seq of strings (constant parts) and keywords (variable parts)."
  [spec]
  (remove #(= "" %)
          (map #(if (vector? %)
                  (if (= "*" (first %))
                    :*
                    (keyword (second %)))
                  %)
               (re-partition spec-re spec))))

(defn insert-args
  "Given a parsed spec and a seq of values, return a route vector
with the values inserted."
  [parsed-spec args]
  (loop [out []
         [spec-head & spec-rest] parsed-spec
         arg args]
    (if spec-head
      (if (string? spec-head)
        (recur (conj out spec-head)
               spec-rest
               arg)
        (if (not args)
          (throw (Exception. (str "Too few arguments for spec " (print-str parsed-spec) (print-str args))))
          (recur (conj out (first arg))
                spec-rest
                (next arg))))
      (if (seq arg)
        (throw (Exception. (str "Too many arguments for spec" (print-str parsed-spec) (print-str args))))
        out))))

(defn uri-for
  "Given a route spec (string or parsed route) and arguments, return a
url string for that route"
  [route & args]
  (let [parts (cond
               (or (vector? route)
                   (seq? route))
               route
               (map? route)
               (:parsed-spec route)
               :else
               (parse route))]
    (apply str (insert-args parts args))))
