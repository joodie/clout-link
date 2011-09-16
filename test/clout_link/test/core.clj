(ns clout-link.test.core
  (:use [clout-link.defroute])
  (:use [clojure.test]
        [ring.mock.request]))

(defroute no-args :get "/foo")

(defroute my-route :get "/foo/:bar/:bla")



(deftest test-defroute
  (is (= (url-for-my-route 1 2)
         "/foo/1/2"))
  (let [handler (handle-my-route req
                                 (:params req))]
    (is (nil? (handler (request :get "/some/other/url"))))
    (is (= (handler (request :get "/foo/bar-value/bla-value"))
           {:bar "bar-value"
            :bla "bla-value"}))))


