(ns xep.core
  (:require [xep.bot :refer [raw-message->message handler]]
            [clojure.core.async :as async]
            [cheshire.core :as json]
            [aleph.tcp :as tcp]
            [manifold.stream :as s]))

(def tcp-config {:host "d.ocsf.in" :port 1985})

(defn pull-message [channel]
  (some-> channel async/<!! raw-message->message))

(defn push-message [client message]
  (s/put! client
          (json/generate-string
           (merge-with #(if (string? %1) %2 (merge %1 %2))
                       {:Data {:sender "xep-osos"} :Type "message"}
                       message))))

(defn receive []
  (let [client @(tcp/client tcp-config)
        channel (async/chan 10)]
    (s/connect client channel)
    (loop [message (pull-message channel)]
      (when-let [response-message (handler message)]
        (println response-message)
        (push-message client response-message))
      (recur (pull-message channel)))))

(defn -main [& args]
  (receive))
