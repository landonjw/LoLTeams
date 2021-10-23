(ns lolteams.frontend.views.util.forms)

(defn input->value [input]
  (-> input .-target .-value))

(defn input
  ([placeholder]
   (input placeholder {}))
  ([placeholder options]
   (let [icon (:icon options)
         on-change (:on-change options)
         input-type (get options :input-type "text")]
     (if icon
       [:div.field
        [:div.control.has-icons-left
         [:input.input {:type        input-type
                        :placeholder placeholder
                        :on-change   on-change}]
         [:span.icon.is-small.is-left
          [:i {:class icon}]]]]
       [:div.field
        [:div.control
         [:input.input {:type        input-type
                        :placeholder placeholder
                        :on-change   on-change}]]]))))

(defn full-width-button [text click-fn]
  [:button.button.is-primary.full-width {:type "button"
                                         :on-click click-fn}
   text])

(defn select [options on-change-fn]
  [:div.select.full-width
   [:select.full-width {:on-change on-change-fn}
    (map (fn [option] [:option {:value option} option]) options)]])