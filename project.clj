(defproject xep "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0-RC1"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [aleph "0.4.1-beta2"]
                 [cheshire "5.5.0"]
                 ]
  :main xep.core
  :core.typed {:check [xep.bot xep.try]})
