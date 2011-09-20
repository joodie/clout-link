(ns clout-link.test.spec
  (:use clout-link.spec
        clojure.test))

(deftest test-spec
  (is (= (parse "/foo/:bar")
         ["/foo/" :bar]))
  (is (= (parse "/foo")
         ["/foo"]))
  (is (= (parse "/this/:that!:my-stuff")
         ["/this/" :that "!" :my-stuff])))

(deftest test-insert-args
  (is (= (insert-args (parse "/foo") [])
         ["/foo"])))

(deftest test-uri-for
  (is (= (uri-for "/foo")
         "/foo"))
  (is (= (uri-for "/this/:that!:my-stuff"
                  "arg1" "arg2")
         "/this/arg1!arg2")))

(deftest test-uri-parsed
  (is (= (uri-for (parse "/foo"))
         "/foo"))
  (is (= (uri-for (parse "/this/:that!:my-stuff")
                  "arg1" "arg2")
         "/this/arg1!arg2")))
