(ns xep.bot
  (:require [manifold.deferred :as d]
            [clojure.core.match :refer [match]]
            [cheshire.core :as json]
            [clojure.string :refer [replace starts-with? join trim]]
            [clojure.edn :as edn]
            [clojure.walk :refer [postwalk]]))

(def state (agent 0))

(defn raw-message->message [barray]
  (try
    (-> (String. barray "UTF-8") (json/parse-string keyword))
    (catch Exception e nil)))

(defn safe-parse-edn [body]
  (try
    (edn/read-string body)
    (catch java.lang.RuntimeException e (println e))))

(def allowed-functions #{'letfn 'let '_ 'contains? 'as-> 'sequence 'into 'transduce 'take 'odd? 'flatten 'x 'y 'z 'group-by 'partition 'merge 'count 'reduce '-> 'seq 'string? 'keyword 'clojure.walk/postwalk 'value 'if 'fn 'xep.bot/state 'agent 'send 'deref '+ '- '* '/ 'map 'inc 'dec 'even? 'filter 'list 'vector 'apply 'comp '= 'not= 'quote 'for 'format 'some 'keep 'restart-agent})

(defn check-expr [expr]
  (let [denied-functions (transient [])]
    (postwalk
     (fn [node]
       (when (and (symbol? node) (not (contains? allowed-functions node)))
         (conj! denied-functions node))
       node)
     expr)
    (persistent! denied-functions)))

(defn clj>-exec [sender body]
  (if-let [expr (-> body (replace #"clj>" "") trim safe-parse-edn)]
    (as-> (check-expr expr) $
      (if (seq $)
        (format "%s denied" (join " " $))
        (try
          (-> expr eval pr-str)
          (catch Exception e (.getMessage e)))))
    (format "%s: Анус себе %s, пёс" sender body)))

(defn try-eval [sender body]
  (cond
    (starts-with? body "clj>") {:Data {:body (clj>-exec sender body)}}
    (starts-with? body "дёрг ") {:Data {:body (if (= sender "ascrazy@jabber.ru")
                                                (format "ascrazy@jabber.ru: вам запрещено дёргать чужой анус")
                                                (format "/me дёрнул анус %s" (re-find #"(?<=дёрг ).*" body)))}}))

(defn handler [message]
  (match [message]
         [{:Type "ping"}] {:Type "pong"}
         [{:Data {:body "пщ"} :Type "message"}] {:Data {:body "(пщ)"}}
         [{:Data {:body "test"} :Type "message"}] {:Data {:body "||"}}
         [{:Data {:body "ping"} :Type "message"}] {:Data {:body "Clj bot alive!"}}
         [{:Data {:body "ня!"} :Type "message"}] {:Data {:body "ня!"}}
         [{:Data {:sender sender :body "дёрг"} :Type "message"}] {:Data {:body (format "/me дёрнул анус %s по его просьбе" sender)}}
         [{:Data {:sender sender :body body} :Type "message"}] (try-eval sender body)
         [nil] nil
         :else (println (format "Missing handler for message %s" message))))
