;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname HHDesign_Final) (read-case-sensitive #t) (teachpacks ((lib "docs.rkt" "teachpack" "htdp"))) (htdp-settings #(#t constructor repeating-decimal #f #t none #f ((lib "docs.rkt" "teachpack" "htdp")))))
;;Chris Panella, William Silva
;;Prof Ahmed
;;CS2500 (HON)
;;4 December 2013

;;Problem Set 12
;;------------------------------------------------------------------------------
#|
HUNGRY HENRY MULTIPLAYER GAME
An interactive game in which the server program takes in the number of players 
who are going to connect and waits to start the game until that many people have
connected. If more try to connect, they are kicked from the server. A specified
number of "cupcakes" are placed on the scene along with representations of all 
connected players using the world program. A player may set waypoints for their
sprite by clicking the appropriate location on the scene with the mouse. When a
player gets close enough to a cupcake, it eats the cupcake. The goal is to eat 
the most cupcakes and when the game finishes, a string containing who won is 
produced.
|#
;;------------------------------------------------------------------------------
(require 2htdp/image)
(require 2htdp/universe)

;;Graphical Constants
(define NUM-CAKES 1)
(define WIDTH 500)
(define HEIGHT 500)
(define SPEED 2)
(define MT (empty-scene WIDTH HEIGHT))
(define CAKE (circle 6 'solid 'red))
(define WAYPOINT (square 20 'outline 'black))
(define PLAYER-SPRITE (square 20 'solid 'gray))

(define debug? false)

;;Data Definitions

;; A Posn.v2 is a (list Number Number)
;; interp. xy-coordinates with the first number being the x-value and the second
;; being the y-value

;; GameProgress is one of:
;; -'waiting
;; -'go
;; interp. states to determine whether the game is waiting to start or 
;; in progress

;; A Cake is a Posn.v2
;; interp. shows where a cake is on the scene

;; A Waypoint is a Posn.v2
;; interp. shows where a waypoint (future player location) is

;; A Player is a (list IWorld Posn.v2 [List-of Waypoint] Number)
;; interp. the Player's IWorld, current position, list of waypoints to visit
;; (visiting them left to right), and number of cakes eaten (score)

;; Examples of Players:
(define p1 (list iworld1 '(0 0) empty 0))
(define p2 (list iworld2 '(0 0) '((1 1)) 2))

;; A ClientPlayer is a (list String Posn.v2 [List-of Waypoint] Number)
;; interp. the Player's IWorld name, position, list of future positions,
;; and number of cakes eaten

;; A UniverseState is a:
;; (list Number GameProgress [List-of Player] [List-of Cake])
;; interp. in order that they are in the list, the elements represent:
;; the number of players needed to start the game, the current state of the
;; game (waiting to start or in progress), a list of all the players
;; currently participating in the game, and a list containing all cake
;; locations

;;Examples of UniverseStates:
(define us1 (list 1 'waiting empty '(0 0)))
(define us2 (list 1 'go (list p1) '((50 50))))
(define us3 (list 1 'go (list p1) empty))
(define us4 (list 1 'go (list p2) empty))

;; A WorldState is a:
;; (list Number GameProgress [List-of ClientPlayer] [List-of Cake])
;; interp. a WorldState is the same as a UniverseState with the exception that
;; the Players are ClientPlayers instead and have a slightly different
;; representation as described above

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;                                          
;                                          
;                                          
;                             ;;        ;; 
;                              ;         ; 
;                              ;         ; 
;  ;;;   ;;  ;;;;   ;; ;;;     ;     ;;; ; 
;   ;     ; ;    ;   ;;        ;    ;   ;; 
;   ;  ;  ; ;    ;   ;         ;    ;    ; 
;   ; ; ; ; ;    ;   ;         ;    ;    ; 
;   ; ; ; ; ;    ;   ;         ;    ;   ;; 
;    ;   ;   ;;;;   ;;;;;   ;;;;;    ;;; ;;
;                                          
;                                          
;                                          
;         
;;------------------------------------------------------------------------------

;; String[Name] String[IP] -> String
;; creates a world with name n that communicates with server at ip-address
;; and produces a String containing the winning players when completed
(define (make-world n ip-address)
  (local (;; render : WorldState -> WorldState
          ;; produces an image of all players and waypoints as well as the
          ;; world's own waypoints
          (define (render ws)
            (cond [(symbol=? (second ws) 'waiting)
                   (overlay (text "WAITING FOR PLAYERS TO JOIN" 12 "black") MT)]
                  [(symbol=? (second ws) 'go) 
                   (draw-waypoints (third (find-player n (third ws)))
                         (draw-players (third ws)
                                (draw-cakes (fourth ws) MT)))])))
    (winning-players (big-bang (list 0 'waiting empty empty)
                               [to-draw render]
                               [on-receive make-msg-world]
                               [on-mouse mouse-handler] 
                               [name n]
                               [register ip-address]))))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Main Functions

;; winning-players : WorldState -> String
;; makes a string "The winning player(s) are:" preceding the winning players
;; of the given ws that consumed the most cake
(check-expect (winning-players 
               (list 1 'go (list (list "iworld2" '(0 0) '((1 1)) 2)) empty))
              "The winning player(s) are: iworld2")
(check-expect (winning-players 
               (list 1 'go (list (list "iworld1" '(0 0) '((1 1)) 5)
                                 (list "iworld2" '(0 0) 
                                       '((1 1)) 2)) empty))
              "The winning player(s) are: iworld1")

(define (winning-players ws)
  (local ((define max-eaten (top-eaten (third ws)))
          (define winners 
            (foldl (lambda (x y) (string-append y " " (first x))) "" 
                   (filter (lambda (p) (= (fourth p) max-eaten)) (third ws)))))
    (string-append "The winning player(s) are:" winners)))

;; make-msg-world : WorldState WorldState -> WorldState
;; outputs the given msg
(check-expect (make-msg-world (list 1 'waiting empty empty) 
                              (list 1 'waiting empty '(0 0)))
              (list 1 'waiting empty '(0 0)))

(define (make-msg-world ws msg)
  msg)

;; mouse-handler : WorldState Number Number String -> Package
;; when the mouse event is "button-down", creates a package that has the current
;; ws and sends a message to the server with the coordinates of the new wp to 
;; add to the end of the player's waypoint list
(check-expect (mouse-handler (list 1 'waiting empty empty) 0 0 "button-down")
              (list 1 'waiting empty empty))
(check-expect (mouse-handler (list 1 'go empty empty) 0 0 "enter")
              (list 1 'go empty empty))
(check-expect (mouse-handler (list 1 'go 
                                   (list (list iworld1 '(0 0) empty 0)) empty)
                             0 0 "button-down")
              (make-package (list 1 'go (list (list iworld1 '(0 0) empty 0))
                                  empty) (list 0 0)))

(define (mouse-handler ws me-x me-y mouse-event)
  (cond [(and (symbol=? (second ws) 'go) (string=? mouse-event "button-down"))
         (make-package ws (list me-x me-y))]
        [else ws]))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Helper Functions

;;render

;; draw-waypoints : [List-of Posn.v2] Image -> Image
;; draws waypoints at all coordinates in the given lowp onto a base image base
(check-expect (draw-waypoints '((0 0)) MT) (place-image WAYPOINT 0 0 MT))
(check-expect (draw-waypoints '((0 0) (7 7)) MT) 
              (place-image WAYPOINT 0 0
                           (place-image WAYPOINT 7 7 MT)))

(define (draw-waypoints lowp base)
  (foldr (lambda (x y) (place-image WAYPOINT (first x) (second x) y))
         base  lowp))

;; find-player : String [List-of ClientPlayer] -> ClientPlayer
;; extracts the player whose name is the given name
;; ASSUME: Not empty and the player with given name exists in the list
(check-expect (find-player "iworld1" (list (list "iworld1" '(1 1) empty 0)))
              (list "iworld1" '(1 1) empty 0))
(check-expect (find-player "iworld2" (list (list "iworld1" '(1 1) empty 0) 
                                           (list "iworld2" '(2 2) empty 2)))
              (list "iworld2" '(2 2) empty 2))

(define (find-player name lop)
  (if (string=? name (first (first lop)))
      (first lop)
      (find-player name (rest lop))))

;; draw-players : [List-of ClientPlayer] Image -> Image
;; renders all players onto a base image at their specified coordinates
;; as PLAYER-SPRITE with their names in small font overlayed on the sprite
(check-expect (draw-players (list (list "iworld1" '(0 0) empty 0)) MT)
              (place-image (overlay (text "iworld1" 8 "black") PLAYER-SPRITE) 
                           0 0 MT))

(define (draw-players lop base)
  (local (;; create-spirte : ClientPlayer -> Image
          ;; produces an image representation of the player with the player's
          ;; name overlayed on the graphical constant sprite
          (define (create-sprite player)
            (overlay (text (first player) 8 "black") PLAYER-SPRITE)))
    (foldr (lambda (x y) (place-image (create-sprite x) 
                                      (first (second x)) (second (second x)) y))
           base lop)))

;; draw-cakes : [List-of Posn.v2] Image -> Image
;; draws cakes at all coordinates in the given cake-list onto a base image base
(check-expect (draw-cakes '((0 0)) MT)
              (place-image CAKE 0 0 MT))
(check-expect (draw-cakes '((0 0) (5 5)) MT)
              (place-image CAKE 0 0 (place-image CAKE 5 5 MT)))

(define (draw-cakes cake-list base)
  (foldr (lambda (x y) (place-image CAKE (first x) (second x) y))
         base cake-list))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;                                                                  
;                                                                
;                                                                
;                                                                
;                      ;                                         
;                                                                
;   ;;  ;;  ;; ;;    ;;;   ;;;  ;;;  ;;;;   ;; ;;;   ;;;;    ;;;; 
;    ;   ;   ;;  ;     ;    ;    ;  ;    ;   ;;     ;       ;    ; 
;    ;   ;   ;   ;     ;     ;  ;   ;;;;;;   ;       ;;;;   ;;;;;; 
;    ;   ;   ;   ;     ;     ;  ;   ;        ;              ;      
;    ;  ;;   ;   ;     ;      ;;    ;        ;      ;       ;      
;     ;; ;  ;;; ;;  ;;;;;     ;;     ;;;;;  ;;;;;   ;;;;;    ;;;;; 
;                                                                  
;                                                                  
;                                                                  
;       
;;------------------------------------------------------------------------------

;; make-universe : Nat -> UniverseState 
;; create a universe hub that accepts n number of players before starting the
;; HungryHenry game and then restarts when the game is complete or if every 
;; player leaves the game
(define (make-universe n)
  (universe (create-initial n)
            [state debug?]
            [on-tick game-move]
            [on-new add-world]
            [on-msg add-waypoint]
            [on-disconnect kick-player]))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Main Functions

;; create-initial : Nat -> UniverseState
;; produces the initial waiting state of the universe (which will allow n
;; players to connect before starting). In addition, this initial universe
;; has a specified number of cakes in random positions.
(check-expect (first (create-initial 1)) 1)
(check-expect (length (fourth (create-initial 1))) NUM-CAKES)

(define (create-initial n)
  (list n 'waiting empty (random-list-posn.v2 NUM-CAKES)))

;; game-move : UniverseState -> UBundle
;; -if game has not started, returns the UniverseState with no mail
;; -if game is in progress, makes the players get closer to their respective
;; waypoints, changing their waypoint the one next in the list if they have
;; reached it. The player will eat cake if they are on it. The Universe will 
;; mail out WorldStates containing all game information
;; -if all cakes eaten, kicks all players out and makes produces a 'waiting
;; UniverseState
(check-expect (game-move us1) (make-bundle us1 '() '()))
(check-expect (game-move us2) 
              (make-bundle us2 
                           (list (make-mail 
                                  iworld1 
                                  (list 1 'go 
                                        (list (list "iworld1" '(0 0) empty 0))
                                        '((50 50))))) '()))
(check-expect (game-move us3) 
              (make-bundle (list 1 'waiting (list p1) empty) '() 
                           (list iworld1)))

(define (game-move us)
  (cond [(symbol=? (second us) 'waiting) (make-bundle us '() '())]
        [(empty? (fourth us)) (end-game us)]
        [else (mail-all-players (eat-cake (move-all-players us)))]))

;; add-world : UniverseState IWorld -> UBundle
;; when a new world joins, a Player representing them is created in a random
;; position with their IWorld and is added to the current UniverseState.
;; -if enough players have joined, the game starts. 
;; -if too many players have joined, the new player is kicked.
(check-expect (add-world us2 iworld2) (make-bundle us2 '() (list iworld2)))
;;NOTE: Part of code cannot be tested due to the random creation of a player and
;;the inability to extract anything from bundle

(define (add-world us iw)
  (local ((define new-players 
            (cons (list iw (random-posn.v2 WIDTH HEIGHT) empty 0) (third us))))
    (cond [(< (length new-players) (first us))
           (make-bundle (list (first us) 
                              'waiting new-players (fourth us)) '() '())]
          [(= (length new-players) (first us))
           (make-bundle (list (first us) 'go new-players (fourth us)) '() '())]
          [(> (length new-players) (first us))
           (make-bundle us '() (list iw))])))

;; add-waypoint : UniverseState IWorld Waypoint -> UBundle
;; adds the given waypoint in the message to the IWorld's player in the given us
(check-expect (add-waypoint us4 iworld3 (list 5 5))
              (make-bundle us4 '() '()))
(check-expect (add-waypoint us2 iworld1 (list 1 1)) 
              (make-bundle (list 1 'go 
                                 (list (list iworld1 '(0 0) '((1 1)) 0))
                                 '((50 50))) '() '()))
(check-expect (add-waypoint us4 iworld2 (list 5 5))
              (make-bundle (list 1 'go (list 
                                        (list iworld2 '(0 0) '((1 1) (5 5)) 2))
                                 empty) '() '()))

(define (add-waypoint us iw wp)
  (local (;; add-waypoint-lop : [List-of Player] -> [List-of Player]
          ;; finds the player in the list with the iw specified in the top
          ;; function and adds wp to the back of that player's list of waypoints
          (define (add-waypoint-lop lop)
            (cond [(empty? lop) lop]
                  [else (local ((define cp (first lop)))
                          (if (iworld=? iw (first cp))
                              (cons (add-waypoint-player cp wp) (rest lop))
                              (cons cp (add-waypoint-lop (rest lop)))))])))
    (make-bundle (list (first us) (second us)
                       (add-waypoint-lop (third us)) (fourth us)) '() '())))

;; kick-player : UniverseState IWorld -> UBundle
(check-expect (kick-player us4 iworld2) 
              (make-bundle (list 1 'go empty empty) '() '()))
(check-expect (kick-player us4 iworld1) (make-bundle us4 '() '()))

(define (kick-player us iw)
  (local ((define (good-player? iw player)
            (not (iworld=? (first player) iw))))
    (make-bundle (list (first us) (second us)
                       (filter (lambda (x) (good-player? iw x)) 
                               (third us)) (fourth us)) '() '())))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;Helper Functions

;;create-initial

;; random-list-posn.v2 : Number -> [List-of Posn.v2]
;; creates list of Posn.v2 with values within the WIDTH and HEIGHT of the scene
(check-expect (length (random-list-posn.v2 4)) 4)
(check-within (first (first (random-list-posn.v2 1))) (/ WIDTH 2) (/ WIDTH 2))
(check-within (second (first (random-list-posn.v2 1))) 
              (/ HEIGHT 2) (/ HEIGHT 2))

(define (random-list-posn.v2 n)
  (build-list n (lambda (x) (list (random WIDTH) (random HEIGHT)))))

;;------------------------------------------------------------------------------
;;game-move

;; end-game : UniverseState -> UBundle
;; creates a UniverseState with 'finished as the GameProgress and kicks all
;; worlds off the server
(check-expect (end-game us4) 
              (make-bundle (list 1 'waiting (list p2) empty) 
                           '() (list iworld2)))

(define (end-game us)
  (local ((define max-eaten (top-eaten (third us))))
    (make-bundle (create-us-w/gameprogress us 'waiting)
                 '()
                 (extract-iworlds (third us)))))

;; top-eaten : [List-of Player] -> Number
;; determines the top number of cakes eaten by any player
(check-expect (top-eaten empty) 0)
(check-expect (top-eaten (list p1 p2)) 2)

(define (top-eaten lop)
  (foldr (lambda (x y) (max (fourth x) y)) 0 lop))

;; take-out-iworlds : UniverseState -> WorldState
;; creates a universestate identical to the given one except iworlds have
;; been replaced with the corresponding iworld name. Converts the "us" so it can
;; be sent in a Mail
(check-expect (take-out-iworlds us1) us1)
(check-expect (take-out-iworlds us4) 
              (list 1 'go (list (list "iworld2" '(0 0) '((1 1)) 2)) empty))

(define (take-out-iworlds us)
  (local ((define (replace-iworld p)
            (list (iworld-name (first p)) (second p) (third p) (fourth p))))
    (create-us-w/players us (map (lambda (x) (replace-iworld x)) (third us)))))

;; mail-all-players : UniverseState -> UBundle
;; sends mail containing the current state of the universe to all players
;; while keeping the universe state the same
(check-expect (mail-all-players us1) (make-bundle us1 '() '()))
(check-expect (mail-all-players us3) 
              (make-bundle us3 
                           (list (make-mail iworld1 
                                            (list 1 'go 
                                                  (list (list "iworld1" '(0 0)
                                                              empty 0))
                                                  empty))) '()))

(define (mail-all-players us)
  (local ((define players (third us)))
    (make-bundle us 
                 (map (lambda (p) (make-mail (first p) (take-out-iworlds us)))
                      players) '())))

;; eat-cake : UniverseState -> UniverseState
;; makes the players in the given us eat the cake they are on, 
;; adding to their "eaten" count and removing the cake
;; NOTE: If two players are on a cake at the same time, they will both get
;; a point added to their eaten score.
(check-expect (eat-cake us3) us3)
(check-expect (eat-cake (list 1 'go (list p1) '((0 0))))
              (list 1 'go (list (list iworld1 '(0 0) empty 1)) empty))

(define (eat-cake us)
  (list (first us) (second us) 
        (list-add-eaten (third us) (fourth us)) 
        (remove-cake (third us) (fourth us))))

;; list-add-eaten : [List-of Player] [List-of Posn.v2] -> [List-of Player]
;; adds 1 to every player's "eaten" count if they are currently on a cake
(check-expect (list-add-eaten (list p2) '((50 50))) (list p2))
(check-expect (list-add-eaten (list p2) '((0 0))) 
              (list (list iworld2 '(0 0) '((1 1)) 3)))

(define (list-add-eaten loplay locake)
  (map (lambda (p) (player-add-eaten p locake)) loplay))

;; player-add-eaten : Player [List-of Posn.v2] -> Player
;; adds 1 to a player's eaten count for every cake they are on
(check-expect (player-add-eaten p1 empty) p1)
(check-expect (player-add-eaten p1 '((500 500))) p1)
(check-expect (player-add-eaten p1 '((0 0))) (list iworld1 '(0 0) empty 1))

(define (player-add-eaten player loc)
  (local (;; player-eat-cake : Player Posn -> Player
          ;; determines how much to add to the eaten field depending on whether
          ;; the Player is on the given cake Posn or not
          (define (player-eat-cake player cake)
            (if (eat-cake? player cake)
                1 0)))
    (make-player-w/eaten player 
                         (foldr (lambda (x y) (+ (player-eat-cake player x) y))
                                (fourth player) loc))))

;; remove-cake : [List-of Player] [List-of Posn.v2] -> [List-of Posn.v2]
;; produces a list of cake posn that is similar to the given cake-list
;; except all cakes that have players on them are removed
(check-expect (remove-cake (list p1) empty) empty)
(check-expect (remove-cake (list p1) '((500 500))) '((500 500)))
(check-expect (remove-cake (list p1) '((0 0))) empty)

(define (remove-cake lop cake-list)
  (local (;; no-player-eat? : Posn -> Boolean
          ;; is the given cake not eaten by any player?
          (define (no-player-eat? cake)
            (andmap (lambda (x) (not (eat-cake? x cake)))
                    lop)))
    (filter no-player-eat? cake-list)))

;; eat-cake? : Player Posn.v2 -> Boolean
;; is the player sprite over the cake posn?
(check-expect (eat-cake? p2 '(500 500)) false)
(check-expect (eat-cake? p2 '(0 0)) true)

(define (eat-cake? player cake)
  (and (<= (x-dist (second player) cake)
           (* 0.5 (image-width PLAYER-SPRITE)))
       (<= (y-dist (second player) cake)
           (* 0.5 (image-height PLAYER-SPRITE)))))

;; move-all-players : UniverseState -> UniverseState
;; makes the players get closer to their respective waypoints, changing their
;; waypoint the one next in the list if they have reached it
(check-expect (move-all-players us1) us1)
(check-expect (move-all-players us4) 
              (list 1 'go (list (list iworld2 '(1 1) empty 2)) empty))
(check-expect (move-all-players 
               (list 1 'go (list (list iworld1 '(0 0) '((0 100)) 0)) empty))
              (list 1 'go (list (list iworld1 `(0 ,SPEED) '((0 100)) 0)) empty))
(check-expect (move-all-players 
               (list 1 'go (list (list iworld1 '(0 0) '((100 0)) 0)) empty))
              (list 1 'go (list (list iworld1 `(,SPEED 0) '((100 0)) 0)) empty))

(define (move-all-players us)
  (create-us-w/players us (map move/snap-player (third us))))

;; move/snap-player : Player -> Player
;; moves or snaps a player to the first waypoint in the list depending 
;; on how close the player is to the waypoint. If the player snaps, removes the
;; current waypoint from the list Does nothing if the waypoint list is empty
(check-expect (move/snap-player (list iworld3 '(5 5) empty 0)) 
              (list iworld3 '(5 5) empty 0))
(check-expect (move/snap-player p2) (list iworld2 '(1 1) empty 2))
(check-expect (move/snap-player (list iworld3 '(0 0) '((0 100)) 0)) 
              (list iworld3 `(0 ,SPEED) '((0 100)) 0))

(define (move/snap-player player)
  (cond [(empty? (third player)) player]
        [else (if (snap-player? player)
                  (snap-player player)
                  (move-player player))]))

;; move-player : Player -> Player
;; moves a player at a set SPEED towards the closest waypoint in its structure
(check-expect (move/snap-player (list iworld3 '(0 0) '((0 100)) 0)) 
              (list iworld3 `(0 ,SPEED) '((0 100)) 0))
(check-expect (move/snap-player (list iworld3 '(0 0) '((100 0)) 0)) 
              (list iworld3 `(,SPEED 0) '((100 0)) 0))

(define (move-player player)
  (local ((define wp (first (third player)))
          (define p (second player))
          (define d (distance wp p))
          (define d-SPEED (- d SPEED))
          (define wp.x (first wp))
          (define wp.y (second wp)))
    (list (first player)
          (list (+ wp.x (* (/ (- (first p) wp.x) d) d-SPEED))
                (+ wp.y (* (/ (- (second p) wp.y) d) d-SPEED)))
          (third player)
          (fourth player))))

;; snap-player : Player -> Player
;; snaps the given player to the first waypoint in its list
;; and removes that waypoint from the list
;; ASSUME: "targets" field is not empty
(check-expect (snap-player p2) (list iworld2 '(1 1) empty 2))
(check-expect (snap-player (list iworld3 '(0 0) '((3 3)) 0)) 
              (list iworld3 '(3 3) empty 0))

(define (snap-player player)
  (list (first player)
        (first (third player))
        (rest (third player))
        (fourth player)))

;; snap-player? : Player -> Boolean
;; will moving the player at SPEED towards the current waypoint result in
;; overshooting or equaling the waypoint?
(check-expect (snap-player? p2) true)
(check-expect (snap-player? (list iworld2 '(0 0) '((50 50)) 2)) false)

(define (snap-player? player)
  (<= (distance (second player) (first (third player))) SPEED))

;;------------------------------------------------------------------------------
;;add-waypoint

;; add-waypoint-player : Player Posn.v2 -> Player
;; adds the given wp to the end of the given player's waypoint list
(check-expect (add-waypoint-player p1 '(1 1))
              (list iworld1 '(0 0) '((1 1)) 0))
(check-expect (add-waypoint-player p2 '(1 1))
              (list iworld2 '(0 0) '((1 1) (1 1)) 2))

(define (add-waypoint-player player wp)
  (create-player-w/waypoint player (append (third player) (list wp))))

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;General Utility Functions

;; distance : Posn.v2 Posn.v2 -> Number
;; produces the distance between the two given coordinates
(check-expect (distance '(0 0) '(0 0)) 0)
(check-expect (distance '(0 0) '(0 4)) 4)
(check-expect (distance '(0 0) '(3 0)) 3)
(check-expect (distance '(0 0) '(3 4)) 5)

(define (distance p1 p2)
  (sqrt (+ (sqr (- (first p1) (first p2)))
           (sqr (- (second p1) (second p2))))))

;; random-posn.v2 : Number Number -> Posn.v2
;; creates a Posn.v2 with random x-coordinate within given x and
;; random y-coordinate within given y
(check-within (first (random-posn.v2 10 10)) 5 5)
(check-within (second (random-posn.v2 WIDTH HEIGHT)) 
              (* 0.5 HEIGHT) (* 0.5 HEIGHT))

(define (random-posn.v2 x y)
  (list (random x) (random y)))

;; create-us-w/gameprogress : UniverseState GameProgress -> UniverseState
;; creates a universestate identical to the given us except the GameProgress is
;; changed to the given gp
(check-expect (create-us-w/gameprogress us1 'go) (list 1 'go empty '(0 0)))
(check-expect (create-us-w/gameprogress us4 'waiting) 
              (list 1 'waiting (list p2) empty))

(define (create-us-w/gameprogress us gp)
  (list (first us) gp (third us) (fourth us)))

;; create-us-w/players : UniverseState [List-of Player] -> UniverseState
;; creates a universestate identical to the given us except the players list is
;; changed to the given lop
(check-expect (create-us-w/players us1 empty) us1)
(check-expect (create-us-w/players us1 (list p1)) 
              (list 1 'waiting (list p1) '(0 0)))

(define (create-us-w/players us lop)
  (list (first us) (second us) lop (fourth us)))

;; make-player-w/eaten : Player Number -> Player
;; makes a player identical to the given player except n replaces the previous
;; number in the player's eaten field
(check-expect (make-player-w/eaten p1 0) p1)
(check-expect (make-player-w/eaten p1 1) (list iworld1 '(0 0) empty 1))

(define (make-player-w/eaten player n)
  (list (first player) (second player) (third player) n))

;; make-player-w/waypoint : Player [List-of Posn.v2] -> Player
;; makes a player identical to the given player except lowp replaces the 
;; previous list of waypoints in the player's waypoint field
(check-expect (create-player-w/waypoint p1 empty) p1)
(check-expect (create-player-w/waypoint p1 '((1 1))) 
              (list iworld1 '(0 0) '((1 1)) 0))

(define (create-player-w/waypoint player lowp)
  (list (first player) (second player) lowp (fourth player)))

;; extract-iworlds : [List-of Player] -> [List-of IWorld]
;; creates a list of all the players' IWorlds
(check-expect (extract-iworlds (list p1 p2)) (list iworld1 iworld2))

(define (extract-iworlds lop)
  (map (lambda (p) (first p)) lop))

;; x-dist : Posn.v2 Posn.v2 -> Number
;; determines the absolute horizontal distance between 
;; two coordinates
(check-expect (x-dist '(3 7) '(4 3)) 1)
(check-expect (x-dist '(0 7) '(9 3)) 9)

(define (x-dist p1 p2)
  (abs (- (first p1) (first p2))))

;; y-dist : Posn.v2 Posn.v2 -> Number
;; determines the absolute vertical distance between 
;; two coordinates
(check-expect (y-dist '(3 7) '(4 3)) 4)
(check-expect (y-dist '(0 0) '(9 3)) 3)

(define (y-dist p1 p2)
  (abs (- (second p1) (second p2))))


;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------



