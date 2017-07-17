A library for using `core.async` with [Macchiato](https://macchiato-framework.github.io/).

# Example

```clojure
(ns example
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]
   [hiccups.core :as hiccups :refer [html]])
  (:require
   [ca.gt0.macchiato-core-async :as m-async]
   [macchiato.server :as http]
   [macchiato.middleware.defaults :as defaults]
   [macchiato.middleware.restful-format :as restful-format]
   [macchiato.middleware.resource :as resource]
   [macchiato.util.response :as r]
   [bidi.bidi :as bidi]
   [bidi.ring :as bidi-ring]
   [taoensso.timbre :as timbre
    :refer-macros (tracef debugf infof warnf errorf)]))

(defn not-found [req]
  (-> [:html [:body [:h1 "404" ]]]
      (html)
      (r/ok))))

(defn index-page [req]
  (go 
    (-> [:html [:body [:p "Hi" ]]]
        (html)
        (r/ok))))
          
(def handlers {:index-page index-page
               :not-found  not-found})

(def routes ["/" index-page])

(def handler (-> (bidi-ring/make-handler routes #(get handlers (:not-found handlers))
                 (m-async/wrap-async)
                 (restful-format/wrap-restful-format {:keywordize? true})
                 (resource/wrap-resource "resources/public")
                 (defaults/wrap-defaults {}))))

(http/start {:handler    handler
             :host       host
             :port       port
             :on-success #(infof "HTTP server listening on %s:%s" host port)})
```
