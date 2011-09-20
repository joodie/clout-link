(ns clout-link.test.route
  (:use clout-link.route
        clojure.test))

(deftest test-route
  (let [r (route :get "/foo/:arg1/:arg2")]
    (is (= (uri-for r "test1" "test2")
           "/foo/test1/test2"))))


