(ns clout-link.args
  "Conversion of request/routes to function arguments")

(defn- fetch-route-params
  "Get vector of route params for this route from the req"
  [req route]
  (vec (map (:route-params req) (:args route))))

(defn req
  "Normal ring-style; pass request as only argument"
  [req _]
  [req])

(defn route
  "Pass route-params only as ordered arguments"
  [req route]
  (fetch-route-params req route))

(defn params
  "Pass params map only "
  [req route]
  [(:params req)])

(defn route+params
  "Pass route-params as a seq and params as a map"
  [req route]
  [(fetch-route-params req route) (:params req)])

(defn route+form
  "Pass route-params as a seq and form-params as a map"
  [req route]
  [(fetch-route-params req route) (:form-params req)])

(defn none
  "Pass nothing"
  [_ _]
  [])

(defn method
  "Only pass the http method"
  [req _]
  [(:request-method req)])

(defn method+headers
  [req _]
  [(:request-method req) (:headers req)])

(defn method+route+params
  [req route]
  [(:request-method req) (fetch-route-params req route) (:params req)])

(defn args
  "Specify argument keys"
  [& keys]
  (fn [req route]
    (map #(if (= % :route)
            (fetch-route-params req route)
            (req %)) keys)))
