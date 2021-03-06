(ns darkexchange.model.interceptors.client-interceptors
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.interceptors.interceptor-util :as interceptor-util]))

(def interceptors (atom []))

(defn add-interceptor [interceptor]
  (swap! interceptors #(cons interceptor %)))

(defn run-interceptors [function request-map]
  (interceptor-util/run-interceptors @interceptors function request-map))