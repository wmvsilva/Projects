; Looks great. 50/50

;;> Boxed World <25/25>
    ;;> Single World <15/15>
    ;;> Double World <10/10>
;;> Quick Lists <25/25>

#|
Sarah Babski, William Silva
Professor Lerner
Fundamentals II (Honors)
29 January 2014

Assignment #4: Nesting Worlds and Quick Lists
|#

#lang class/1

;;------------------------------------------------------------------------------
;;4.3 Quick Lists
;;------------------------------------------------------------------------------
;;Data Definitions

;; A [List T] is one of:
;; -(new cons% T [List T])
;; -(new mt%)
;; interp. a class-based representation of a standard List

;; A [List X] implements
;; - cons : X -> [List X]
;;   Cons given element on to this list.
;; - first : -> X
;;   Get the first element of this list (only defined on non-empty lists).
;; - rest : -> [List X]
;;   Get the rest of this (only defined on non-empty lists).
;; - list-ref : Natural -> X
;;   Get the ith element of this list (only defined for lists of i+1 or more elements).
;; - length : -> Natural
;;   Compute the number of elements in this list.

;; The function (empty) returns a [List X] for any X.

;; A [Tree X] is one of:
;; -(new node% [Tree X] X [Tree X])
;; -(new leaf% X)
;; interp. a class-based representation of binary trees.

;; A [QuickList X] is a (new quick-list% [List [Tree X]])
;; interp. the List of Trees is organized in such a way that:
;; -it consists of increasingly large full binary trees.
;; -with the possible exception of the first two trees, every successive tree
;;  is strictly larger

;; A [QuickList X] implements
;; - cons : X -> [QuickList X]
;;   Cons given element on to this list.
;; - first : -> X
;;   Get the first element of this list (only defined on non-empty lists).
;; - rest : -> [QuickList X]
;;   Get the rest of this (only defined on non-empty lists).
;; - list-ref : Natural -> X
;;   Get the ith element of this list (only defined for lists of i+1 or more elements).
;; - length : -> Natural
;;   Compute the number of elements in this list.

;; The function (quick-empty) returns a [QuickList X] for any X.

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------
;;List Classes

(define-class cons%
  (fields first rest)
  
  ;; cons : X -> [List X]
  ;; cons given element on to this list
  (check-expect (mt-list . cons 1) list1)
  (check-expect (list1 . cons 2) list2)
  (check-expect (list4 . cons 'd)
                (new cons% 'd (new cons% 'a 
                                   (new cons% 'b (new cons% 'c (new mt%))))))
  
  (define (cons t)
    (new cons% t this))
  
  ;; list-ref : Natural -> X
  ;; get the ith element of this list (only defined for lists of i+1 or more 
  ;; elements)
  (check-expect (list1 . list-ref 0) 1)
  (check-expect (list3 . list-ref 1) 2)
  (check-expect (list4 . list-ref 2) 'c)
  
  (define (list-ref n)
    (if (zero? n)
        (this . first)
        (this . rest . list-ref (sub1 n))))
  
  ;; length : -> Natural
  ;; compute the number of elements in this list
  (check-expect (list1 . length) 1)
  (check-expect (list2 . length) 2)
  (check-expect (list3 . length) 3)
  (check-expect (list4 . length) 3)
  
  (define (length)
    (+ 1 (this . rest . length)))
  
  ;; append : [List X] -> [List X]
  ;; appends the given list to this list
  (check-expect (list1 . append mt-list) list1)
  (check-expect (list1 . append list1)
                (new cons% 1 (new cons% 1 (new mt%))))
  (check-expect (list2 . append list3)
                (new cons% 2 (new cons% 1 
                                  (new cons% 3 (new cons% 2 
                                                    (new cons% 1 (new mt%)))))))
  
  (define (append l)
    (new cons% (this . first) (this . rest . append l))))

(define-class mt%
  (fields)
  
  ;; cons : X -> [List X]
  ;; cons given element on to this list
  (check-expect (mt-list . cons 1) list1)
  (check-expect (mt-list . cons 'c)
                (new cons% 'c mt-list))
  (check-expect (mt-list . cons "String")
                (new cons% "String" mt-list))
  
  (define (cons t)
    (new cons% t (new mt%)))
  
  ;; length : -> Natural
  ;; compute the number of elements in this list
  ;; (the length of an empty list is always zero)
  (check-expect (mt-list . length) 0)
  
  (define (length)
    0)
  
  ;; append : [List X] -> [List X]
  ;; appends the given list to this list
  (check-expect (mt-list . append list1) list1)
  (check-expect (mt-list . append list4) list4)
  (check-expect (mt-list . append list3) list3)
  
  (define (append l)
    l))

;; empty : -> [List X]
;; produces the empty list of a [List X]
(define (empty)
  (new mt%))

;;Examples of Lists:
(define mt-list (new mt%))

(define list1 (new cons% 1 mt-list))
(define list2 (new cons% 2 list1))
(define list3 (new cons% 3 list2))
(define list4 (new cons% 'a (new cons% 'b (new cons% 'c mt-list))))
(define ls ((empty) . cons 'a . cons 'b . cons 'c . cons 'd . cons 'e))

;;Extra Tests
(check-expect ((empty) . length) 0)
(check-expect (ls . length) 5)
(check-expect (ls . first) 'e)
(check-expect (ls . rest . first) 'd)
(check-expect (ls . rest . rest . first) 'c)
(check-expect (ls . rest . rest . rest . first) 'b)
(check-expect (ls . rest . rest . rest . rest . first) 'a)

(check-expect (ls . list-ref 0) 'e)
(check-expect (ls . list-ref 1) 'd)
(check-expect (ls . list-ref 2) 'c)
(check-expect (ls . list-ref 3) 'b)
(check-expect (ls . list-ref 4) 'a)

;;------------------------------------------------------------------------------
;;QuickList Class

(define-class quick-list%
  (fields forest)
  
  #|
Examples of QuickLists:
NOTE: Examples are mentioned here for clarity in what the tests represent.
      Examples are actually coded following the class definition (otherwise
      an error will occur).
(define mt-quick (quick-empty))

(define ql1 (new quick-list% (new cons% (new leaf% 0) (new mt%))))
(define ql2 (new quick-list% (new cons% (new leaf% 1) 
                                  (new cons% (new leaf% 0) (new mt%)))))
(define ql3 (new quick-list% (new cons% (new node% (new leaf% 1) 2 
                                             (new leaf% 0)) (new mt%))))
(define ql4 (new quick-list% (new cons% (new leaf% 3) 
                                  (new cons% (new node% (new leaf% 1) 2 
                                                  (new leaf% 0)) (new mt%)))))

(define qls ((quick-empty) . cons 'a . cons 'b . cons 'c . cons 'd . cons 'e))
|#
  
  ;; first-tree : -> [Tree X]
  ;; returns the first Tree in the [List Tree] of this QuickList's forest field
  ;; (only defined on non-empty lists)
  (check-expect (ql1 . first-tree) (new leaf% 0))
  (check-expect (ql2 . first-tree) (new leaf% 1))
  (check-expect (ql3 . first-tree) (new node% (new leaf% 1) 2 (new leaf% 0)))
  (check-expect (qls . first-tree) (new leaf% 'e))
  
  (define (first-tree)
    (this . forest . first))
  
  ;; rest-tree : -> [List [Tree X]]
  ;; returns the forest field of this quick-list with everything but the first
  ;; Tree (only defined on non-empty lists)
  (check-expect (ql1 . rest-tree) (new mt%))
  (check-expect (ql2 . rest-tree) (new cons% (new leaf% 0) (new mt%)))
  (check-expect (ql3 . rest-tree) (new mt%))
  (check-expect (qls . rest-tree) 
                (new cons% (new leaf% 'd) 
                     (new cons% (new node% (new leaf% 'b) 'c 
                                     (new leaf% 'a)) (new mt%))))
  
  (define (rest-tree)
    (this . forest . rest))
  
  ;; num-trees : -> Natural
  ;; computes the number of Trees that this QuickList contains
  (check-expect (mt-quick . length) 0)
  (check-expect (ql1 . length) 1)
  (check-expect (ql2 . length) 2)
  (check-expect (ql3 . length) 3)
  (check-expect (qls . length) 5)
  
  (define (num-trees)
    (this . forest . length))
  
  ;; combine-two : X -> [QuickList X]
  ;; assuming the first two trees are of equal size, combines them to create a
  ;; new tree with the first on the left and the second of the right with the
  ;; given x at the top
  (check-expect (ql2 . combine-two 3)
                (new quick-list% (new cons% (new node% (new leaf% 1) 3 
                                                 (new leaf% 0)) (new mt%))))
  (check-expect (qls . combine-two 7)
                (new quick-list% (new cons% (new node% (new leaf% 'e) 7 
                                                 (new leaf% 'd))
                                      (new cons% (new node% (new leaf% 'b) 'c
                                                      (new leaf% 'a)) 
                                           (new mt%)))))
  
  (define (combine-two x)
    (new quick-list% ((new cons% (new node% (this . first-tree)
                                      x (this . rest-tree . first))
                           (new mt%)) . append 
                                      (this . rest-tree . rest))))
  
  ;; add-leaf : X -> [QuickList X]
  ;; adds a leaf with value equal to the given x to this QuickList
  (check-expect (mt-quick . add-leaf 'a) 
                (new quick-list% (new cons% (new leaf% 'a) (new mt%))))
  (check-expect (ql1 . add-leaf 5) 
                (new quick-list% (new cons% (new leaf% 5) 
                                      (new cons% (new leaf% 0) (new mt%)))))
  
  (define (add-leaf x)
    (new quick-list% ((new mt%) . cons (new leaf% x) . append (this . forest))))
  
  ;; cons : X -> [QuickList X]
  ;; cons given element on to this list using the specified invariant
  (check-expect (mt-quick . cons 'a)
                (new quick-list% (new cons% (new leaf% 'a) (new mt%))))
  (check-expect (ql1 . cons 'a)
                (new quick-list% (new cons% (new leaf% 'a) 
                                      (new cons% (new leaf% 0) (new mt%)))))
  (check-expect (qls . cons 9)
               (new quick-list% (new cons% (new node% (new leaf% 'e) 9 
                                                (new leaf% 'd)) 
                                     (new cons% (new node% (new leaf% 'b) 'c 
                                                     (new leaf% 'a)) 
                                          (new mt%)))))
  
  (define (cons x)
    (local ((define total-trees (this . num-trees)))
      (cond [(<= total-trees 1) 
             (this . add-leaf x)]
            [(>= total-trees 2)
             (if (= (this . first-tree . length) 
                    (this . rest-tree . first . length))
                 (this . combine-two x)
                 (this . add-leaf x))])))
  
  ;; first : -> X
  ;; get the first element of this list (only defined on non-empty lists)
  (check-expect (ql1 . first) 0)
  (check-expect (ql2 . first) 1)
  (check-expect (ql3 . first) 2)
  (check-expect (ql4 . first) 3)
  (check-expect (qls . first) 'e)
  
  (define (first)
    (this . forest . first . val))
  
  ;; rest : -> [QuickList X]
  ;; get the rest of this (only defined on non-empty lists)
  (check-expect (ql1 . rest) (new quick-list% (new mt%)))
  (check-expect (qls . rest)
                (new quick-list% (new cons% (new leaf% 'd) 
                                      (new cons% (new node% (new leaf% 'b) 'c 
                                                      (new leaf% 'a)) 
                                           (new mt%)))))
  
  (define (rest)
    (new quick-list% (this . forest . first . split
                           . append (this . rest-tree))))
  
  ;; list-ref : Natural -> X
  ;; get the ith element of this list (only defined for lists of i+1 or more 
  ;; elements)
  (check-expect (ql1 . list-ref 0) 0)
  (check-expect (ql2 . list-ref 1) 0)
  (check-expect (ql3 . list-ref 1) 1)
  (check-expect (qls . list-ref 4) 'a)
  
  (define (list-ref n)
    (cond [(zero? n) (this . first)]
          [(= (this . num-trees) 1)
           (if (<= n (this . first-tree . left . length))
               ((new quick-list% (new cons% (this . first-tree . left) (new mt%))) . list-ref (sub1 n))
               ((new quick-list% (new cons% (this . first-tree . right) (new mt%)))
                . list-ref (- n 1 (this . first-tree . left . length))))]
          [(>= (this . num-trees) 2)
           (if (<= n (sub1 (this . ql-left-trees . length)))
               (this . ql-left-trees . list-ref n)
               (this . ql-right-trees . list-ref (- n (this . ql-left-trees . length))))]))
  
  ;; length : -> Natural
  ;; compute the number of elements in this list
  (check-expect (mt-quick . length) 0)
  (check-expect (ql3 . length) 3)
  (check-expect (qls . length) 5)
  
  (define (length)
    (cond [(= (this . num-trees) 0) 0]
          [else
           (+ (this . first-tree . length)
              ((new quick-list% (this . rest-tree)) . length))]))
  
  ;; ql-left-trees : -> [QuickList X]
  ;; produces a [QuickList X] identical to this one except with the forest field
  ;; containing only the trees on the left side (if there is an odd number,
  ;; takes the larger amount)
  ;; Assume: the number of Trees in the QuickList is equal to or greater
  ;; than 2
  (check-expect (ql2 . ql-left-trees) 
                (new quick-list% (new cons% (new leaf% 1) (new mt%))))
  (check-expect (ql4 . ql-left-trees)
                (new quick-list% (new cons% (new leaf% 3) (new mt%))))
  (check-expect (qls . ql-left-trees)
                (new quick-list% (new cons% (new leaf% 'e) 
                                      (new cons% (new leaf% 'd) (new mt%)))))
  
  (define (ql-left-trees)
    (local ((define this-many (round (/ (this . num-trees) 2)))
            (define (pick-them l n)
              (cond [(zero? n) (new mt%)]
                    [else (new cons% (l . first) (pick-them (l . rest) (sub1 n)))])))
      (new quick-list% (pick-them (this . forest) this-many))))
  
  ;; ql-left-trees : -> [QuickList X]
  ;; produces a [QuickList X] identical to this one except with the forest field
  ;; containing only the trees on the right side (if there is an odd number,
  ;; takes the smaller amount)
  (check-expect (ql2 . ql-right-trees) 
                (new quick-list% (new cons% (new leaf% 0) (new mt%))))
  (check-expect (ql4 . ql-right-trees)
                (new quick-list% (new cons% (new node% (new leaf% 1) 2 
                                                 (new leaf% 0)) (new mt%))))
  (check-expect (qls . ql-right-trees)
                (new quick-list% (new cons% (new node% (new leaf% 'b) 'c 
                                                 (new leaf% 'a)) (new mt%))))
  
  (define (ql-right-trees)
    (local ((define this-many (round (/ (this . num-trees) 2)))
            (define (pick-them l n)
              (cond [(zero? n) l]
                    [else (pick-them (l . rest) (sub1 n))])))
      (new quick-list% (pick-them (this . forest) this-many)))))


;; quick-empty : -> [QuickList X]
;; produces the empty QuickList containing no Trees
(check-expect (quick-empty) (new quick-list% (new mt%)))

(define (quick-empty)
  (new quick-list% (new mt%)))

;;------------------------------------------------------------------------------
;;Tree Classes

(define-class node%
  (fields left val right)
  
  ;; length : -> Natural
  ;; computes how many elements are in this Tree
  (check-expect (node1 . length) 3)
  (check-expect (node2 . length) 7)
  
  (define (length)
    (+ 1 (this . left . length) (this . right . length)))
  
  ;; split : -> [List [Tree X]]
  ;; splits this Tree into a List containing the right side and then the left
  ;; side. The value is dropped to the floor.
  (check-expect (node1 . split)
                 (new cons% (new leaf% 0) (new cons% (new leaf% 2) (new mt%))))
  (check-expect (node2 . split)
                (new cons% (new node% (new leaf% 0) 3 (new leaf% 2))
                     (new cons% (new node% (new leaf% 0) 3 
                                     (new leaf% 2)) (new mt%))))
  
  (define (split)
    (new cons% (this . left) (new cons% (this . right) (new mt%)))))


(define-class leaf%
  (fields val)
  
  ;; length : -> Natural
  ;; computes how many elements are in this Tree
  ;; (A Leaf always has one element)
  (check-expect (leaf1 . length) 1)
  (check-expect (leaf2 . length) 1)
  
  (define (length)
    1)
  
  ;; split : -> [List X]
  ;; splits this Tree into its left and right components
  ;; (a leaf has no left or right so it simply dissolves into an empty list)
  (check-expect (leaf1 . split) mt-list)
  (check-expect (leaf2 . split) mt-list)
  
  (define (split)
    (new mt%)))

;; Examples of Trees:
(define leaf0 (new leaf% 0))
(define leaf1 (new leaf% 1))
(define leaf2 (new leaf% 2))
(define leaf3 (new leaf% 3))

(define node1 (new node% leaf0 3 leaf2))
(define node2 (new node% node1 4 node1))

;;------------------------------------------------------------------------------
;;Examples of QuickLists and additional tests:
(define mt-quick (quick-empty))

(define ql1 (new quick-list% (new cons% (new leaf% 0) (new mt%))))
(define ql2 (new quick-list% (new cons% (new leaf% 1) 
                                  (new cons% (new leaf% 0) (new mt%)))))
(define ql3 (new quick-list% (new cons% (new node% (new leaf% 1) 2 
                                             (new leaf% 0)) (new mt%))))
(define ql4 (new quick-list% (new cons% (new leaf% 3) 
                                  (new cons% (new node% (new leaf% 1) 2 
                                                  (new leaf% 0)) (new mt%)))))

(define qls ((quick-empty) . cons 'a . cons 'b . cons 'c . cons 'd . cons 'e))

(check-expect ((quick-empty) . length) 0)
(check-expect (qls . length) 5)
(check-expect (qls . first) 'e)
(check-expect (qls . rest . first) 'd)
(check-expect (qls . rest . rest . first) 'c)
(check-expect (qls . rest . rest . rest . first) 'b)
(check-expect (qls . rest . rest . rest . rest . first) 'a)

(check-expect (qls . list-ref 0) 'e)
(check-expect (qls . list-ref 1) 'd)
(check-expect (qls . list-ref 2) 'c)
(check-expect (qls . list-ref 3) 'b)
(check-expect (qls . list-ref 4) 'a)

;;------------------------------------------------------------------------------
;;------------------------------------------------------------------------------















