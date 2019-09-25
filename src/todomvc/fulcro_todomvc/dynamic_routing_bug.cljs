(ns fulcro-todomvc.dynamic-routing-bug
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defmutation load-page-a [_]
  (action [{:keys [state app]}]
          (js/setTimeout
           (fn []
             (dr/target-ready! app [:page/id :a]))
           120)))

(defmutation load-page-b [_]
  (action [{:keys [state app]}]
          (js/setTimeout
           (fn []
             (dr/target-ready! app [:page/id :b]))
           60)))

(defsc PageA [this props]
  {:query [::id]
   :initial-state {::id :a}
   :ident [:page/id ::id]
   :route-segment ["a"]
   :will-enter (fn [app _]
                 (dr/route-deferred
                  [:page/id :a]
                  (fn []
                    (comp/transact! app `[(load-page-a)]))))
   }
  (dom/h2 "Page A"))

(defsc PageB [this props]
  {:query [::id]
   :initial-state {::id :b}
   :ident [:page/id ::id]
   :will-enter (fn [app _]
                 (dr/route-deferred
                  [:page/id :b]
                  (fn []
                    (comp/transact! app `[(load-page-b)]))))
   :route-segment ["b"]}
  (dom/h2 "Page B"))

(dr/defrouter TopRouter [this {:keys [current-state]}]
  {:router-targets [PageA PageB]}
  #_(dom/div "domme div")
  (case current-state
    :initial (dom/div "What to show when the router is on screen but has never been asked to route")
    :pending (dom/div "Loading...")
    :failed (dom/div "Oops")
    (dom/div "default")))

(def ui-top-router (comp/factory TopRouter))

(defsc Root [this {:ui/keys [top-router current-route-segment]}]
  {:initial-state {:ui/top-router {}
                   :ui/current-route-segment "a"}
   :query [{:ui/top-router (comp/get-query TopRouter)}
           :ui/current-route-segment]}
  (dom/div
   (dom/h1 "Dynamic router bug reproduction")
   (dom/p "Current-route-segment: " (dom/strong current-route-segment))
   (dom/ul
    (dom/li (dom/a {:href "#" :onClick #(do
                                          (js/console.log "--------------------------------------------------")
                                          (dr/change-route this ["a"]))} "To page a"))
    (dom/li (dom/a {:href "#" :onClick #(do
                                          (js/console.log "--------------------------------------------------")
                                          (dr/change-route this ["b"]))} "To page b")))
   (ui-top-router top-router)))

