#|
Sarah Babski, William Silva
Professor Lerner
Fundamentals II (Honors)
17 January 2014

Assignment #2: Fish Feeding Frenzy
|#

(require racket/class)
#lang class/0
(require 2htdp/image)
(require class/universe)

;;------------------------------------------------------------------------------
;;2.2 Fish Feeding Frenzy
;; In this game, you start off as a small fish in a pond of smaller and larger
;; fish, and to survive you must eat the smaller fish while avoiding being eaten
;; by the larger ones. Move your fish with the arrow keys. You win when you are
;; larger than all the other fish in the pond
;;------------------------------------------------------------------------------
;Constants
(define WIDTH 500)
(define HEIGHT 500)
(define MT (rectangle WIDTH HEIGHT "solid" "skyblue"))
(define PSPEED 4)
(define PLAYER-COLOR "salmon")
(define WINNING-SIZE 40)
(define WINNING-MESSAGE "You are the biggest fish! You win!")
(define LOSING-MESSAGE "GULP. You got eaten or quit early! You lost!")

;-------------------------------------------------------------------------------
;-------------------------------------------------------------------------------
;;Classes

; A Player is a (new player% Number Number Number)
; interp. the numbers represent coordinate position and fish size
(define-class player%
  (fields x y size)
  
  ; render : -> Image
  ; draws the player as a fish
  (check-expect (send player1 render) (circle 20 "solid" PLAYER-COLOR))
  (check-expect (send player2 render) (circle 10 "solid" PLAYER-COLOR))
  
  (define (render)
    (circle (send this size) "solid" PLAYER-COLOR))
  
  ; add-size : Number -> Player
  ; grows the player by an adjustment of the given amount
  (check-expect (send player1 add-size 15) (new player% 50 998 23))
  (check-expect (send player2 add-size 5) (new player% 100 100 11))
  
  (define (add-size n)
    (new player% (send this x) (send this y) (+ (/ n 5) (send this size))))
  
  ; key-event : String -> Player
  ; moves the player according to which key is pressed
  (check-expect (send player2 key-event "left") (new player% 96 100 10))
  (check-expect (send player2 key-event "right") (new player% 104 100 10))
  (check-expect (send player2 key-event "up") (new player% 100 96 10))
  (check-expect (send player2 key-event "down") (new player% 100 104 10))
  (check-expect (send player2 key-event "w") player2)
  
  (define (key-event ke)
    (local (; pmove : [Number Number -> Number] Number -> Number
            ; moves the player's given coordinate by the speed 
            ; in the given direction
            (define (pmove f n)
              (modulo (f n PSPEED) HEIGHT)))
      (cond [(string=? ke "left")
             (new player% (pmove - (send this x)) (send this y) 
                  (send this size))]
            [(string=? ke "right")
             (new player% (pmove + (send this x)) (send this y) 
                  (send this size))]
            [(string=? ke "up")
             (new player% (send this x) (pmove - (send this y)) 
                  (send this size))]
            [(string=? ke "down")
             (new player% (send this x) (pmove + (send this y)) 
                  (send this size))]
            [else this]))))

;;Examples of Player:
(define player1 (new player% 50 998 20))
(define player2 (new player% 100 100 10))


;-------------------------------------------------------------------------------

; A Non-Player is a (new np% Number Number NonNegativeNumber Number)
; interp. the numbers represent x- and y- coordinate position, fish size,
; and velocity
(define-class np%
  (fields x y size v)
  
  ; render : -> Image
  ; creates an image representation of the player in the 
  ; form of a colored circle
  (check-expect (send np1 render) (circle 35 "solid" "cadetblue"))
  (check-expect (send np2 render) (circle 15 "solid" "palevioletred"))
  
  (define (render)
    (local ((define s (send this size))
            (define color (cond [(< s 15) "orangered"]
                                [(< s 25) "palevioletred"]
                                [(< s 35) "mediumseagreen"]
                                [else "cadetblue"])))
      (circle s "solid" color)))
  
  ; step : -> Non-Player
  ; moves the fish horizontally by its velocity
  (check-expect (send np1 step) (new np% 196 200 35 -4))
  (check-expect (send np2 step) (new np% 102 100 15 2))
  
  (define (step)
    (new np% 
         (+ (send this x) (send this v))
         (send this y) 
         (send this size)
         (send this v)))
  
  ; off-screen? : -> Boolean
  ; is the non-player fish off of the visible screen?
  (check-expect (send np1 off-screen?) false)
  (check-expect (send np2 off-screen?) false)
  (check-expect (send np3 off-screen?) true)
  (check-expect (send np4 off-screen?) true)
  
  (define (off-screen?)
    (not (or (< 0 (send this x) WIDTH)
             (< 0 (send this y) HEIGHT)))))

;;Examples of Non-Player:
(define np1 (new np% 200 200 35 -4))
(define np2 (new np% 100 100 15 2))
(define np3 (new np% 1020 1020 20 1))
(define np4 (new np% (* 2 WIDTH) (* 2 HEIGHT) 20 1))


;-------------------------------------------------------------------------------

; A World is a (new world% Player [List-of Non-Player])
; interp. the first item is the player and the list is of the other fish
(define-class world%
  (fields p l)
  
  ; overlap? : Non-Player -> Boolean
  ; does the Non-Player overlap with the Player?
  (check-expect (send world2 overlap? np1) true)
  (check-expect (send world2 overlap? np2) false)
  (check-expect (send world2 overlap? np3) false)
  
  (define (overlap? f)
    (local ((define dist
              (sqrt (+ (sqr (- (send (send this p) x) (send f x)))
                       (sqr (- (send (send this p) y) (send f y)))))))
      (< dist (+ (send (send this p) size) (send f size)))))
  
  ; on-fish : -> [List-of Non-Player]
  ; return the list of fish the player is on
  (check-expect (send world1 on-fish) empty)
  (check-expect (send world2 on-fish) `(,np1))
  
  (define (on-fish)
    (filter (λ (i) (send this overlap? i))
            (send this l)))
  
  ; eat-fish : -> World
  ; grows the player size if a fish is eaten, removes eaten fish
  (check-expect (send world1 eat-fish) world1)
  (check-expect (send world2 eat-fish) 
                (new world% (new player% 200 200 45) `(,np2)))
  
  (define (eat-fish)
    (cond [(empty? (send this on-fish)) this]
          [else 
           (local ((define eatensize (foldr (λ (i j) (+ (send i size) j))
                                            0
                                            (send this on-fish)))
                   (define eatenl 
                     (filter (λ (i) (not (or (ormap (λ (j) (equal? j i)) 
                                                    (send this on-fish))
                                             (send i off-screen?))))
                             (send this l))))
             (if (not (send this die?))
                 (new world% 
                      (send (send this p) add-size eatensize)
                      eatenl)
                 this))]))
  
  ; addf : -> [List-of Non-Player]
  ; either adds a new randomly-generated fish or does not
  (define (addf)
    (local ((define randv (if (= (random 2) 0) (add1 (random 4))
                              (- (random 4) 4)))
            (define side (if (> randv 0) 0 WIDTH))
            ; randomf : -> Non-Player
            ; generates a fish with a random placement, size, and velocity
            (define (randomf)
              (new np% side 
                   (random (add1 HEIGHT)) (* 5 (add1 (random 8))) randv)))
      (if (= (random 25) 0)
          (cons (randomf) (send this l))
          (send this l))))
  
  ; rmf : -> World
  ; removes all non-player fish when they are off of the screen
  (check-expect (send world1 rmf) world1)
  (check-expect (send world5 rmf) (new world% (new player% 200 200 20) '()))
  
  (define (rmf)
    (new world% (send this p)
         (filter (λ (i) (not (send i off-screen?))) (send this l))))
  
  ; on-tick : -> World
  ; moves the non-player fish, removes all out-of-bounds fish, adds new fish,
  ;  and changes the size of the player if a fish is eaten
  (check-expect (send world1 on-tick) 
                (new world% (send world1 p) (send world1 addf)))
  (check-expect (send world2 on-tick)
                (new world% (new player% 200 200 45) 
                     `(,(new np% 102 100 15 2))))
  
  (define (on-tick)
    (send (send (new world% 
                     (send this p) 
                     (map (λ (i) (send i step)) (send this addf))) eat-fish) 
          rmf))
  
  ; to-draw : -> Scene
  ; draws the player and the other fish on an empty scene
  (check-expect (send world1 to-draw) (place-image (circle 10 "solid" "salmon")
                                                   500 500 MT))
  (check-expect (send world3 to-draw) 
                (place-image (circle 10 "solid" "salmon")
                             200 200
                             (place-image (circle 35 "solid" "cadetblue")
                                          200 200 MT)))
  
  (define (to-draw)
    (place-image (send (send this p) render)
                 (send (send this p) x)
                 (send (send this p) y)
                 (foldr (λ (i j) 
                          (place-image (send i render)
                                       (send i x)
                                       (send i y)
                                       j))
                        MT
                        (send this l))))
  
  ; on-key : String -> World
  ; moves the player if arrow keys are pressed
  (check-expect (send world5 on-key "left")
                (new world% (new player% 196 200 20) `(,np3)))
  (check-expect (send world5 on-key "right")
                (new world% (new player% 204 200 20) `(,np3)))
  (check-expect (send world5 on-key "up")
                (new world% (new player% 200 196 20) `(,np3)))
  (check-expect (send world5 on-key "down")
                (new world% (new player% 200 204 20) `(,np3)))
  (check-expect (send world5 on-key "w")
                world5)
  
  (define (on-key ke)
    (new world% (send (send this p) key-event ke) (send this l)))
    
  ; die? : -> Boolean
  ; has the player eaten a fish bigger than it?
  (check-expect (send world1 die?) false)
  (check-expect (send world3 die?) true)
  (check-expect (send world4 die?) false)
  
  (define (die?)
    (cond [(empty? (send this on-fish)) false]
          [else (ormap (λ (i) (<= (send (send this p) size) (send i size)))
                       (send this on-fish))]))
  
  ; win? : -> Boolean
  ; is the player the biggest fish in the pond?
  (check-expect (send world1 win?) false)
  (check-expect (send world3 win?) false)
  (check-expect (send world4 win?) true)
  
  (define (win?)
    (> (send (send this p) size) WINNING-SIZE))  
  
  ; stop-when : -> Boolean
  ; ends the game if the player dies or wins
  (check-expect (send world2 stop-when) false)
  (check-expect (send world3 stop-when) true)
  (check-expect (send world4 stop-when) true)
  
  (define (stop-when)
    (or (send this die?)
        (send this win?))))
  
;;Examples of World:
(define world1 (new world% (new player% 500 500 10) '()))
(define world2 (new world% (new player% 200 200 38) `(,np1 ,np2)))
(define world3 (new world% (new player% 200 200 10) `(,np1)))
(define world4 (new world% (new player% 120 210 45) '()))
(define world5 (new world% (new player% 200 200 20) `(,np3)))

;-------------------------------------------------------------------------------
;;Main Function

;; fishy : Number -> String
;; creates an interactive game similar in which the player controls a fish equal
;; to the size of the given s (preferably 10 and less than the WINNING-SIZE for
;; fair play). Once their fish has been eaten or they win, a string is created
;; with an appropriate message
(define (fishy s)
  (local ((define (results world)
            (if (send world win?)
                WINNING-MESSAGE LOSING-MESSAGE)))
    (results (big-bang (new world% (new player% (/ WIDTH 2) (/ HEIGHT 2) s) '())))))

;-------------------------------------------------------------------------------
;-------------------------------------------------------------------------------





