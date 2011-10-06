(ns clout-link.route
  (:require [clout-link.spec :as spec]
            [clout-link.args :as args]
            [clout.core :as clout]))

(defn route
  "Make a route; `method` is a keyword matching request-method,
or a set of keywords. `spec` is a ring-compatible routing string with
`regexs` defining custom regex matches for the spec"
  [method spec & regexs]
  (let [p (spec/parse spec)
        out {:spec spec
             :method (if (set? method)
                       method
                       #{method})
             :parsed-spec p
             :regexs regexs
             :clout-route (clout/route-compile spec
                                               (apply hash-map regexs))
             :args  (filter keyword? p)}]
    (with-meta out (:doc spec))))

(defn uri-for
  "Given a `route` and a seq of `args`, create a uri where
`args` are inserted into the route spec."
  [route & args]
  (apply spec/uri-for (:parsed-spec route) args))

(defn handle
  "build a handler function for a route. the handler function tests
the uri and request method and if both match, calls `f` with the request.

A optional `arg-f` function can be used to parse the request; the
results will be applied to `f`. See also `clout-link.args`"
  ([route arg-f f]
     (fn [request]
       (if ((:method route) (:request-method request))
         (if-let [params (clout/route-matches (:clout-route route) request)]
           (apply f (-> request
                        (assoc :route-params params
                               :params (merge (:params request) params))
                        (arg-f route)))))))
  ([route f]
     (handle route args/req f)))


