(ns xdg-rc.core-test
  (:require [clojure.test :refer :all]
            [xdg-rc.core :refer :all]))

(deftest make-xdg-rc-filename-test
  (testing "It generates an rc name as expected."
    (is (= "foorc" (make-xdg-rc-filename "foo")))))

(deftest make-classic-rc-filename-test
  (testing "It generates a classic rc name as expected."
    (is (= ".foorc" (make-classic-rc-filename "foo")))))
