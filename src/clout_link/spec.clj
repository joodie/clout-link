(ns clout-link.spec
  "Parsing of clout/ring-style route strings"
  (:require [clojure.string :as string])
  (:import java.net.URLEncoder
           java.util.regex.Pattern))

(defn- re-partition
  "Splits the string into a lazy sequence of substrings, alternating
between substrings that match the patthern and the substrings
between the matches. The sequence always starts with the substring
before the first match, or an empty string if the beginning of the
string matches.

For example: (re-partition #\"[a-z]+\" \"abc123def\")

Returns: (\"\" \"abc\" \"123\" \"def\")"
  [#^Pattern re string]
  (let [m (re-matcher re string)]
    ((fn step [prevend]
       (lazy-seq
        (if (.find m)
          (cons (.subSequence string prevend (.start m))
                (cons (re-groups m)
                      (step (+ (.start m) (count (.group m))))))
          (when (< prevend (.length string))
            (list (.subSequence string prevend (.length string)))))))
     0)))

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

(defn encode-uri-path
  "Use rfc 3986 to encode s"
  [s]
  (.toASCIIString (java.net.URI. nil s nil)))

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

(defn- encode-params
  "Turn a map of parameters into a urlencoded string."
  [params]
  (string/join "&"
    (for [[k v] params]
      (str (URLEncoder/encode (name k)) "="
           (URLEncoder/encode (str v))))))


(defn uri-for
  "Given a route spec (string or parsed sequence) and arguments, return a
url string for that route. Optionally takes a map of parameters to encode
and append to the uri as the last argument."
  [route & route-args-and-params]
  (let [params* (last route-args-and-params)
        has-params? (map? params*)
        params (if has-params?
                 params*
                 nil)
        route-args (if has-params?
                     (butlast route-args-and-params)
                     route-args-and-params)
        parts (cond
               (or (vector? route)
                   (seq? route))
               route
               :else
               (parse route))
        uri (encode-uri-path (apply str (insert-args parts route-args)))]
    (if has-params?
      (str uri "?" (encode-params params))
      uri)))

