(ns clout-link.args
  "Conversion of request/routes to function arguments")

(defn req
  "Normal ring-style; pass request as only argument"
  [req _]
  [req])

(defn route
  "Pass route-params only as ordered arguments"
  [req route]
  (map (:route-params req) (map name (:args route))))

(defn params
  "Pass params map only "
  [req route]
  [(:params req)])




