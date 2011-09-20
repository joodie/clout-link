(ns clout-link.args
  "Conversion of request/routes to function arguments")

(defn req
  "Normal ring-style; pass request as only argument"
  [req _]
  [req])

(defn route
  "Pass route-params only as ordered arguments"
  [req route]
  (map (:route-params req) (:args route)))

(defn params
  "Pass params map only "
  [req route]
  [(:params req)])

(defn route+params
  "Pass route-params as a seq and params as a map"
  [req route]
  [(map (:route-params req) (:args route)) (:params req)])

(defn route+form
  "Pass route-params as a seq and form-params as a map"
  [req route]
  [(map (:route-params req) (:args route)) (:form-params req)])
