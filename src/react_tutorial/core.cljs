(ns react-tutorial.core
    (:require
      [reagent.core :as r]))

;; -------------------------
;; Views

(defn square [{:keys [on-click value]}]
  ; (js/console.log (pr-str value))
  [:button.square
    ; {:on-click #(js/alert value)}
    {:on-click #(on-click)}
    value])

(defn calculate-winner [squares]
  (let [lines (vec [[0, 1, 2]
                    [3, 4, 5]
                    [6, 7, 8]
                    [0, 3, 6]
                    [1, 4, 7]
                    [2, 5, 8]
                    [0, 4, 8]
                    [2, 4, 6]])]
    (reduce (fn [_ [a b c]]
      (if (and (not= (squares a) "")
               (= (squares a) (squares b))
               (= (squares b) (squares c)))
        (reduced (squares a))
        nil))
      nil
      lines)))

(defn board []
  (let [state (r/atom {:squares (vec (repeat 9 ""))
                       :x-is-next? true})]
  (fn []
    (letfn
      [(handle-click [i]
        (let [x-is-next? (get @state [:x-is-next?])]
          (js/console.log (pr-str (calculate-winner (get @state :squares))))
          (swap! state assoc-in [:squares i] (if x-is-next? "X" "O"))
          (swap! state assoc [:x-is-next?] (not x-is-next?))))
       (render-square [i]
        [square {
          :value (let [v (get-in @state [:squares i])]
          v)
          :on-click #(handle-click i)
        }])]
        (let [status (if (= (calculate-winner (get @state :squares)) nil)
                         (str "Next player: " (if (get @state [:x-is-next?]) "X" "O"))
                         (str "Winner: " (calculate-winner (get @state :squares))))]
          [:div
            [:div.status status]
            [:div.board-row
            (render-square 0)
            (render-square 1)
            (render-square 2)]
            [:div.board-row
            (render-square 3)
            (render-square 4)
            (render-square 5)]
            [:div.board-row
            (render-square 6)
            (render-square 7)
            (render-square 8)]])))))

(defn game []
  [:div.game
    [:div.game-board
      [board]]
  [:div.game-info
    [:div
      ;"status"
    ]
  [:ol
    ;"todo"
    ]]])

(defn home-page []
  [game])


;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
