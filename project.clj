(defproject ca.gt0.theasp/macchiato-core-async "0.2.2"
  :description "Core.async ring handler support for Macchiato"
  :url "https://github.com/theasp/macchiato-core-async"
  :license {:name "MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.145"]
                 [com.taoensso/timbre "4.10.0"]
                 [macchiato/core "0.2.10"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :plugins [[lein-npm "0.6.2"]
            [lein-cljsbuild "1.1.7"]
            [lein-codox "0.10.3"]]
  :npm {:dependencies [[source-map-support "0.4.0"]]}
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release"]
  :target-path "target"

  :deploy-repositories [["clojars" {:username :env/clojars_username
                                    :password :env/clojars_password}]]

  :hooks [leiningen.cljsbuild]

  :codox {:language :clojurescript}

  :prep-tasks ["compile" ["cljsbuild" "once"]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :cljsbuild {:builds
              [{:id           "macchiato-core-async"
                :source-paths ["src/cljs"]
                :jar          true
                :compiler
                {:output-dir    "target/js"
                 :output-to     "target/js/macchiato_core_async.js"
                 :optimizations :whitespace
                 :pretty-print  false}}]})
