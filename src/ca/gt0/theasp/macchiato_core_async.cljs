(ns ca.gt0.theasp.macchiato-core-async
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljs.core.async
    :refer [<! chan put! close! onto-chan to-chan]]
   [cljs.core.async.impl.protocols :as async-protos]
   [macchiato.util.response :as r]
   [taoensso.timbre :as timbre
    :refer-macros (tracef debugf infof warnf errorf)]))

(defn- read-port?
  "Here be dragons"
  [c]
  (satisfies? async-protos/ReadPort c))

;; TODO: UNTESTED
(defn stream-response
  "Handle a response, but read the body as a `core.async` channel"
  [{:keys [body] :as response}]
  (errorf "stream-response not implemented!")
  #_(let [stream (new js/stream.Writable)]
      (go-loop []
        (when-let [data (<! body)]
          (.write stream data)
          (recur))
        (.end stream))
      (assoc response :body stream)))

(defn handle-response
  "Handle a response, as a map or a `core.async` channel"
  [res response]
  (cond
    (nil? response)
    (do
      (warnf "Empty response")
      (res (r/not-found)))

    (read-port? response)
    (go (handle-response res (<! response)))

    (read-port? (:body response))
    (res (stream-response response))

    :else
    (res response)))

(defn wrap-async
  "Create a Macchiato ring handler that accepts `req`, `res` and
  `raise`, but wraps a more conventional ring handler that only
  accepts `req` as an argument, and can either return a map as a
  response, or a `core.async` channel that will have the response."
  [handler-fn]
  (fn [req res raise]
    (try
      (handle-response res (handler-fn req))
      (catch js/Error e
        (errorf "Caught exception: %s" (.-stack e))
        (raise e)))))
