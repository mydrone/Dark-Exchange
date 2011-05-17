(ns darkexchange.controller.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.add-destination.add-destination :as add-destination]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.model.peer :as peers-model]
            [darkexchange.view.main.peer-tab :as peer-tab-view]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing.table DefaultTableModel]))

(defn find-destination-text [main-frame]
  (seesaw-core/select main-frame ["#destination-text"]))

(defn set-destination-text [main-frame destination]
  (.setText (find-destination-text main-frame) (.toBase64 destination)))

(defn create-destination-listener [main-frame]
  (fn [destination]
    (set-destination-text main-frame destination)))

(defn load-destination [main-frame]
  (let [destination (i2p-server/current-destination)]
    (if destination
      (set-destination-text main-frame destination)
      (i2p-server/add-destination-listener (create-destination-listener main-frame)))))

(defn find-peer-table [main-frame]
  (seesaw-core/select main-frame ["#peer-table"]))

(defn add-rows [table-model table-rows]
  (doseq [row table-rows]
    (.addRow table-model row)))

(defn reload-table-data [main-frame]
  (let [peer-table (find-peer-table main-frame)
        peer-table-model (.getModel peer-table)]
    (.setRowCount peer-table-model 0)
    ;(add-rows peer-table-model (peers-model/all-table-row-peers))
    (seesaw-core/config! peer-table :model [:columns peer-tab-view/peer-table-columns
                                            :rows (peers-model/all-peers)])))

(defn load-peer-table [main-frame]
  (let [peer-table (find-peer-table main-frame)]
    (reload-table-data main-frame)))

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-button"]))

(defn attach-listener-to-add-button [main-frame]
  (seesaw-core/listen (find-add-button main-frame) :action
    (fn [e]
      (add-destination/show #(reload-table-data main-frame)))))

(defn find-test-button [main-frame]
  (seesaw-core/select main-frame ["#test-button"]))

(defn attach-listener-to-test-button [main-frame]
  (seesaw-core/listen (find-test-button main-frame) :action
    (fn [e]
      (i2p-server/send-message (i2p-server/current-destination) "ping"))))

(defn attach [main-frame]
  (load-destination main-frame)
  (load-peer-table main-frame)
  (attach-listener-to-add-button main-frame)
  (attach-listener-to-test-button main-frame))