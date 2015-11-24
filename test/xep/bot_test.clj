(ns xep.bot-test
  (:require [clojure.test :refer :all]
            [xep.bot :refer :all]))

(deftest check-expr-test
  (is (= [] (check-expr '(inc 1))))
  (is (= [] (check-expr '(map inc [1]))))
  (is (= ['rm 'fr] (check-expr '(rm fr [1])))))

(deftest handler-test
  (is (= {:Data {:body "(2 3 4)"}}
         (handler {:Data {:sender "debasher" :body "clj> (map inc [1 2 3])"} :ID 0 :Type "message"})))
  (is (= {:Data {:body "Divide by zero"}}
         (handler {:Data {:sender "debasher" :body "clj> (/ 1 0)"} :ID 0 :Type "message"})))
  (is (= {:Data {:body "Clj bot alive!"}}
         (handler {:Data {:sender "debasher" :body "ping"} :ID 0 :Type "message"})))
  (is (= {:Data {:body "||"}}
         (handler {:Data {:sender "debasher" :body "test"} :ID 0 :Type "message"})))
  (is (= {:Data {:body "2"}}
         (handler {:Data {:sender "denasje" :body "clj> (+ 1 1)"} :ID 0 :Type "message"})))
  (is (= {:Data {:body "/me дёрнул анус дёрнул анус дёрнул анус"}}
         (handler {:Data {:sender "kms_" :body "дёрг дёрнул анус дёрнул анус"} :ID 0 :Type "message"}))))

