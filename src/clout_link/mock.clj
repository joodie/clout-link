(ns clout-link.mock
  (:require
   [clout-link.route :as route]
   [ring.mock.request :as ring-mock]))

(defn request
  ([route & route-args-and-params]
     (ring-mock/request (:method route)
                        (apply route/uri-for route route-args-and-params))))

