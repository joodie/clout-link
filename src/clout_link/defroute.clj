(ns clout-link.defroute
  (:use clout-link.core)
  (:require [clout.core :as clout]))

(defn make-handler-fn
  [spec regexs method bindings body]
  (let [real-bindings (if (vector? bindings)
                        {{:keys bindings} :params}
                        bindings)]
    `(let [route# (clout/route-compile ~spec ~(apply hash-map regexs))]
       (fn [request#]
         (if (= ~method
                (:request-method request#))
           (if-let [params# (clout/route-matches route# request#)]
             (let [~real-bindings (assoc request#
                                    :route-params params#
                                    :params (merge (:params request#) params#))]
               ~@body)))))))

(defmacro defroute
  [n method spec & regexs]
  (let [p (parse-route spec) 
        args (map #(symbol (name %)) (filter keyword? p))
        url-for-sym (symbol (str "url-for-" (name n)))
        route {:spec spec
               :method method
               :parsed-route (list `quote p)
               :regexs (list `quote regexs)
               :args (list `quote args)}]
    `(do
       (defn ~url-for-sym
         ~(vec args)
         (str ~@(insert-args p args)))
       (defmacro ~(symbol (str "handle-" (name n)))
         {:arglists '([~(vec args) & body] [request & body])}
         [~'bindings & ~'body]
         (make-handler-fn ~spec ~regexs
                          ~method ~'bindings ~'body))
       (def ~n (assoc ~route :url-fn ~url-for-sym)))))




