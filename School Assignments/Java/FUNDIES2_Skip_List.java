/* 
Sadruddin Saleem, William Silva
Professor Lerner
Fundamentals II (Honors)
31 March 2014

Assignment #10: Skip Lists
*/

import tester.*;

//-----------------------------------------------------------------------------
//Building Skip Lists
//Run Settings- 
//Main Class: tester.Main
//Arguments: Examples10_1

//NOTE: The Deque from last assignment is included before the skiplist.
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------



//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//Deque
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
	
	//Returns the number of nodes in this deque
	public int length() {
		return this.header.moveAhead().visit(new LengthV<T>());
	}
	
	//EFFECT: Reverses the next and prev fields of all nodes in this list
	public void reverse() {
		
		this.header.moveAhead().visit(new ReverseV<T>());
	}
	
	//Is this deque have the same structure as the given deque according
	//to the given ISameTest comparator?
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
	
	//EFFECT: Runs the given IFunction on the data field of every node.
	void mapperData(IFunction<T, Void> func) {
		this.header.moveAhead().mapperDataHelper(func);
	}
	
	//Returns the ith node from the sentinel
	Node<T> getNode(int i) {
		return this.header.moveAhead().getNodeHelper(i);
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
	
	//EFFECT: Maps the function over the data in every node in this list of
	//nodes going forward until the sentinel is reached.
	void mapperDataHelper(IFunction<T, Void> func) {
		func.apply(this.data);
		this.moveAhead().mapperDataHelper(func);
	}
	
	//Gets the ith node from this node list going forward in the list.
	Node<T> getNodeHelper(int i) {
		if (i == 0) {
			return this;
		}else{
			return this.moveAhead().getNodeHelper(i - 1);
		}
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
	
	//Signals the end of the list so the mapper is done and nothing
	//occurs.
	//EFFECT: None.
	void mapperDataHelper(IFunction<T, Void> func) {

	}
	
	//Gets the ith node from this node going forward.
	//A sentinel signals the end of the list so the ith node does not exist
	//and an error occurs.
	Node<T> getNodeHelper(int i) {
		throw new RuntimeException("getNodeHelper- Not enough nodes");
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
//Test Classes

// Represents a class for testing whether two ints are equal.
class SameNumber implements ISameTest<Integer> {
	
	SameNumber() {}
	
	// are the given first and second ints equal?
	public boolean check(Integer first, Integer second) {
		return first == second;
	}
}



//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//SKIPLIST ASSIGNMENT
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//Skiplist Class

//A SkipList<V> is a
//new SkipList<V>(Integer, V)
//and implements
//
//insert : Integer V -> Void
//Effect: updates the list with a new element
//add an element with the given integer key
//
//search : Integer -> V
//look up the element matching the given key

class SkipList<V> {
	
	Element<V> firstElem;
	
	SkipList(int key, V val){
		this.firstElem = new Element<V>(key, val, Global.maxLevel);
	}
	
	//does this list contain the given key?
	public boolean contains(int k) {
		return this.firstElem.nodeList().moveBack().visit(new SkipContainsV<V>(k));
	}
	
	//returns the length of this list
	public int length() {
		return this.firstElem.lengthHelper();
	}
	
	//look up the element matching the given key
	public V search(int k) {
		return firstElem.nodeList().moveBack().visit(new SkipSearchV<V>(k));
	}
	
	//Effect: updates the list with a new element
	//add an element with the given integer key
	public void insert(int k, V val) {
		
		int lengthOfNew = 1 + Probability.coinFlipper(Global.maxLevel - 1);
		Element<V> newElem = new Element<V>(k, val, lengthOfNew);
		
		if (firstElem.compareKeys(k) > 0) {
			throw new RuntimeException("insert- Does not make sense to insert item before initial element.");
		}else{ //Element is here already
			
		if (this.contains(k)) {
			firstElem.nodeList().moveBack().visit(new SkipReplaceV<V>(k, val));
			
		}else{ //new Element goes after first one
			
			this.firstElem.levels().getNode(lengthOfNew - 1).visit(new SkipInsertV<V>(newElem));
			}
		}
	}
	
	public void remove(int k) {
		
		if (this.length() == 1 || this.firstElem.compareKeys(k) == 0) {
			throw new RuntimeException("remove- List must have more than one item and you cannot remove first item.");
		}
		
		if (this.contains(k)) {
			firstElem.nodeList().moveBack().visit(new SkipRemoveV<V>(k));
		}else{
			throw new RuntimeException("remove- Key is not here so cannot be removed.");
		}
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//Utility Class

//Represents a class for using probability functions
class Probability {
	
	//Returns the number of times a coin is flipped on heads sucessfully.
	//The coin is flipped the given number of times or until a tails appears.
	public static int coinFlipper(int times) {
		if (times == 0){
			return 0;
		}else{
			
		if ((int)Math.round(Math.random()) == 0) {
			return 1 + Probability.coinFlipper(times - 1);
		}else{
			
			return 0;
			}
		}
	}
	
}

//Represents a utility for calling global variables.
class Global {
	
	//Represents the max number of levels of the skiplist
	public static int maxLevel = 5;
	
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//SkiplistNode Interface and Classes

//A SkiplistNode<V> is one of
//new MTNode()
//new Element(Integer, V, Integer)
//and implements
//
//isEmpty : -> Boolean
//specify whether this is the end
//
//getKey : -> Integer
//get the key at this node
//
//getValue : -> V
//get the value at this node
//
//setValue : Integer V -> Void
//Effect: updates the value in-place
//sets the value for this node
//
//getNext : Integer -> Node<V>
//gets the next node linked at the given level
//
//setNext : Integer Node<V> -> Void
//Effect: updates the next node in-place
//sets the next node linked at the given level

interface SkiplistNode<V> {
	
	//specify whether this is the end
	boolean isEmpty();
	
	//get the key at this node
	int getKey();
	
	//get the value at this node
	V getValue();
	
	//Effect: updates the value in-place
	//sets the value for this node
	void setValue(int key, V val);
	
	//gets the next node linked at the given level
	SkiplistNode<V> getNext(int level);
	
	//Effect: updates the next node in-place
	//sets the next node linked at the given level
	void setNext(int level, SkiplistNode<V> n);
	
	//EFFECT: In all nodes for all levels of this, sets the
	//SLNodeData's upperElement to be the given elem
	void setUpperElemAllData(SkiplistNode<V> elem);
	
	//Returns the levels field of this node which consists
	//of a Deque
	Deque<SLNodeData<V>> levels();
	
	//Returns a positive, negative, or zero number depending
	//on whether the key of this node is larger, smaller, or
	//equal respectively.
	int compareKeys(int k);
	
	//Returns the Sentinel of the Deque in the header field
	//of this
	Node<SLNodeData<V>> nodeList();
	
	//Returns how many SkiplistNodes there are, including this one
	//and all elements linked through nodes to this
	int lengthHelper();
	
}


class Element<V> implements SkiplistNode<V>	{
	
	int key;
	V value;
	Deque<SLNodeData<V>> levels;
	
	Element(int k, V val, int height) {
		this.key = k;
		this.value = val;
		
		Deque<SLNodeData<V>> deque = new Deque<SLNodeData<V>>();
		MTNode<V> mtNode = new MTNode<V>(Global.maxLevel);
		
		for (int i = height; i > 0; i = i - 1) {
			deque.cons(new LinkedNodeData<V>(mtNode.levels.getNode(i - 1)));
		}
		
		this.levels = deque;
		
		this.setUpperElemAllData(this);
		
	}
	
	//specify whether this is the end
	public boolean isEmpty() {
		return false;
	}
	
	//get the key at this node
	public int getKey() {
		return this.key;
	}
	
	//get the value at this node
	public V getValue() {
		return this.value;
	}
	
	//Effect: updates the value in-place
	//sets the value for this node
	public void setValue(int k, V val) {
		this.key = k;
		this.value = val;
	}
	
	//gets the next node linked at the given level
	public SkiplistNode<V> getNext(int level) {
		return this.levels.get(level).moveRight().data.moveUp();
	}
	
	//Effect: updates the next node in-place
	//sets the next node linked at the given level
	public void setNext(int level, SkiplistNode<V> n) {
		this.levels.get(level).setLink(n.levels().getNode(level));
	}
	
	//EFFECT: In all nodes for all levels of this, sets the
	//SLNodeData's upperElement to be the given elem
	public void setUpperElemAllData(SkiplistNode<V> elem) {
		this.levels.mapperData(new SetUpperElemF<V>(elem));
	}
	
	//Returns the levels field of this node which consists
	//of a Deque
	public Deque<SLNodeData<V>> levels() {
		return this.levels;
	}
	
	//Returns a positive, negative, or zero number depending
	//on whether the key of this node is larger, smaller, or
	//equal respectively.
	public int compareKeys(int k) {
		return this.key - k;
	}
	
	//Returns the Sentinel of the Deque in the header field
	//of this
	public Node<SLNodeData<V>> nodeList() {
		return this.levels().header;
	}
	
	//Returns how many SkiplistNodes there are, including this one
	//and all elements linked through nodes to this
	public int lengthHelper() {
		return 1 + this.levels().getNode(0).data.moveRight().data.moveUp().lengthHelper();
	}
	
}

class MTNode<V> implements SkiplistNode<V> {
	
	Deque<SLNodeData<V>> levels;
	
	MTNode(int height) {
		
		MTNodeData<V> mtData = new MTNodeData<V>();
		
		Deque<SLNodeData<V>> deque = new Deque<SLNodeData<V>>();
		
		for (int i = height; i > 0; i = i - 1) {
		deque.cons(mtData);
		}
		
		this.levels = deque;
		
		this.setUpperElemAllData(this);
		
	}
	
	//specify whether this is the end
	public boolean isEmpty() {
		return true;
	}
	
	//get the key at this node
	public int getKey() {
		throw new RuntimeException("getKey- Cannot get key of MTNode");
	}
	
	//get the value at this node
	public V getValue() {
		throw new RuntimeException("getValue- Cannot get value of MTNode");
	}
	
	//Effect: updates the value in-place
	//sets the value for this node
	public void setValue(int key, V val) {
		throw new RuntimeException("setValue- Cannot set value of MTNode");
	}
	
	//gets the next node linked at the given level
	public SkiplistNode<V> getNext(int level) {
		throw new RuntimeException("getNext- MTNode has no next node");
	}
	
	//Effect: updates the next node in-place
	//sets the next node linked at the given level
	public void setNext(int level, SkiplistNode<V> n) {
		throw new RuntimeException("setNext- MTNode cannot have next node");
	}
	
	//EFFECT: In all nodes for all levels of this, sets the
	//SLNodeData's upperElement to be the given elem
	public void setUpperElemAllData(SkiplistNode<V> elem) {
		this.levels.mapperData(new SetUpperElemF<V>(elem));
	}
	
	//Returns the levels field of this node which consists
	//of a Deque
	public Deque<SLNodeData<V>> levels() {
		return this.levels;
	}
	
	//Returns a positive, negative, or zero number depending
	//on whether the key of this node is larger, smaller, or
	//equal respectively.
	public int compareKeys(int k) {
		return 1;
	}
	
	//Returns the Sentinel of the Deque in the header field
	//of this
	public Node<SLNodeData<V>> nodeList() {
		return this.levels().header;
	}
	
	//Returns how many SkiplistNodes there are, including this one
	//and all elements linked through nodes to this
	public int lengthHelper() {
		return 0;
	}
	
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//IFunction Interface and Classes

//Represents a function which can be applied to a given data of type T to
//produce data of type U
interface IFunction<T, U> {
	
	//applies some function to transform data
	U apply(T t);
}

//Represents a function which takes SLNodeData and changes its upperElem field
//to the given e in the constructor
class SetUpperElemF<V> implements IFunction<SLNodeData<V>, Void> {
	
	SkiplistNode<V> elem;
	
	SetUpperElemF(SkiplistNode<V> e) {
		this.elem = e;
	}
	
	//EFFECT: Changes the upperElem field of the given nodeData to the element
	//given when this class was constructed.
	public Void apply(SLNodeData<V> nodeData) {
		nodeData.setUpperElem(elem);
		return null;
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//NodeVisitor Classes

//Represents a visitor for Deque nodes which searches for a specific key in the
//SLNodeData and returns the V value associated with that key.
class SkipSearchV<V> implements NodeVisitor<SLNodeData<V>, V> {
	
	int key;
	
	SkipSearchV(int k) {
		this.key = k;
	}
	
	//Goes through nodes in the node list, checking to see if the SLNodeData contains
	//the key given in the constructor of this class. If the SLNodeData link has a
	//larger key, it moves back in the node list. If the SLNodeData link has a
	//smaller key, it goes into the link's node.
	public V visitNode(Node<SLNodeData<V>> node) {
		int nodeKey = node.data.moveUp().getKey();
		int compareNextKey = node.data.moveRight().data.moveUp().compareKeys(this.key);
		
		if (nodeKey == this.key) {
			return node.data.moveUp().getValue();
		}else{
			
		if (compareNextKey == 0){
			return node.data.moveRight().data.moveUp().getValue();
		}else{
			
		if (compareNextKey > 0) {
			return node.moveBack().visit(this);
		}else{ //compareNextKey < 0
			
			return node.data.moveRight().visit(this);
				}
			}
		}
	}
	
	//Throws an error as a sentinel signals the end of the list and the key
	//was not found.
	public V visitSentinel(Sentinel<SLNodeData<V>> sent) {
		throw new RuntimeException("visitSentinel in SkipSearch- Key not found.");
	}
	
}

//Represents a visitor which links the given element of the constructor into
//the SLNodeData of the appropriate nodes for a Skiplist.
//Assumption: The key of the given e for the constructor does not already exist
//in any of the SLNodeData
class SkipInsertV<V> implements NodeVisitor<SLNodeData<V>, Void> {
	
	//The currentNode of the element we are inserting that we are on
	Node<SLNodeData<V>> currentNode;
	
	SkipInsertV(Element<V> e) {
		this.currentNode = e.levels.getNode(e.levels.length() - 1);
	}
	
	//Changes the links of nodes such that they appropriately link to the element
	//of the constructor.
	//This method should be given the nth node at first (not zero index) where n 
	//is how many levels the given 'e' in the constructor has. Reason- No links
	//have to change in levels greater than the height of the given 'e'.
	//Algorithm description: This works by checking the key that the given 'node'
	//links to. If the key is larger than our currentNode's key, it means we
	//want to insert our currentNode in between these links. Then we go forward
	//in our currentNode list and our given node list (moving down a level).
	//If the key is smaller, it means we are done linking the nodes of our
	//current element and we rerun the visitor on the node that our given
	//node linked to (equivalent to moving right an element).
	//EFFECT: Goes through nodes and the links of nodes to link to the currentNode
	//and changes the links including in the currentNode list to link with later
	//node links of our given node.
	public Void visitNode(Node<SLNodeData<V>> node) {
			SkiplistNode<V> nextSLNode = node.data.moveRight().data.moveUp();
			int elemToInsertKey = this.currentNode.data.moveUp().getKey();
			int compareKeys = nextSLNode.compareKeys(elemToInsertKey);
		
		if (compareKeys > 0){ //NextNode's key is greater than key we want to set
			this.currentNode.data.setLink(node.data.moveRight());
			node.data.setLink(currentNode);
			this.currentNode = currentNode.moveBack();
			node.moveBack().visit(this);
			
			return null;
			
		}else{ //compareKeys < 0
			node.data.moveRight().visit(this);
			return null;
		}
	}
	
	//Inserts the currentNode into the links of these nodes.
	//A sentinel represents the end of the node list so the entire
	//list has been gone through.
	//EFFECT: None
	public Void visitSentinel(Sentinel<SLNodeData<V>> sent) {
		return null;
	}
	
}

//Represents a visitor which modifies the value of an element with a specific key
//in a skiplist.
class SkipReplaceV<V> implements NodeVisitor<SLNodeData<V>, Void> {
	
	int key;
	V val;
	
	SkipReplaceV(int k, V v) {
		this.key = k;
		this.val = v;
	}
	
	//Checks this node to see if its element has the given key. If it does, changes the
	//value of that element to the 'v' of this class's constructor. If not, goes
	//to the next linked node and tries again.
	//EFFECT: Modifies the element with this.key to have a val field of this.val.
	public Void visitNode(Node<SLNodeData<V>> node) {
		SkiplistNode<V> nextSLNode = node.data.moveRight().data.moveUp();
		int compareKeys = nextSLNode.compareKeys(this.key);
		
		if (node.data.moveUp().getKey() == this.key) {
			node.data.moveUp().setValue(this.key, this.val);
			return null;
		}else{
			
		if (compareKeys == 0) {
			node.data.moveRight().data.moveUp().setValue(this.key, this.val);
			return null;
		}else{
			
		if (compareKeys > 0) {
			node.moveBack().visit(this);
			return null;
		}else{ //CompareKeys < 0
			
			node.data.moveRight().visit(this);
			return null;
				}
			}
		}
	}
	
	//Throws an error as the key could not be found.
	//EFFECT: None.
	public Void visitSentinel(Sentinel<SLNodeData<V>> sent) {
		throw new RuntimeException("SkipReplaceV- could not find key to replace");
	}
}

//Represents a visitor for nodes containing SLNodeData which modifies links
//of these nodes to not link to the nodes associated with the element with
//the key k of the constructor. Nodes instead link to what the removed nodes
//of the same level linked to.
class SkipRemoveV<V> implements NodeVisitor<SLNodeData<V>, Void> {
	
	int key;
	
	SkipRemoveV(int k) {
		this.key = k;
	}
	
	//Assumption: The given node is a node is at the end of its Deque node list
	//Algorithm: Checks whether the next linked node's element's key is larger,
	//smaller, or equal. If it is equal, we set the links of all the nodes in
	//this element to be linked to the node after the next one. If the next
	//key is greater, there is nothing to replace at this level so we move down
	//a level. If the next key is smaller, we know that the links of our current
	//element are okay so we move ahead to the next element and run the visitor
	//on the node of the level we were on previously.
	//EFFECT: Modifies the links of nodes such that they no longer link to
	//nodes associated with the element to be removed but the nodes after that
	//one.
	public Void visitNode(Node<SLNodeData<V>> node) {
		
		SkiplistNode<V> nextSLNode = node.data.moveRight().data.moveUp();
		int compareNextKey = nextSLNode.compareKeys(this.key);
		
		if (compareNextKey == 0) {
			node.data.setLink(node.data.moveRight().data.moveRight());
			node.moveBack().visit(this);
			
			return null;
		}else{
		if (compareNextKey > 0) {//NextNode's key is greater than key we want to set
			node.moveBack().visit(this);
			
			return null;
		}else{ //compareNextKey < 0
			node.data.moveRight().visit(this);
			
			return null;
			}
		}
	}
	
	//We have reached the 0th level of nodes so we are done and nothing happens.
	//EFFECT: None.
	public Void visitSentinel(Sentinel<SLNodeData<V>> sent) {
		return null;
	}
	
}

//Represents a visitor that determines if a key given in the constructor
//exists in the node lists or in the nodes linked to the given node.
class SkipContainsV<V> implements NodeVisitor<SLNodeData<V>, Boolean>{
	
	int key;
	
	SkipContainsV(int k) {
		this.key = k;
	}
	
	//Checks this node and every node linked to it to see if they are associated
	//with an element with the given key from this class's constructor.
	public Boolean visitNode(Node<SLNodeData<V>> node) {
		
		SkiplistNode<V> nextSLNode = node.data.moveRight().data.moveUp();
		int compareNextKey = nextSLNode.compareKeys(this.key);
		
		if (compareNextKey == 0 || this.key == node.data.moveUp().getKey()) {
			return true;
		}else{
		if (compareNextKey > 0) {
			return node.moveBack().visit(this);
		}else{ //compareNextKey < 0
			return node.data.moveRight().visit(this);
			}
		}
	}
	
	//The visitNode method goes from the last node in the list to the
	//first. Reaching the sentinel means the key was not found so false
	//is returned.
	public Boolean visitSentinel(Sentinel<SLNodeData<V>> sent) {
		return false;
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//SLNodeData Interface and Classes

interface SLNodeData<V> {
	
	//EFFECT: Sets the link field of this to the given 'n'.
	void setLink(Node<SLNodeData<V>> n);
	
	//EFFECT: Sets the upperElement field of this to the given 'elem'.
	void setUpperElem(SkiplistNode<V> elem);
	
	//Returns the node that this data links to.
	Node<SLNodeData<V>> moveRight();
	
	//Returns the upper element which contains nodes which all should contain
	//data which have this upper element.
	SkiplistNode<V> moveUp();
	
}

class LinkedNodeData<V> implements SLNodeData<V> {
	
	//link to node of same level on next element
	Node<SLNodeData<V>> link;
	//link to the element containing this node
	SkiplistNode<V> upperElement;
	
	LinkedNodeData(Node<SLNodeData<V>> l){
		this.link = l;
		this.upperElement = null;
	}
	
	//EFFECT: Sets the link field of this to the given 'n'.
	public void setLink(Node<SLNodeData<V>> n) {
		this.link = n;
	}
	
	//EFFECT: Sets the upperElement field of this to the given 'elem'.
	public void setUpperElem(SkiplistNode<V> elem) {
		this.upperElement = elem;
	}
	
	//Returns the node that this data links to.
	public Node<SLNodeData<V>> moveRight() {
		return this.link;
	}
	
	//Returns the upper element which contains nodes which all should contain
	//data which have this upper element.
	public SkiplistNode<V> moveUp() {
		return this.upperElement;
	}
	
	
}

class MTNodeData<V> implements SLNodeData<V> {
	
	//link to the element containing this node.
	SkiplistNode<V> upperElement;
	
	MTNodeData() {
		this.upperElement = null;
	}
	
	//EFFECT: None. Error because MTNode has no link field.
	public void setLink(Node<SLNodeData<V>> n) {
		throw new RuntimeException("setLink- Cannot set link of MTNode");
	}
	
	//EFFECT: Sets the upperElement field of this to the given 'elem'.
	public void setUpperElem(SkiplistNode<V> e) {
		this.upperElement = e;
	}
	
	//Returns the node that this data links to.
	//NOTE: Since MTNode does not have any links, error is thrown.
	public Node<SLNodeData<V>> moveRight() {
		throw new RuntimeException("moveRight- MTNode has no links.");
	}
	
	//Returns the upper element which contains nodes which all should contain
	//data which have this upper element.
	public SkiplistNode<V> moveUp() {
		return this.upperElement;
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//Test Classes

class Examples10_1 {
	
	//-------------------------------------------------------------------------
	//Deque Test Methods
	//-------------------------------------------------------------------------
	
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
	
	//-------------------------------------------------------------------------
	//Skiplist Test Methods
	//-------------------------------------------------------------------------
	
	void testSkipList(Tester t) {
		
		//Test lists
		SkipList<Integer> e1 = new SkipList<Integer>(0, 0);
		SkipList<Integer> e2 = new SkipList<Integer>(-5, -3);
		
		//Searching with single item
		t.checkExpect(e1.search(0), 0);
		t.checkExpect(e1.length(), 1);
		t.checkExpect(e1.contains(0), true);
		t.checkExpect(e1.contains(1), false);
		
		t.checkExpect(e2.search(-5), -3);
		t.checkExpect(e2.length(), 1);
		t.checkExpect(e2.contains(-5), true);
		t.checkExpect(e2.contains(0), false);
		
		//Replacing single item with insert
		e1.insert(0, 1);
		t.checkExpect(e1.search(0), 1);
		t.checkExpect(e1.length(), 1);
		t.checkExpect(e1.contains(0), true);
		
		//Inserting a new item
		t.checkExpect(e1.contains(5), false);
		e1.insert(5, 2);
		t.checkExpect(e1.contains(5), true);
		t.checkExpect(e1.search(5), 2);
		t.checkExpect(e1.length(), 2);
		
		//Updating the new item with insert
		e1.insert(5, 9);
		t.checkExpect(e1.search(5), 9);
		t.checkExpect(e1.length(), 2);
		
		//Inserting more items
		e1.insert(1, 1);
		t.checkExpect(e1.search(1), 1);
		t.checkExpect(e1.length(), 3);
		e1.insert(2, -4);
		t.checkExpect(e1.search(2), -4);
		t.checkExpect(e1.length(), 4);
		
		e1.insert(9, 9);
		t.checkExpect(e1.search(9), 9);
		t.checkExpect(e1.length(), 5);
		e1.insert(8, 10);
		t.checkExpect(e1.length(), 6);
		e1.insert(7, 11);
		t.checkExpect(e1.length(), 7);
		e1.insert(6, 12);
		t.checkExpect(e1.length(), 8);
		
		//Removing items
		e1.remove(1);
		t.checkExpect(e1.contains(1), false);
		t.checkExpect(e1.length(), 7);
		e1.remove(2);
		t.checkExpect(e1.contains(2), false);
		t.checkExpect(e1.length(), 6);
		
		e2.insert(10, 10);
		e2.remove(10);
		t.checkExpect(e2.contains(10), false);
			
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------