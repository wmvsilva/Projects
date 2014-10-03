#|
Sarah Babski, William Silva
Professor Lerner
Fundamentals II (Honors)
22 January 2014

Assignment #3: Fundamentals + Pong
|#

#lang class/1
(require 2htdp/image)
(require class/universe)

;;------------------------------------------------------------------------------
;;Problem 8
;;------------------------------------------------------------------------------
;;Data Definitions

(define WIDTH 500)
(define HEIGHT 500)
(define MT (empty-scene WIDTH HEIGHT "black"))
(define DELTA 15)
(define X-LEFT 10)
(define X-RIGHT 490)
(define PLAY-HEIGHT 40)
(define PLAY-WIDTH 10)
(define BALL-X-SPEED 5)
(define BALL-DY-CHANGE 5)
(define WINNING-SCORE 7)

;; A Score is a (make-score Number Number)
(define-struct score (left right))

;; A Player is a (new player% Number Number)

;; A WaitingBall is a (new waiting-ball% Direction Number)

;; A Ball is a (new ball% Number Number Number Score)

;; An IBall can do:
;; draw : -> Image
;; draw-on : Image -> Image
;; next : -> IBall

;; A World is a (new world% Player Player Ball)

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Player Class

(define-class player%
  (fields y)
  
  ;; draw : -> Image
  ;; creates an image representation of the paddle
  (define (draw)
    (rectangle PLAY-WIDTH PLAY-HEIGHT "solid" "white"))
  
  ;; up : -> Player
  ;; moves this player up vertically a specified number of pixels
  ;; if the player is touching the top of the screen, returns this player
  (check-expect (play1 . up) play1)
  (check-expect (play2 . up) (new player% (- 200 DELTA)))
  (check-expect (play3 . up) (new player% (- HEIGHT DELTA)))
  
  (define (up)
    (if (<= (this . y) (/ PLAY-HEIGHT 2))
        this
    (new player% (- (this . y) DELTA))))
  
  ;; down : -> Player
  ;; moves this player down vertically a specified number of pixels
  ;; if the player is touching the bottom of the screen, returns this player
  (check-expect (play1 . down) (new player% DELTA))
  (check-expect (play2 . down) (new player% (+ 200 DELTA)))
  (check-expect (play3 . down) (new player% HEIGHT))
  
  (define (down)
    (if (>= (this . y) (- HEIGHT (/ PLAY-HEIGHT 2)))
        this
    (new player% (+ (this . y) DELTA)))))

;;Examples of Players:
(define play1 (new player% 0))
(define play2 (new player% 200))
(define play3 (new player% HEIGHT))

;;------------------------------------------------------------------------------
;;Ball Class

(define-class ball%
  (fields x y dir dy score)
  
  ;; draw : -> Image
  ;; creates an image representation of the ball
  (define (draw)
    (circle 5 "solid" "white"))
  
  ;; draw-on : Image -> Image
  ;; draws this ball (and scores for each player) onto a given scn
  (check-expect (ball1 . draw-on MT)
                (place-image (circle 5 "solid" "white")
                 0
                 0
                 (place-image (text "0" 36 "White")
                              100
                              50
                              (place-image (text "0" 36 "White")
                              400
                              50
                              MT))))
  (check-expect (ball2 . draw-on MT)
                (place-image (circle 5 "solid" "white")
                 250
                 250
                 (place-image (text "1" 36 "White")
                              100
                              50
                              (place-image (text "1" 36 "White")
                              400
                              50
                              MT))))
  
  (define (draw-on scn)
    (place-image (this . draw)
                 (this . x)
                 (this . y)
                 (place-image (text (number->string 
                                     (score-left (this . score))) 36 "White")
                              100
                              50
                              (place-image 
                               (text (number->string 
                                      (score-right (this . score))) 36 "White")
                              400
                              50
                              scn))))
  
  ;; next : -> IBall
  ;; moves the ball, bounces the ball off walls and paddles, and changed the
  ;; ball into a waiting ball (with score added) if it touches either of the
  ;; side walls
  (check-expect (ball1 . next 0 0) 
                (new waiting-ball% 'left 100 (make-score 0 1)))
  (check-expect (ball2 . next 0 0) 
                (new ball% 255 250 'right 0 (make-score 1 1)))
  (check-expect (ball3 . next 77 HEIGHT)  
                (new ball% 255 255 'right 5 (make-score 1 1)))
  
  (define (next left-y right-y)
    (this . move . bounce left-y right-y . score-ball))
  
  ;; move : -> Ball
  ;; moves the ball horizontally by a set amount and vertically by the dy field
  (check-expect (ball2 . move) (new ball% 255 250 'right 0 (make-score 1 1)))
  (check-expect (ball3 . move)  (new ball% 255 255 'right 5 (make-score 1 1)))
  (check-expect (ball4 . move) (new ball% 95 120 'left 20 (make-score 1 1)))
  
  (define (move)
    (new ball% (+ (this . x) (if (symbol=? 'left (this . dir))
                                 (* -1 BALL-X-SPEED)
                                 BALL-X-SPEED))
         (+ (this . y) (this . dy))
         (this . dir) (this . dy) (this . score)))
  
  ;; bounce : -> Ball
  ;; bounces the ball off walls and paddles
  (check-expect (ball5 . bounce 0 0) (new ball% 250 1 'left 5 (make-score 1 1)))
  (check-expect (ball6 . bounce 250 250) 
                (new ball% 11 250 'right 0 (make-score 1 1)))
  
  (define (bounce left-y right-y)
    (local (;; bounce-paddle : Ball Number Direction -> Ball
            ;; bounces the ball off of the paddle in the opposite direction with
            ;; a new dy-value depending on where the ball hit the paddle
            (define (bounce-paddle ball y dir)
              (local ((define ball-y (ball . y))
                      (define 10-pad (* 0.1 PLAY-HEIGHT))
                      (define new-x (if (symbol=? dir 'left)
                                        (add1 X-LEFT) (sub1 X-RIGHT)))
                      (define new-dir (if (symbol=? dir 'left) 'right 'left))
                      (define new-dy (cond [(<= (+ y (* -1 10-pad)) 
                                                ball-y (+ y 10-pad))
                                            (ball . dy)]
                                           [(> ball-y (+ y 10-pad))
                                            (+ (ball . dy) BALL-DY-CHANGE)]
                                           [(< ball-y (- y 10-pad))
                                            (- (ball . dy) BALL-DY-CHANGE)])))
                (new ball% new-x ball-y new-dir new-dy (ball . score)))))
      (cond [(<= (this . y) 0)
             (new ball% (this . x) 1 (this . dir) 
                  (* -1 (this . dy)) (this . score))]
            [(>= (this . y) HEIGHT)
             (new ball% (this . x) (sub1 HEIGHT) (this . dir) 
                  (* -1 (this . dy)) (this . score))]
            [(and (<= (- left-y (/ PLAY-HEIGHT 2)) 
                      (this . y) (+ left-y (/ PLAY-HEIGHT 2)))
                  (<= (this . x) X-LEFT))
             (bounce-paddle this left-y 'left)]
            [(and (<= (- right-y (/ PLAY-HEIGHT 2))
                      (this . y) (+ right-y (/ PLAY-HEIGHT 2)))
                  (>= (this . x) X-RIGHT))
             (bounce-paddle this right-y 'right)]
            [else this])))
  
  ;; score-ball : -> IBall
  ;; if the ball is outside of the scene, creates a waiting-ball with the score
  ;; to the appropriate player
  (check-expect (ball6 . score-ball) ball6)
  (check-expect ((new ball% -10 250 'left 0 (make-score 1 1)) . score-ball)
                (new waiting-ball% 'left 100 (make-score 1 2)))
  
  (define (score-ball)
    (cond [(<= (this . x) 0)
           (new waiting-ball% 'left 100
                (make-score (score-left (this . score))
                            (add1 (score-right (this . score)))))]
          [(>= (this . x) WIDTH)
           (new waiting-ball% 'right 100
                (make-score (add1 (score-left (this . score)))
                            (score-right (this . score))))]
          [else this])))

;;Examples of Balls:
(define ball1 (new ball% 0 0 'left 0 (make-score 0 0)))
(define ball2 (new ball% 250 250 'right 0 (make-score 1 1)))
(define ball3 (new ball% 250 250 'right 5 (make-score 1 1)))
(define ball4 (new ball% 100 100 'left 20 (make-score 1 1)))
(define ball5 (new ball% 250 0 'left -5 (make-score 1 1)))
(define ball6 (new ball% 10 250 'left 0 (make-score 1 1)))

;;------------------------------------------------------------------------------
;;WaitingBall Class

(define-class waiting-ball%
  (fields dir timer score)
  
  ;; draw-on : Image -> Image
  ;; draws this ball (and scores for each player) onto a given scn
  (check-expect (wb1 . draw-on MT) (place-image (text "0"
                                                      36 "White")
                                                100
                                                50
                                                (place-image (text "0"
                                                                   36 "White")
                                                             400
                                                             50
                                                             MT)))
  
  (define (draw-on scn)
                 (place-image (text (number->string (score-left (this . score)))
                                    36 "White")
                              100
                              50
                              (place-image (text (number->string 
                                                  (score-right (this . score)))
                                                 36 "White")
                              400
                              50
                              scn)))
  
  ;; next : Number Number -> IBall
  ;; the timer of this WaitingBall goes down with each run of next, if it has
  ;; reached zero, it creates a new Ball. Otherwise, this WaitingBall is
  ;; returned
  (check-expect (wb1 . next 250 250) 
                (new waiting-ball% 'left 99 (make-score 0 0)))
  (check-expect (wb2 . next 250 250) 
                (new ball% 250 250 'right 0 (make-score 2 3)))
  (check-expect (wb2 . next 100 100) 
                (new ball% 250 100 'right 0 (make-score 2 3)))
  
  (define (next left-y right-y)
    (cond [(zero? (this . timer))
           (new ball% (/ WIDTH 2) (if (symbol=? (this . dir) 'left) 
                                      left-y right-y)
                (this . dir) 0 (this . score))]
          [else
           (new waiting-ball% (this . dir)
                (sub1 (this . timer)) (this . score))])))

;;Examples of WaitingBalls:
(define wb1 (new waiting-ball% 'left 100 (make-score 0 0)))
(define wb2 (new waiting-ball% 'right 0 (make-score 2 3)))

;;------------------------------------------------------------------------------
;;World Class

(define-class world%
  (fields left-player right-player ball)
  
  ;; to-draw : -> Image
  ;; renders an image of the game world with the paddles, ball, score, and
  ;; dotted line in the middle
  (define (to-draw)
    (local (;; dotted-line : Image -> Image
            ;; creates a dotted line over the given scn to the y-value 
            ;; up to HEIGHT
            (define (dotted-line scn)
              (local ((define (dotted-helper start end)
                        (cond [(<= start HEIGHT)
                               (add-line (dotted-helper (+ end 10) (+ end 20))
                                         (/ WIDTH 2) start 
                                         (/ WIDTH 2) end "white")]
                              [else scn])))
                (dotted-helper 0 10))))
      (this . ball . draw-on
            (place-image (this . left-player . draw)
                         X-LEFT
                         (this . left-player . y)
                         (place-image (this . right-player . draw)
                                      X-RIGHT
                                      (this . right-player . y)
                                      (dotted-line MT))))))
  
  ;; on-tick : -> World
  ;; moves the ball, bouncing it off paddles and walls,
  ;; as well as handling scoring and reseting the ball
  (check-expect (world1 . on-tick) 
                (new world% play1 play2 
                     (new waiting-ball% 'left 100 (make-score 0 1))))
  (check-expect (world2 . on-tick) 
                (new world% play3 play2 
                     (new ball% 255 250 'right 0 (make-score 1 1))))
  (check-expect (world3 . on-tick) 
                (new world% play3 play3 
                     (new ball% 5 250 'left 0 (make-score 1 1))))
  
  (define (on-tick)
    (new world% (this . left-player)
         (this . right-player)
         (this . ball . next (this . left-player . y)
               (this . right-player . y))))
  
  ;; on-key : KeyEvent -> World
  ;; moves the player's paddles up and down if the appropriate keys are pressed
  (check-expect (world2 . on-key "up") 
                (new world% play3 (new player% 185) 
                     (new ball% 250 250 'right 0 (make-score 1 1))))
  (check-expect (world2 . on-key "down") 
                (new world% play3 (new player% 215) 
                     (new ball% 250 250 'right 0 (make-score 1 1))))
  (check-expect (world2 . on-key "w") 
                (new world% (new player% 485) play2
                     (new ball% 250 250 'right 0 (make-score 1 1))))
  (check-expect (world2 . on-key "s") 
                (new world% (new player% 500) play2
                     (new ball% 250 250 'right 0 (make-score 1 1))))
  
  (define (on-key ke)
    (cond [(string=? ke "up") (new world% (this . left-player)
                                   (this . right-player . up)
                                   (this . ball))]
          [(string=? ke "down") (new world% (this . left-player)
                                     (this . right-player . down)
                                     (this . ball))]
          [(string=? ke "w") (new world% (this . left-player . up)
                                  (this . right-player) (this . ball))]
          [(string=? ke "s") (new world% (this . left-player . down)
                                  (this . right-player) (this . ball))]
          [else this]))
  
  ;; stop-when : -> Boolean
  ;; returns true when a certain score has been reached by a player
  (check-expect (world1 . stop-when) false)
  (check-expect (world3 . stop-when) false)
  (check-expect (world4 . stop-when) true)
  (check-expect (world5 . stop-when) true)
  
  (define (stop-when)
    (or (= WINNING-SCORE (score-left (this . ball . score)))
        (= WINNING-SCORE (score-right (this . ball . score))))))

;;Examples of World:
(define world1 (new world% play1 play2 ball1))
(define world2 (new world% play3 play2 ball2))
(define world3 (new world% play3 play3 ball6))
(define world4 (new world% play3 play3 (new ball% 250 250 'left 0
                                            (make-score WINNING-SCORE 0))))
(define world5 (new world% play3 play3 (new ball% 250 250 'left 0
                                            (make-score 0 WINNING-SCORE))))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Main Function

;; pong : Number -> String
;; starts a game of pong in which the ball starts moving after the given
;; time (in ticks) has progressed. Returns a string with the winner of the game
(define (pong time)
  (local ((define a-player (new player% (/ HEIGHT 2)))
          (define (results world)
            (string-append "The " 
                           (cond
                             [(= WINNING-SCORE 
                                 (score-left (world . ball . score)))"left "]
                             [(= WINNING-SCORE
                                 (score-right (world . ball . score))) "right "]
                             [else "(NA) "]) "player wins! Congrats!")))
    (results (big-bang (new world% a-player a-player
                            (new waiting-ball% 'right 
                                 time (make-score 0 0)))))))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------




