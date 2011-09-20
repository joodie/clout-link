(ns clout-link.route
  (:require [clout-link.spec :as spec]
            [clout.core :as clout]))

(defn route
  "Make a route; method is a keyword matching request-method,
spec is a ring-compatible routing string with regexs defining
custom regex matches for the spec"
  [method spec & regexs]
  (let [p (spec/parse spec)]
    {:spec spec
     :method method
     :parsed-spec p
     :regexs regexs
     :clout-route (clout/route-compile spec
                                       (apply hash-map regexs))
     :args  (filter keyword? p)}))

(defn uri-for
  "Given a route and a seq of arguments, create a uri where
args are inserted into the spec."
  [route & args]
  (apply spec/uri-for (:parsed-spec route) args))

(defn handler-for
  ([route arg-f handler]
     (fn [request]
       (if (= (:request-method request)
              (:method route))
         (if-let [params (clout/route-matches (:clout-route route) request)]
           (apply handler (-> request
                              (assoc :route-params params
                                     :params (merge (:params request) params))
                              (arg-f route)))))))
  ([route handler]
     (handler-for route (fn [r _] r) handler)))


