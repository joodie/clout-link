(ns clout-link.mock
  (:require
   [clout-link.route :as route]
   [ring.mock.request :as ring-mock]))

(defn request*
  [method route args]
  (ring-mock/request method
                     (apply route/uri-for route args)))

(defn request
  "create a request object for a given route and arguments.
route is either a clout-link route or a pair [method route]
which must be used when the route specifies more than one method."
  ([route & route-args-and-params]
     (if (vector? route)
       (request* (first route) (second route) route-args-and-params)
       (request* (if (= (count (:method route)) 1)
                   (first (:method route))
                   (throw (Exception. "Route has more than one method. Please specify which one to use.")))
                 route route-args-and-params))))

