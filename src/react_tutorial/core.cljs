(ns react-tutorial.core
    (:require
      [reagent.core :as r]))

;; -------------------------
;; Views

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

(defn square [{:keys [on-click value]}]
  [:button.square
    {:on-click #(on-click)}
    value])

(defn board [& {:keys [squares on-click]}]
  (letfn
    [(render-square [i]
       [square {
         :value (squares i)
         :on-click #(on-click i)
       }])]
        [:div
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
            (render-square 8)]]))

(defn game []
  (let [state (r/atom {:history (vec [{:squares (vec (repeat 9 ""))}])
                       :step-number 0
                       :x-is-next? true})]
    (fn []
      (letfn
        [(handle-click [i]
          (let [history (vec (take (inc (get @state :step-number)) (get @state :history)))
                current (last history)
                squares (get current :squares)
                x-is-next? (get @state :x-is-next?)]
            (when (and (= (calculate-winner squares) nil) (= (squares i) ""))
                  (swap! state
                         assoc :history
                         (conj history
                               (assoc-in current [:squares i] (if x-is-next? "X" "O"))))
                  (swap! state assoc :step-number (count history))
                  (swap! state assoc :x-is-next? (not x-is-next?)))))
          (jump-to [step]
            (swap! state assoc :step-number step)
            (swap! state assoc :x-is-next? (if (= (mod step 2) 0) true false)))]
            (let [history (get @state :history)
                  current (get history (get @state :step-number))
                  squares (get current :squares)
                  winner (calculate-winner squares)
                  moves (map-indexed (fn [move _]
                                       (let [desc (if (= move 0)
                                                      (str "Go to game start")
                                                      (str "Go to move #" move))]
                                         [:li {:key move} [:button {:on-click #(jump-to move)} desc]]))
                                     history)
                  status (if (= winner nil)
                              (str "Next player: " (if (get @state [:x-is-next?]) "X" "O"))
                              (str "Winner: " winner))]
                    [:div.game
                      [:div.game-board
                        [board :squares squares
                               :on-click handle-click]
                        ]
                    [:div.game-info
                      [:div status]
                    [:ol
                      moves
                    ]]])))))

(defn home-page []
  [game])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
