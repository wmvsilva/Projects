// Really nice job, guys! Very clean code, it works, it's tested, and it reads easy.

// 60/60

/* 
Sadruddin Saleem, William Silva
Professor Lerner
Fundamentals II (Honors)
17 March 2014

Assignment #9: Cyclic and Mutable Structures
*/

import tester.*;

//-----------------------------------------------------------------------------
//Problem 8.2 Mutable Deques
//Run Settings- 
//Main Class: tester.Main
//Arguments: Examples9_2
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//IMutableList and ISameTest Interfaces

// Represents a mutable, direct-access list
interface IMutableList<T> {
	
	// Get the (zero-based) nth element from the fron of the list
	// Throw an error if the index is out of bounds
	T get(int n);
	
	// Set the (zero-based) nth element of the list to the new value
	// and return the old value at that index. Throw an error if the
	// index is out of bounds
	T set(int n, T newT);
	
	// Insert a new element at the given (zero-based) index. Throw
	// an error if the index is less than zero or greater than length()
	void insert(int n, T t);
	
	// Remove an element at the given (zero-based) index and return it.
	// Throw an error if the index is out of bounds
	T remove(int n);
	
	// Add given element on to the front of this list.
	void cons(T t);
	
	// Add given element on to the end of this list
	void snoc(T t);
	
	// Computer the number of elements in this list
	int length();
	
	// Reverses the list
	void reverse();
	
	// Computes whether this list is the same as that one,
	// using the provided IComparator to compare elements
	boolean sameList(IMutableList<T> that, ISameTest<T> areSame);
	
}

// Represents a function which takes in two Ts to see if they are equal
interface ISameTest<T> {
	
	// Returns true if first is the same as second
	boolean check(T first, T second);
	
}

//-----------------------------------------------------------------------------
//IMutableList Classes

// Represents a wrapper class of a cyclic list.
class Deque<T> implements IMutableList<T> {
	
	Sentinel<T> header;
	
	Deque() {
		this.header = new Sentinel<T>();
	}
	
	public T get(int n) {
		return this.header.moveAhead().visit(new GetV<T>(n));
	}
	
	//EFFECT: Changes the nth element in the list to newElem
	//Changes the data field of the nth node to newElem
	public T set(int n, T newElem) {
		return this.header.moveAhead().visit(new SetV<T>(n, newElem));
	}
	
	//EFFECT: Adds a node to the front of this list containing the given elem
	//The sentinel leads to the new node and the previous first node's prev field
	//is changed to the new node
	public void cons(T elem) {
		
		this.header.next = new Node<T>(elem, this.header.next, this.header);
		this.header.moveAhead().moveAhead().changePrevTo(this.header.next);	
	}
	
	//EFFECT: Adds a node to the back of this list containing the given elem
	//The sentinel now backtracks to this new node while the previous backtrack
	//node now backtracks to this new node.
	public void snoc(T elem) {
		
		this.header.prev = new Node<T>(elem, this.header, this.header.prev);
		this.header.moveBack().moveBack().changeNextTo(this.header.prev);
	}
	
	//EFFECT: Inserts a new node at the nth position in this list
	//The node before this spot now leads to this new node while the
	//node that used to be at this spot now backtracks to this new node
	public void insert(int n, T elem) {
		this.header.moveAhead().visit(new InsertV<T>(n, elem));
	}
	
	//EFFECT: Removes the node at the nth position in this list
	//The node before the removed node now leads to the node ahead of the
	//removed node. The node ahead of the removed node now backtracks to
	// the node before the removed node.
	public T remove(int n) {
		return this.header.moveAhead().visit(new RemoveV<T>(n));
	}
	
	public int length() {
		return this.header.moveAhead().visit(new LengthV<T>());
	}
	
	//EFFECT: Reverses the next and prev fields of all nodes in this list
	public void reverse() {
		
		this.header.moveAhead().visit(new ReverseV<T>());
	}
	
	public boolean sameList(IMutableList<T> that, ISameTest<T> areSame){
		int thisLength = this.length();
		boolean sameLength = thisLength == that.length();
		
		if (sameLength && (this.length() == 0)) {
			return true;
		}else{
		if (sameLength) {
			return this.sameListHelper(that,areSame, (this.length() - 1));
		}else{
			return false;
			}
		}
	}
	
	// Given two non-empty lists of the same length, computes whether the lists
	// are the same starting from nth element going downward
	boolean sameListHelper(IMutableList<T> that, ISameTest<T> areSame, int n){
		if (n == 0){
			return areSame.check(this.get(n), that.get(n));
		}else{
			return areSame.check(this.get(n), that.get(n)) &&
					this.sameListHelper(that, areSame, (n - 1));
		}	
	}
	
	// Resets this MutableList to a blank slate
	//EFFECT: Changes the header field to an empty representation
	void reset() {
		this.header = new Sentinel<T>();
	}
}

// Represents an individual node in a cyclic list
class Node<T> {
	T data;
	Node<T> next;
	Node<T> prev;
	
	Node(T d, Node<T> n, Node<T> p) {
		this.data = d;
		this.next = n;
		this.prev = p;
	}
	
	//NOTE: This constructor is only needed for the sentinel subclass
	Node() {
		this.data = null;
		this.next = null;
		this.prev = null;
	}
	
	// returns the node in front of this node
	public Node<T> moveAhead() {
		return this.next;
	}
	
	// returns the node behind this node
	public Node<T> moveBack() {
		return this.prev;
	}
	
	// changes the node in front of this node to the given node
	//EFFECT: The next field of this node becomes the given 'that'
	public void changeNextTo(Node<T> that) {
		this.next = that;
	}
	
	// changes the node behind this node to the given node
	//EFFECT: The prev field of this node becomes the given 'that'
	public void changePrevTo(Node<T> that) {
		this.prev = that;
	}
	
	// sends this node to the given visitor which will perform some operation
	// on it
	public <U> U visit(NodeVisitor<T,U> visitor) {
		return visitor.visitNode(this);
	}

}

// Represents a special node that contains no data and marks the end of
// the list
class Sentinel<T> extends Node<T> {
	
	Sentinel() {
		this.data = null;
		this.next = this;
		this.prev = this;
	}
	
	// sends this sentinel to the given visitor which will perform some
	// operation on it
	public <U> U visit(NodeVisitor<T,U> visitor) {
		return visitor.visitSentinel(this);
	}
	
}

//-----------------------------------------------------------------------------
//NodeVisitor Interface and Classes

interface NodeVisitor<T, U> {
	
	// performs some operation on a given node (non-sentinel)
	U visitNode(Node<T> n);
	
	// performs some operation on a given sentinel
	U visitSentinel(Sentinel<T> s);
}

// represents a visitor which will go down Nodes to reach the nth node and
// return its data
class GetV<T> implements NodeVisitor<T, T> {
	int n;
	
	GetV(int n) {
		this.n = n;
	}
	
	// returns the data of the nth node by going through the given node
	// (until it reaches a sentinel)
	//EFFECT: If n is not zero, n is subtracted by 1 and the visitor is
	// run again on the next node
	public T visitNode(Node<T> node) {
		if (n == 0){
			return node.data;
		}else{
			this.n = n - 1;
			return node.next.visit(this);
		}
	}
	
	// returns an error as the given n was too large and the end of the list
	// has been reached
	public T visitSentinel(Sentinel<T> sent) {
		throw new RuntimeException("GetV- List too small");
	}
	
}

// represents a visitor which goes through nodes to change the node in the nth
//position to contain the value in the 'changeTo' field
class SetV<T> implements NodeVisitor<T, T> {
	int n;
	T changeTo;
	
	SetV(int n, T c) {
		this.n = n;
		this.changeTo = c;
	}
	
	// goes down nodes and changes the node in the nth position to contain a
	// specified data according to this constructor
	//EFFECT: changes the data field of the nth node to 'changeTo'
	public T visitNode(Node<T> node) {
		if (n == 0) {
			
			T original = node.data;
			node.data = changeTo;
			
			return original;
		}else{
			this.n = n - 1;
			return node.next.visit(this);
		}
	}
	
	// goes down nodes and changes the node in the nth position to contain a
	// specified data according to this constructor
	//(The sentinel marks the end of the list so reaching here means that the list
	// was too small for the given n)
	public T visitSentinel(Sentinel<T> sent) {
		throw new RuntimeException("SetV- List too small");
	}
}

// Represents a visitor which inserts a node with a given data into the nth
//position
class InsertV<T> implements NodeVisitor<T, Void> {
	int n;
	T insertThis;
	
	InsertV(int n, T i) {
		this.n = n;
		this.insertThis = i;
	}
	
	// Goes down nodes, and inserts a new node at the nth position containing
	// the given data specified by the constructor
	//EFFECT: The node before this spot now leads to this new node while the
	//node that used to be at this spot now backtracks to this new node
	public Void visitNode(Node<T> node) {
		if (this.n == 0) {
			Node<T> oldNode = new Node<T>(node.data, node.next, node);
			node.next = oldNode;
			node.data = insertThis;
			node.moveAhead().changePrevTo(node);
			
			return null;
		}else{
			this.n = n - 1;
			node.next.visit(this);
			
			return null;
		}
	}
	
	// Goes down nodes, and inserts a new node at the nth position containing
	// the given data specified by the constructor
	//(Reaching the sentinel means that the end of the list has been reached
	//and the given n was too large for this list)
	public Void visitSentinel(Sentinel<T> node) {
		throw new RuntimeException("InsertV- List not long enough");
	}	
}

//Represents a visitor which removes a node with a given data into the nth
//position
class RemoveV<T> implements NodeVisitor<T, T> {
	int n;
	
	RemoveV(int n){
		this.n = n;
	}
	
	// Goes down nodes, removing the node at the nth position
	//EFFECT: The node before the removed node now leads to the node ahead of the
	//removed node. The node ahead of the removed node now backtracks to
	// the node before the removed node.
	public T visitNode(Node<T> node) {
		if (this.n == 0) {
			node.moveAhead().changePrevTo(node.prev);
			node.moveBack().changeNextTo(node.next);
			
			return node.data;
		}else{
			this.n = this.n - 1;
			return node.next.visit(this);
		}
	}
	
	// Goes down nodes, removing the node at the nth position
	//(Reaching the sentinel means reaching the end of the list and that
	// the given n was too large for this list)
	public T visitSentinel(Sentinel<T> sent){
		throw new RuntimeException("RemoveV- List too small");
	}
}

// Represents a visitor which returns the length of a list by going down nodes
class LengthV<T> implements NodeVisitor<T, Integer> {
	
	LengthV() {}
	
	//Returns the length of this node (goes down this node until a sentinel is
	//reached)
	public Integer visitNode(Node<T> node) {
		return 1 + node.next.visit(this);
	}
	
	//Returns the length of this node (goes down this node until a sentinel is
	//reached)
	public Integer visitSentinel(Sentinel<T> sent) {
		return 0;
	}
}

// Represents a visitor which reverses a MutableList by going down nodes and
//switching where the next and prev fields point to
class ReverseV<T> implements NodeVisitor<T, Void> {
	
	ReverseV() {}
	
	// reverses the order that these nodes point to
	//EFFECT changes this node's next field to the prev field and the prev
	//field to the next field. Then reruns this visitor on what was the next
	//node in this list
	public Void visitNode(Node<T> node) {
		Node<T> n = node.next;
		Node<T> p = node.prev;
		node.next = p;
		node.prev = n;
		node.prev.visit(this);
		
		return null;
	}
	
	// reverses the order that these nodes point to
	//EFFECT changes this node's next field to the prev field and the prev
	//field to the next field.
	public Void visitSentinel(Sentinel<T> sent) {
		Node<T> n = sent.next;
		Node<T> p = sent.prev;
		sent.next = p;
		sent.prev = n;
		
		return null;
		
	}
}

//-----------------------------------------------------------------------------
//Example and Test Classes

// Represents a class for testing whether two ints are equal.
class SameNumber implements ISameTest<Integer> {
	
	SameNumber() {}
	
	// are the given first and second ints equal?
	public boolean check(Integer first, Integer second) {
		return first == second;
	}
}

class Examples9_2 {
	
	Deque<Integer> d0 = new Deque<Integer>();
	Deque<Integer> d1 = new Deque<Integer>();
	Deque<String> d2 = new Deque<String>();

	void testDeque(Tester t) {
		
		//Cons and Get
		d2.reset();
		d2.cons("a");
		t.checkExpect(d2.get(0), "a");
		d2.cons("b");
		t.checkExpect(d2.get(0), "b");
		t.checkExpect(d2.get(1), "a");
		d2.cons("c");
		t.checkExpect(d2.get(0), "c");
		t.checkExpect(d2.get(1), "b");
		t.checkExpect(d2.get(2), "a");
		d2.reset();
		
		//Set
		d1.reset();
		d1.cons(1);
		d1.cons(2);
		t.checkExpect(d1.get(0), 2);
		t.checkExpect(d1.set(0,5), 2);
		t.checkExpect(d1.get(0), 5);
		t.checkExpect(d1.set(1, 4), 1);
		t.checkExpect(d1.get(1), 4);
		d1.reset();
		
		//Insert and Length
		d1.reset();
		d1.cons(1);
		d1.cons(2);
		t.checkExpect(d1.length(), 2);
		d1.insert(0, 10);
		t.checkExpect(d1.length(), 3);
		t.checkExpect(d1.get(0), 10);
		t.checkExpect(d1.get(1), 2);
		t.checkExpect(d1.get(2), 1);
		d1.insert(2, 50);
		t.checkExpect(d1.length(), 4);
		t.checkExpect(d1.get(2), 50);
		d1.reset();
		
		//Remove
		d2.reset();
		d2.cons("x");
		t.checkExpect(d2.length(), 1);
		t.checkExpect(d2.remove(0), "x");
		t.checkExpect(d2.length(), 0);
		d2.reset();
		d2.cons("y");
		d2.cons("z");
		t.checkExpect(d2.remove(1), "y");
		t.checkExpect(d2.length(), 1);
		t.checkExpect(d2.get(0), "z");
		d2.reset();
		
		//Snoc and Reverse
		d1.reset();
		d1.cons(1);
		d1.snoc(2);
		d1.snoc(3);
		d1.snoc(4);
		t.checkExpect(d1.length(), 4);
		t.checkExpect(d1.get(0), 1);
		t.checkExpect(d1.get(1), 2);
		t.checkExpect(d1.get(2), 3);
		t.checkExpect(d1.get(3), 4);
		d1.reverse();
		t.checkExpect(d1.length(), 4);
		t.checkExpect(d1.get(0), 4);
		t.checkExpect(d1.get(1), 3);
		t.checkExpect(d1.get(2), 2);
		t.checkExpect(d1.get(3), 1);
		d1.reset();
		
		//SameList
		d0.reset();
		d1.reset();
		t.checkExpect(d1.sameList(d0, new SameNumber()));
		t.checkExpect(d1.sameList(d1, new SameNumber()));
		d1.cons(1);
		t.checkExpect(d1.sameList(d1, new SameNumber()));
		t.checkExpect(d1.sameList(d0, new SameNumber()), false);
		d0.cons(1);
		t.checkExpect(d1.sameList(d0, new SameNumber()));
		d1.snoc(5);
		t.checkExpect(d1.sameList(d1, new SameNumber()));
		t.checkExpect(d0.sameList(d1, new SameNumber()), false);
		d0.snoc(2);
		t.checkExpect(d0.sameList(d1, new SameNumber()), false);
		t.checkExpect(d0.set(1, 5), 2);
		t.checkExpect(d1.sameList(d0, new SameNumber()));
		
	}
	
}




