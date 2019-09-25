(ns fulcro-todomvc.main
  (:require
   [fulcro-todomvc.dynamic-routing-bug :as dynamic-routing-bug]
   [com.fulcrologic.fulcro.networking.http-remote :as http]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.application :as app]))

(defonce app (app/fulcro-app {:client-did-mount (fn [app]
                                                  (dr/change-route app ["a"]))
                              :remotes {:remote (http/fulcro-http-remote {})}}))

(defn mount []
  (app/mount! app dynamic-routing-bug/Root "app"))

(defn start []
  (mount)
  )

(defn reload []
  (mount))
