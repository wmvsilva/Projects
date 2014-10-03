#|
Sarah Babski, William Silva
Professor Lerner
Fundamentals II (Honors)
7 February 2014

Assignment #5: A Universe of Pong
|#

#lang class/1
(require 2htdp/image)
(require class/universe)

;;------------------------------------------------------------------------------
;;Problem 1: Networked Pong (Client+Server)
#|
An interactive online game of pong in which two players connect to a server
and play against each other. The first player to connect controls the paddle on
the left while the second player controls the right paddle. Any players to
connect after this are immediately disconnected by the server. The first player
to the defined winning score (default 7) is the winner. The programs of both
players stop upon one player reaching this score. The server checks to make sure
that players are not moving the ball where they are not supposed to and to make
sure player's paddles are not moving more than they are supposed to. In
addition, the server sends sync messages to players in pre-defined intervals to
ensure the ball is synced up on both screens.

Controls (Left Player)-
-Move paddle up: w
-Move paddle down: s

Controls (Right Player)-
-Move paddle up: up arrow
-Move paddle down: down arrow
|#
;;------------------------------------------------------------------------------
;;Data Definitions

(define WIDTH 500) ;;pixels
(define HEIGHT 500) ;;pixels
(define MT (empty-scene WIDTH HEIGHT "black"))
;; Image representing the game background
(define DELTA 15) ;;pixels
;; how much the player can move vertically
(define X-LEFT 10) ;; pixels
;; left player's horizontal coordinate
(define X-RIGHT 490)
;; right player's horizontal coordinate
(define PLAY-HEIGHT 40) ;;pixels
;; the height of the player sprite
(define PLAY-WIDTH 10) ;;pixels
;; the width of the player sprite
(define BALL-X-SPEED 5) ;;pixels/tick
;; the ball's constant horizontal speed
(define BALL-DY-CHANGE 5) ;;pixels/tick
;; the amount that a ball's vertical speed can change when bounced off a paddle
(define WINNING-SCORE 7)
;; interp. score that a player needs to get to win the game
(define SYNC-INT 101)
;; interp. Ball messages sent to server before syncing occurs
(define IP-ADDRESS LOCALHOST)
;; interp. the IP that the clients attempt to connect to

;; A InitialServer is a (new init-server%)
;; interp. the initial server. When a player connects, it turns into a OneServer
;; which has a field for a single player. Otherwise, it does nothing.

;; A OneServer is a (new one-server% IWorld)
;; interp. the second stage server containing the first player who connected.
;; When another player connects, this turns into a full server and tells players
;; to start

;; A Server is a (new server% IWorld IWorld Number)
;; interp. the running server which has the left player as the first field and
;; the right player as its second field. This server checks to make sure players
;; are not cheating. If a player is cheating, a message is send out telling
;; the players that someone is cheating and the game ends
;; when a player moves, that message is sent to the other player
;; The third field is the 'timer' field which goes down on each message and 
;; determines when to send messages to players to sync their balls together.

;; A Direction is one of:
;; -'left
;; -'right
;; interp. a chosen side of the playing field.

;; A Score is a (list Number Number)
;; interp. the first item in the list is the score of the left player while
;; the second item is the score of the right player.

;; A BallType is one of:
;; -'move
;; -'wait
;; interp. used to identify whether a ball is a Ball or a WaitingBall

;; A Message is one of:
;; -(list start Direction)
;; interp. a message for WaitingPlayers telling them to start their games.
;; -(list Direction 'ball BallType 
;;     (list Number Number Direction Number Number Score)
;;      (list Number Number Direction Number Number Score Number Number))
;; interp. a message containing information about the movement of a ball.
;; it contains the side the message came from, the type of the Ball, and lists
;; (for the starting and ending states of the ball after a tick) that contain
;; the ball's x-coord, y-coord, direction it is going in, vertical speed, ticks
;; it will take to turn into a standard Ball, and Score. The list for the ending
;; state also contains the left and right paddle y-coordinates used in the
;; creation of the ending state.
;; -(list 'sync 'ball BallType 
;;     (list Number Number Direction Number Number Score)
;;      (list Number Number Direction Number Number Score Number Number))
;; interp. this is the same as the Message above except with 'sync as the first
;; item. It tells the player that they should replace their ball with one
;; identical to the provided data
;; -(list Direction 'paddle 'move (list Number) (list Number KeyEvent))
;; interp. a message containing information about the movement of a paddle.
;; it contains the side of the paddle that moved, and two lists (for the
;; starting and ending states of the paddle) that each contain the y-coordinates
;; of the paddles. The second list also contains the key-event which caused
;; this movement as its second item.
;; -(list cheat)
;; interp. a message sent to players when one of the users is detected cheating
;; -(list leave)
;; interp. a message sent to players when one of the users leaves early.

;;Example of a Message:
(define msg1 '(left ball wait (0 0 left 0 70 (0 0)) 
                    (0 0 left 0 69 (0 0) 250 250)))
(define msg2 '(right paddle move (160) (175 down)))
(define msg3 '(right ball move (296 250 right 0 0 (0 0)) 
                     (301 250 right 0 0 (0 0) 250 250)))
(define msg4 '(left paddle move (160) (175 down)))

;; A Player is a (new player% Number)
;; interp. a representation of a user's paddle with the field being the y-coord
;; of the paddle.

;; A WaitingBall is a (new waiting-ball% Number Number Direction
;;                     Number Number Score)
;; interp. essentially a respawning ball. It does not move and is not shown.
;; once its timer goes to zero, it creates a normal Ball.

;; A Ball is a (new ball% Number Number Direction Number Number Score)
;; interp. a standard pong ball that goes back and forth. It bounces off the top
;; and bottom as well as paddles. When it goes off-screen, it turns into a
;; WaitingBall and points are awarded to the appropriate Player.

;; An IBall can do:
;; draw : -> Image
;; draw-on : Image -> Image
;; next : -> IBall
;; msg->ball : Message -> IBall

;; A Ball implements IBall.

;; A WaitingPlayer is a (new waiting-player%)
;; interp. a player waiting to start the game. Once the server sends a message
;; to start (along with what side the player will be), this will become a 
;; PlayerWorld and the game will begin

;; A PlayerWorld is one of:
;; -(new left-world% Player Player Ball)
;; -(new right-world% Player Player Ball)
;; interp. depending on what the server tells the WaitingPlayer it will be,
;; the player will exist as a left-world% (in which they control the left
;; paddle) or a right-world% (in which they control the right paddle)

;; A World is a (new world% Player Player Ball)
;; interp. a base class for the PlayerWorlds

;; An ErrorClient is a (new bad-world% Message)
;; interp. a stopped client that is created when a bad message of some sort
;; is sent to the player. This may be the result of a player cheating, a player
;; leaving, or some sort of unrecognized message. A short statement on the
;; problems is displayed to the user.

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Classes pertaining to Servers
;;------------------------------------------------------------------------------
;;InitialServer Class

(define-class init-server%
  (fields)
  
  ;; on-new : IWorld -> OneServer
  ;; when a user connects, starts a OneServer containing that user's IWorld.
  (check-expect (init1 . on-new iworld1) (new one-server% iworld1))
  (check-expect (init1 . on-new iworld2) (new one-server% iworld2))
  
  (define (on-new iw)
    (new one-server% iw)))

;; Examples of InitialServer:
(define init1 (new init-server%))

;;------------------------------------------------------------------------------
;;OneServer Class

(define-class one-server%
  (fields left)
  
  ;; on-new : IWorld -> Server
  ;; when a user connects, starts a complete server with the IWorld in the 
  ;; 'left' field as the left player and the given IWorld as the right player.
  (check-expect (1-server1 . on-new iworld2)
                (make-bundle (new server% iworld1 iworld2 SYNC-INT)
                             (list (make-mail iworld1
                                              (list 'start 'left))
                                   (make-mail iworld2
                                              (list 'start 'right)))
                             '()))
  
  (define (on-new iw)
    (make-bundle (new server% (this . left) iw SYNC-INT)
                 (list (make-mail (this . left)
                                  (list 'start 'left))
                       (make-mail iw
                                  (list 'start 'right)))
                 '()))
  
  ;; on-disconnect : IWorld -> InitialServer
  ;; if a player disconnects, changes this server into an intial server with no
  ;; IWorlds in it.
  (check-expect (1-server1 . on-disconnect iworld1) init1)
  (check-expect (1-server2 . on-disconnect iworld2) init1)
  
  (define (on-disconnect iw)
    (new init-server%)))

;;Examples of OneServer:
(define 1-server1 (new one-server% iworld1))
(define 1-server2 (new one-server% iworld2))

;;------------------------------------------------------------------------------
;;Server Class

(define-class server%
  (fields left right timer)
  
  ;; on-new : IWorld -> Server
  ;; when a user attempts to connect to this server, this server remains
  ;; unchanged and that user is disconnected.
  (check-expect (server1 . on-new iworld3)
                (make-bundle server1 '() (list iworld3)))
  (check-expect (server2 . on-new iworld1)
                (make-bundle server2 '() (list iworld1)))
  
  (define (on-new iw)
    (make-bundle this '() (list iw)))
  
  ;; on-disconnect : IWorld -> InitialServer
  ;; if a player disconnects, changes this server into an intial server with no
  ;; IWorlds in it and sends a message to the world that did not leave saying
  ;; that someone left and disconnects them.
  (check-expect (server1 . on-disconnect iworld1)
                (make-bundle (new init-server%) 
                             (list (make-mail iworld2 (list 'leave))) 
                             (list iworld2)))
  (check-expect (server2 . on-disconnect iworld2)
                (make-bundle (new init-server%) 
                             (list (make-mail iworld3 (list 'leave))) 
                             (list iworld3)))
  
  (define (on-disconnect iw)
    (if (iworld=? (this . left) iw)
        (make-bundle (new init-server%) 
                     (list (make-mail (this . right) (list 'leave))) 
                     (list (this . right)))
        (make-bundle (new init-server%) 
                     (list (make-mail (this . left) (list 'leave))) 
                     (list (this . left)))))
  
  ;; subtract-timer : -> Server
  ;; creates a Server identical to this one but with the 'timer' field decreased
  ;; by one
  (check-expect (server1 . subtract-timer) (new server% iworld1 iworld2 49))
  (check-expect (server2 . subtract-timer) (new server% iworld2 iworld3 19))
  
  (define (subtract-timer)
    (new server% (this . left) (this . right) (sub1 (this . timer))))
  
  ;; cheat-detected : Message -> Bundle
  ;; sends a message to players saying that someone cheated and changed this
  ;; server back into an InitialServer. Also disconnects all players.
  (check-expect (server1 . cheat-detected msg1)
                (make-bundle init1
                 (list (make-mail iworld1 (list 'cheat msg1))
                       (make-mail iworld2 (list 'cheat msg1)))
                 (list iworld1 iworld2)))
  
  (define (cheat-detected msg)
    (make-bundle (new init-server%)
                 (list (make-mail (this . left) (list 'cheat msg))
                       (make-mail (this . right) (list 'cheat msg)))
                 (list (this . left) (this . right))))
  
  ;; on-msg : IWorld Message -> Bundle
  ;; determines if players are cheating when they send ball or paddle updates
  ;; if there is a paddle update, sends paddle message to the enemy.
  ;; Cheating is determined for paddles by whether they moved within the 
  ;; program-set amount. Cheating for balls is detected by running the Ball's
  ;; program using it's pre-tick state and seeing if it matches the post-tick
  ;; state as stated by the message.
  ;; Every time there is a moving ball message, the timer goes down by one.
  ;; Once it is zero or less, sync messages are sent to the clients and the
  ;; timer for this server is reset.
  (check-expect (server1 . on-msg iworld2 msg2)
                (make-bundle (new server% iworld1 iworld2 50)
                             (list (make-mail iworld1 msg2)) '()))
  (check-expect (server1 . on-msg iworld2 msg1) 
                (new server% iworld1 iworld2 49))
  (check-expect (server2 . on-msg iworld2 msg1)
                (new server% iworld2 iworld3 19))
  (check-expect (server2 . on-msg iworld3 msg2)
                (make-bundle (new server% iworld2 iworld3 20)
                             (list (make-mail iworld2 msg2)) '()))
  
  (define (on-msg iw msg)
    (cond [(symbol=? (second msg) 'ball)
           (local ((define props (fourth msg))
                   (define props2 (fifth msg))
                   (define old-b (if (symbol=? (third msg) 'wait)
                                     (new waiting-ball% (first props) 
                                          (second props) (third props) 
                                          (fourth props) (fifth props) 
                                          (sixth props))
                                     (new ball% (first props) (second props)
                                          (third props) (fourth props) 
                                          (fifth props) (sixth props))))
                   (define new-b (old-b . next (seventh props2) 
                                        (eighth props2)))
                   (define actual-list (list (new-b . x) (new-b . y)
                                             (new-b . dir)  (new-b . dy)
                                             (new-b . timer) (new-b . score)))
                   (define supposed-list (list (first props2) (second props2)
                                               (third props2) (fourth props2) 
                                               (fifth props2) (sixth props2))))
             (if  (equal? actual-list supposed-list)
                  (if (and (<= (this . timer) 0) (symbol=? (third msg) 'move))
                      (make-bundle (new server% (this . left) 
                                        (this . right) SYNC-INT)
                                   (list (make-mail (this . left) 
                                                    (append '(sync) (rest msg)))
                                         (make-mail (this . right) 
                                                    (append '(sync) 
                                                            (rest msg))))
                                   '())
                      (this . subtract-timer))
                  (this . cheat-detected msg)))]
          [(symbol=? (second msg) 'paddle)
           (local ((define old-coord (first (fourth msg)))
                   (define new-coord (first (fifth msg))))
             (if  (<= (abs (- new-coord old-coord)) DELTA)
                  (make-bundle this
                               (if (symbol=? (first msg) 'left)
                                   (list (make-mail (this . right) msg))
                                   (list (make-mail (this . left) msg)))
                               '())
                  (this . cheat-detected msg)))])))

;;Examples of Server:
(define server1 (new server% iworld1 iworld2 50))
(define server2 (new server% iworld2 iworld3 20))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Classes pertaining to pong logic and management
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
        (new player% (+ (this . y) DELTA))))
  
  ;; cheating-check : -> Message
  ;; creates a message with the paddles data before and after key movement
  (check-expect (play1 . cheating-check 'left 15 "s")
                (list 'left 'paddle 'move (list 0) (list 15 "s")))
  (check-expect (play2 . cheating-check 'right 185 "up")
                (list 'right 'paddle 'move (list 200) (list 185 "up")))
  
  (define (cheating-check side new-y ke)
    (list side 'paddle 'move
          (list (this . y))
          (list new-y ke))))

;;Examples of Players:
(define play1 (new player% 0))
(define play2 (new player% 200))
(define play3 (new player% HEIGHT))

;;------------------------------------------------------------------------------
;;Ball Class

(define-class ball%
  (fields x y dir dy timer score)
  
  ;; draw : -> Image
  ;; creates an image representation of the ball
  (define (draw)
    (circle 5 "solid" "white"))
  
  ;; draw-on : Image -> Image
  ;; draws this ball (and scores for each player) onto a given scn
  (define (draw-on scn)
    (place-image (this . draw)
                 (this . x)
                 (this . y)
                 (place-image (text (number->string 
                                     (first (this . score))) 36 "White")
                              100
                              50
                              (place-image 
                               (text (number->string 
                                      (second (this . score))) 36 "White")
                               400
                               50
                               scn))))
  
  ;; next : -> IBall
  ;; moves the ball, bounces the ball off walls and paddles, and changed the
  ;; ball into a waiting ball (with score added) if it touches either of the
  ;; side walls
  (check-expect (ball1 . next 0 0) 
                (new waiting-ball% 0 0 'left 0 100 '(0 1)))
  (check-expect (ball2 . next 0 0) 
                (new ball% 255 250 'right 0 0 '(1 1)))
  (check-expect (ball3 . next 77 HEIGHT)  
                (new ball% 255 255 'right 5 0 '(1 1)))
  
  (define (next left-y right-y)
    (this . move . bounce left-y right-y . score-ball))
  
  ;; move : -> Ball
  ;; moves the ball horizontally by a set amount and vertically by the dy field
  (check-expect (ball2 . move) (new ball% 255 250 'right 0 0 '(1 1)))
  (check-expect (ball3 . move)  (new ball% 255 255 'right 5 0 '(1 1)))
  (check-expect (ball4 . move) (new ball% 95 120 'left 20 0 '(1 1)))
  
  (define (move)
    (new ball% (+ (this . x) (if (symbol=? 'left (this . dir))
                                 (* -1 BALL-X-SPEED)
                                 BALL-X-SPEED))
         (+ (this . y) (this . dy))
         (this . dir) (this . dy) (this . timer) (this . score)))
  
  ;; bounce : -> Ball
  ;; bounces the ball off walls and paddles
  (check-expect (ball5 . bounce 0 0) (new ball% 250 1 'left 5 0 '(1 1)))
  (check-expect (ball6 . bounce 250 250) 
                (new ball% 11 250 'right 0 0 '(1 1)))
  
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
                (new ball% new-x ball-y new-dir new-dy 
                     (ball . timer) (ball . score)))))
      (cond [(<= (this . y) 0)
             (new ball% (this . x) 1 (this . dir) 
                  (* -1 (this . dy)) (this . timer) (this . score))]
            [(>= (this . y) HEIGHT)
             (new ball% (this . x) (sub1 HEIGHT) (this . dir) 
                  (* -1 (this . dy)) (this . timer) (this . score))]
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
  (check-expect ((new ball% -10 250 'left 0 0 '(1 1)) . score-ball)
                (new waiting-ball% 0 0 'left 0 100 '(1 2)))
  
  (define (score-ball)
    (cond [(<= (this . x) 0)
           (new waiting-ball% 0 0 'left 0 100
                (list (first (this . score))
                      (add1 (second (this . score)))))]
          [(>= (this . x) WIDTH)
           (new waiting-ball% 0 0 'right 0 100
                (list (add1 (first (this . score)))
                      (second (this . score))))]
          [else this]))
  
  ;; msg->ball : Message -> Ball
  ;; constructs a new Ball with the attributes of the pre-tick ball data in the
  ;; given message. ASSUME: The message is a 'ball' Message.
  (check-expect (ball1 . msg->ball msg1)
                (new ball% 0 0 'left 0 70 '(0 0)))
  (check-expect (ball2 . msg->ball msg1)
                (new ball% 0 0 'left 0 70 '(0 0)))
  (check-expect (ball3 . msg->ball msg3)
                (new ball% 296 250 'right 0 0 '(0 0)))
  
  (define (msg->ball msg)
    (local ((define props (fourth msg)))
      (new ball% (first props) (second props)
           (third props) (fourth props) (fifth props) (sixth props))))
      
  ;; cheating-check : -> Message
  ;; creates a message with the balls data before and after tick calculation
  (check-expect (ball2 . cheating-check 'left 250 250)
                '(left ball move (250 250 right 0 0 (1 1)) 
                       (255 250 right 0 0 (1 1) 250 250)))
  (check-expect (ball4 . cheating-check 'right 30 300)
                '(right ball move (100 100 left 20 0 (1 1)) 
                       (95 120 left 20 0 (1 1) 30 300)))
  
  (define (cheating-check side left-y right-y)
    (local ((define new-b (this . next left-y right-y)))
      (list side 'ball 'move
            (list (this . x) (this . y) (this . dir) (this . dy)
                  (this . timer) (this . score))
            (list (new-b . x) (new-b . y) (new-b . dir) 
                  (new-b . dy) (new-b . timer) (new-b . score)
                  left-y right-y)))))

;;Examples of Balls:
(define ball1 (new ball% 0 0 'left 0 0 '(0 0)))
(define ball2 (new ball% 250 250 'right 0 0 '(1 1)))
(define ball3 (new ball% 250 250 'right 5 0 '(1 1)))
(define ball4 (new ball% 100 100 'left 20 0 '(1 1)))
(define ball5 (new ball% 250 0 'left -5 0 '(1 1)))
(define ball6 (new ball% 10 250 'left 0 0 '(1 1)))

;;------------------------------------------------------------------------------
;;WaitingBall Class

(define-class waiting-ball%
  (fields x y dir dy timer score)
  
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
    (place-image (text (number->string (first (this . score)))
                       36 "White")
                 100
                 50
                 (place-image (text (number->string 
                                     (second (this . score)))
                                    36 "White")
                              400
                              50
                              scn)))
  
  ;; next : Number Number -> IBall
  ;; the timer of this WaitingBall goes down with each run of next, if it has
  ;; reached zero, it creates a new Ball. Otherwise, this WaitingBall is
  ;; returned
  (check-expect (wb1 . next 250 250) 
                (new waiting-ball% 0 0 'left 0 99 '(0 0)))
  (check-expect (wb2 . next 250 250) 
                (new ball% 250 250 'right 0 0 '(2 3)))
  (check-expect (wb2 . next 100 100) 
                (new ball% 250 100 'right 0 0 '(2 3)))
  
  (define (next left-y right-y)
    (cond [(zero? (this . timer))
           (new ball% (/ WIDTH 2) (if (symbol=? (this . dir) 'left) 
                                      left-y right-y)
                (this . dir) 0 0 (this . score))]
          [else
           (new waiting-ball% (this . x) (this . y) (this . dir) (this . dy)
                (sub1 (this . timer)) (this . score))]))
  
   ;; msg->ball : Message -> Ball
  ;; constructs a new Ball with the attributes of the pre-tick ball data in the
  ;; given message. ASSUME: The message is a 'ball' Message.
  (check-expect (wb1 . msg->ball msg1)
                (new waiting-ball% 0 0 'left 0 70 '(0 0)))
  (check-expect (wb2 . msg->ball msg3)
                (new waiting-ball% 296 250 'right 0 0 '(0 0)))
  
  (define (msg->ball msg)
    (local ((define props (fourth msg)))
      (new waiting-ball% (first props) (second props)
           (third props) (fourth props) (fifth props) (sixth props))))
  
  ;; cheating-check : -> Message
  ;; creates a message with the balls data before and after tick calculation
  (check-expect (wb1 . cheating-check 'left 250 250)
                '(left ball wait (0 0 left 0 100 (0 0)) 
                       (0 0 left 0 99 (0 0) 250 250)))
  (check-expect (wb2 . cheating-check 'right 30 300)
                '(right ball wait (0 0 right 0 0 (2 3)) 
                        (250 300 right 0 0 (2 3) 30 300)))
  
  (define (cheating-check side left-y right-y)
    (local ((define new-b (this . next left-y right-y)))
      (list side 'ball 'wait
            (list (this . x) (this . y) (this . dir) (this . dy)
                  (this . timer) (this . score))
            (list (new-b . x) (new-b . y) (new-b . dir) (new-b . dy)
                  (new-b . timer) (new-b . score)
                  left-y right-y)))))

;;Examples of WaitingBalls:
(define wb1 (new waiting-ball% 0 0 'left 0 100 '(0 0)))
(define wb2 (new waiting-ball% 0 0 'right 0 0 '(2 3)))

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
  
  ;; on-tick : PlayerWorld -> PlayerWorld
  ;; progresses the game by moving the ball, bouncing the ball, changing the
  ;; score, and reseting the ball
  ;; also sends messages to the server to ensure that this player is not
  ;; cheating in any way
  (check-expect (world1 . game-move 'left)
                (make-package 
                 (new left-world% (new player% 0) (new player% 200) 
                      (new waiting-ball% 0 0 'left 0 100 '(0 1))) 
                 '(left ball move (0 0 left 0 0 (0 0)) 
                        (0 0 left 0 100 (0 1) 0 200))))
  (check-expect (world2 . game-move 'left)
                (make-package
                 (new left-world% (new player% 500) (new player% 200) 
                      (new ball% 255 250 'right 0 0 '(1 1))) 
                 '(left ball move (250 250 right 0 0 (1 1)) 
                        (255 250 right 0 0 (1 1) 500 200))))
  (check-expect (world1 . game-move 'right)
                (make-package
                 (new right-world% (new player% 0) (new player% 200) 
                      (new waiting-ball% 0 0 'left 0 100 '(0 1))) 
                 '(right ball move (0 0 left 0 0 (0 0)) 
                         (0 0 left 0 100 (0 1) 0 200))))
  (check-expect (world2 . game-move 'right)
                (make-package (new right-world% (new player% 500) 
                                   (new player% 200)
                                   (new ball% 255 250 'right 0 0 '(1 1))) 
                              '(right ball move (250 250 right 0 0 (1 1)) 
                                      (255 250 right 0 0 (1 1) 500 200))))
  
  (define (game-move side)
    (make-package (if (symbol=? side 'left)
     (new left-world% (this . left-player) (this . right-player)
                       (this . ball . next (this . left-player . y)
                             (this . right-player . y)))
     (new right-world% (this . left-player) (this . right-player)
                       (this . ball . next (this . left-player . y)
                             (this . right-player . y))))
                  (this . ball . cheating-check side (this . left-player . y)
                        (this . right-player . y))))
  
  ;; on-key : KeyEvent -> World
  ;; moves the player's paddles up and down if the appropriate keys are pressed
  (check-expect (world2 . key-press "up") 
                (new world% play3 (new player% 185) 
                     (new ball% 250 250 'right 0 0 '(1 1))))
  (check-expect (world2 . key-press "down") 
                (new world% play3 (new player% 215) 
                     (new ball% 250 250 'right 0 0 '(1 1))))
  (check-expect (world2 . key-press "w") 
                (new world% (new player% 485) play2
                     (new ball% 250 250 'right 0 0 '(1 1))))
  (check-expect (world2 . key-press "s") 
                (new world% (new player% 500) play2
                     (new ball% 250 250 'right 0 0 '(1 1))))
  
  (define (key-press ke)
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
    (or (= WINNING-SCORE (first (this . ball . score)))
        (= WINNING-SCORE (second (this . ball . score))))))

(define world1 (new world% play1 play2 ball1))
(define world2 (new world% play3 play2 ball2))
(define world3 (new world% play3 play3 ball6))
(define world4 (new world% play3 play3 (new ball% 250 250 'left 0
                                            0 (list WINNING-SCORE 0))))
(define world5 (new world% play3 play3 (new ball% 250 250 'left 0
                                            0 (list 0 WINNING-SCORE))))


;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Classes pertaining to clients
;;------------------------------------------------------------------------------
;;WaitingPlayer Class

(define-class waiting-player%
  (fields)
  
  ;; register : -> IP
  ;; the IP address of the server that this player connects to
  (define (register) IP-ADDRESS)
  
  ;; to-draw : -> Image
  ;; creates an image representation of this server which is just text telling
  ;; the user that the client is either connecting or waiting for another player
  ;; to join.
  (define (to-draw)
    (place-image (text "CONNECTING/WAITING FOR OTHER PLAYER TO JOIN..." 16 'red)
                 (/ WIDTH 2)
                 (/ HEIGHT 2)
                 MT))
  
  ;; on-receive : Msg -> PlayerWorld
  ;; when given the go-ahead by the server, changes this into a left or right
  ;; world depending on the message
  (check-expect (wp1 . on-receive '(start left))
                (new left-world% (new player% 250) 
                     (new player% 250) 
                     (new waiting-ball% 0 0 'left 0 100 '(0 0))))
  (check-expect (wp1 . on-receive '(start right))
                (new right-world% (new player% 250) 
                     (new player% 250) 
                     (new waiting-ball% 0 0 'left 0 100 '(0 0))))
  
  (define (on-receive msg)
    (if (symbol=? (first msg) 'start)
        (cond [(symbol=? (second msg) 'left)
               (new left-world% (new player% (/ HEIGHT 2)) 
                    (new player% (/ HEIGHT 2))
                    (new waiting-ball% 0 0 'left 0 100 '(0 0)))]
              [(symbol=? (second msg) 'right)
               (new right-world% (new player% (/ HEIGHT 2)) 
                    (new player% (/ HEIGHT 2))
                    (new waiting-ball% 0 0 'left 0 100 '(0 0)))])
        (new bad-world% msg))))

(define wp1 (new waiting-player%))

;;------------------------------------------------------------------------------
;;PlayerWorld Class (left-world% version)

(define-class left-world%
  (super world%)
  (fields)
  
  ;; on-tick : PlayerWorld -> PlayerWorld
  ;; progresses the game by moving the ball, bouncing the ball, changing the
  ;; score, and reseting the ball
  ;; also sends messages to the server to ensure that this player is not
  ;; cheating in any way
  (check-expect (lworld1 . on-tick)
                (make-package 
                 (new left-world% (new player% 0) (new player% 200) 
                      (new waiting-ball% 0 0 'left 0 100 '(0 1))) 
                 '(left ball move (0 0 left 0 0 (0 0)) 
                        (0 0 left 0 100 (0 1) 0 200))))
  (check-expect (lworld2 . on-tick)
                (make-package
                 (new left-world% (new player% 500) (new player% 200) 
                      (new ball% 255 250 'right 0 0 '(1 1))) 
                 '(left ball move (250 250 right 0 0 (1 1)) 
                        (255 250 right 0 0 (1 1) 500 200))))
  
  (define (on-tick)
    (this . game-move 'left))
  
  ;; on-key : PlayerWorld -> PlayerWorld
  ;; when "w" is "s" is pressed, tells the base class world to move the left
  ;; paddle up or down. Also sends a message to the server so it can tell the
  ;; other client and check if cheating is occuring
  ;; NOTE: Tests for this item failed for some reason even though the fail
  ;; message says that actual value X differs from Y when X is identical to Y
  
  (define (on-key ke)
    (if (or (string=? ke "w") (string=? ke "s"))
        (make-package (new left-world% (this . key-press ke . left-player)
                           (this . right-player)
                           (this . ball))
                      (this . left-player . cheating-check 'left
                            (this . key-press ke . left-player . y) ke))
        this))
  
  ;; on-receive : PlayerWorld Sexpr -> PlayerWorld
  ;; receives messages about enemy movement and cheating
  (check-expect (lworld1 . on-receive '(right paddle move (160) (175 "up")))
                (new left-world% (new player% 0) 
                     (new player% 185) 
                     (new ball% 0 0 'left 0 0 '(0 0))))
  (check-expect (lworld1 . on-receive '(right paddle move (160) (175 "down")))
                (new left-world% (new player% 0) 
                     (new player% 215)
                     (new ball% 0 0 'left 0 0 '(0 0))))
  (check-expect (lworld1 . on-receive '(cheat)) (new bad-world% '(cheat)))
  (check-expect (lworld1 . on-receive '(badmessage)) 
                (new bad-world% '(badmessage)))
  
  (define (on-receive msg)
    (cond [(symbol=? (first msg) 'right)
           (cond [(symbol=? (second msg) 'paddle)
                  (local ((define world 
                            (this . key-press (second (fifth msg)))))
                    (new left-world% (world . left-player) 
                         (world . right-player)
                         (world . ball)))]
                 [else (new bad-world% msg)])]
          [(symbol=? (first msg) 'sync)
           (new left-world% (this . left-player) (this . right-player)
                (this . ball . msg->ball msg))]
          [else (new bad-world% msg)])))

(define lworld1 (new left-world% play1 play2 ball1))
(define lworld2 (new left-world% play3 play2 ball2))

;;------------------------------------------------------------------------------
;;PlayerWorld Class (right-world% version)

(define-class right-world%
  (super world%)
  (fields)
  
  ;; on-tick : PlayerWorld -> PlayerWorld
  ;; progresses the game by moving the ball, bouncing the ball, changing the
  ;; score, and reseting the ball
  ;; also sends messages to the server to ensure that this player is not
  ;; cheating in any way
  (check-expect (rworld1 . on-tick)
                (make-package 
                 (new right-world% (new player% 0) (new player% 200) 
                      (new waiting-ball% 0 0 'left 0 100 '(0 1))) 
                 '(right ball move (0 0 left 0 0 (0 0)) 
                         (0 0 left 0 100 (0 1) 0 200))))
  (check-expect (rworld2 . on-tick)
                (make-package
                 (new right-world% (new player% 500) (new player% 200) 
                      (new ball% 255 250 'right 0 0 '(1 1))) 
                 '(right ball move (250 250 right 0 0 (1 1)) 
                        (255 250 right 0 0 (1 1) 500 200))))
  
  (define (on-tick)
    (this . game-move 'right))
  
  ;; on-key : PlayerWorld -> PlayerWorld
  ;; when "up" or "down" is pressed, tells the base class world to move the 
  ;; right paddle up or down. Also sends a message to the server so it can tell
  ;; the other client and check if cheating is occuring
  ;; NOTE: Tests for this item failed for some reason even though the fail
  ;; message says that actual value X differs from Y when X is identical to Y
  
  (define (on-key ke)
    (if (or (string=? ke "up") (string=? ke "down"))
        (make-package (new right-world% (this . left-player)
                           (this . key-press ke . right-player)
                           (this . ball))
                      (this . right-player . cheating-check 'right
                            (this . key-press ke . right-player . y) ke))
        this))
  
  ;; on-receive : PlayerWorld Sexpr -> PlayerWorld
  ;; receives messages about enemy movement and cheating
  (check-expect (rworld1 . on-receive '(left paddle move (160) (175 "up")))
                (new right-world% (new player% 0) (new player% 185) 
                     (new ball% 0 0 'left 0 0 '(0 0))))
  (check-expect (rworld1 . on-receive '(left paddle move (160) (175 "down")))
                 (new right-world% (new player% 0) (new player% 215) 
                      (new ball% 0 0 'left 0 0 '(0 0))))
  (check-expect (rworld1 . on-receive '(cheat)) (new bad-world% '(cheat)))
  (check-expect (rworld1 . on-receive '(badmessage)) 
                (new bad-world% '(badmessage)))
  
  (define (on-receive msg)
    (cond [(symbol=? (first msg) 'left)
           (cond [(symbol=? (second msg) 'paddle)
                  (local ((define world 
                            (this . key-press (second (fifth msg)))))
                    (new right-world% (world . left-player) 
                         (world . right-player)
                         (world . ball)))]
                 [else (new bad-world% msg)])]
          [(symbol=? (first msg) 'sync)
           (new right-world% (this . left-player) (this . right-player)
                (this . ball . msg->ball msg))]
          [else (new bad-world% msg)])))

(define rworld1 (new right-world% play1 play2 ball1))
(define rworld2 (new right-world% play3 play2 ball2))

;;------------------------------------------------------------------------------
;;ErrorClient Class

(define-class bad-world%
  (fields msg)
  
  ;; to-draw : -> Image
  ;; creates an image which consists of text telling the player that the game
  ;; has ended due to an error obtained by receiving an unrecognized message.
  (define (to-draw)
    (local ((define text-to-draw
              (cond [(symbol=? (first (this . msg)) 'cheat)
                     "Someone cheated."]
                    [(symbol=? (first (this . msg)) 'leave)
                     "Someone left the game."]
                    [else
                     "Bad message received."])))
    (place-image (text (string-append "END GAME. " text-to-draw) 12 'red)
                 (/ WIDTH 2)
                 (/ HEIGHT 2)
                 MT)))
  
  ;; stop-when : -> Boolean
  ;; since this returns true, this client will stop immediately
  (define (stop-when)
    true))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Main Functions
;;------------------------------------------------------------------------------

;; server : -> InitialServer
;; creates the initial server that clients can connect to so they may start a
;; game of Pong
(define (server)
  (universe (new init-server%)))

;; client : -> WaitingPlayer
;; creates the client that connects to a server so that a user running this may
;; play a game of Pong with another user running the client.
(define (client)
  (big-bang (new waiting-player%)))

;; pong :
;; creates a game of Pong with a server and two clients on a single computer.
;; This is used for testing without the need for a second computer.
(define (pong)
(launch-many-worlds (server)
                      (client)
                      (client)))
;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
