/* 
Nam Luu Nhat, William Silva
Professor Lerner
Fundamentals II (Honors)
3 March 2014

Assignment #8: Quick Lists and Visits,Revisited
*/

import tester.*;

//-----------------------------------------------------------------------------
//Problem 8.2 Quick Lists and 8.3 Quick Visits
//Run Settings- 
//Main Class: tester.Main
//Arguments: Examples8_2
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//Binary Tree Interfaces and Classes
//-----------------------------------------------------------------------------

//represents a binary tree with values of type T at nodes and leaves
interface IBinaryTree<T> {

	/** Return the size of this binary tree */
	int size();

	IBinaryTree<T> doubleTree(IBinaryTree<T> tree, T val);
	
	/** Return the top element of this tree */
	T top();
	
	/** Returns the left child of this tree */
	IBinaryTree<T> getLeft();
	
	/** Returns the right child of this tree */
	IBinaryTree<T> getRight();
	
	/** Returns the value of this tree */
	T getValue();
	
	/** Returns the kth-element of this tree */
	T searchFor(int k);
}

//represents the node of a binary tree holding a value of type T
class Node<T> implements IBinaryTree<T> {

	IBinaryTree<T> left, right;
	T value;

	Node(T value, IBinaryTree<T> left, IBinaryTree<T> right) {
		this.left = left;
		this.value = value;
		this.right = right;
	}

	public int size() {
		return 1 + this.left.size() + this.right.size();
	}

	public IBinaryTree<T> doubleTree(IBinaryTree<T> tree, T val) {
		return new Node<T>(val, tree, this);
	}
	
	public T top() {
		return this.value;
	}
	
	public IBinaryTree<T> getLeft() { return this.left; }
	
	public IBinaryTree<T> getRight() { return this.right; }
	
	public T getValue() { return this.value; }
	
	public T searchFor(int k) {
		if (k == 0) { 
			return this.value;
		} else if (k <= this.size()/2) {
			return this.left.searchFor(k-1);
		} else {
			return this.right.searchFor(k - (this.size() + 1)/2);
		}
	}
}

//represent a leaf of a binary tree holding a value of type T
class Leaf<T> implements IBinaryTree<T> {

	Leaf() {}

	public int size() {
		return 0;
	}

	public IBinaryTree<T> doubleTree(IBinaryTree<T> tree, T val) {
		return new Node<T>(val, tree, this);
	}
	
	public T top() {
		return null;
	}
	
	public IBinaryTree<T> getLeft() {
		throw new RuntimeException("Error: cannot call getLeft on a leaf");
	}
	
	public IBinaryTree<T> getRight() {
		throw new RuntimeException("Error: cannot call getRight on a leaf");
	}
	
	public T getValue() {
		throw new RuntimeException("Error: cannot call getValue on a leaf");
	}
	
	public T searchFor(int k) {
		throw new RuntimeException("Error: the element which is being looked for doesn't exist");
	}
}

//-----------------------------------------------------------------------------
//Visitor Interface and Classes
//-----------------------------------------------------------------------------

//represents a visitor that can process an empty or non-empty IList when
//given to it
interface IListVisitor<T, U> {
	
	/** Visit an empty list and produce a U */
	U visitMt();
	
	/** Visit a "cons" (i.e., first and rest) and produce a U */
	U visitCons(T first, IList<T> rest);
}

//represents a visitor that computes the length of the IList given
//to it
class LengthVisitor<T> implements IListVisitor<T, Integer> {
	
	Object param;
	
	public Integer visitMt() { return 0; }
	
	public Integer visitCons(T first, IList<T> rest) {
		return 1 + rest.visit(this);
	}
}

//represents a visitor that produces a QuickList version of the
//IList given to it that is reversed
class ReverseVisitor<T> implements IListVisitor<T, IList<T>> {
	
	public IList<T> visitMt() {
		return new QuickList<>();
	}
	
	public IList<T> visitCons(T first, IList<T> rest) {
		IList<T> listFirst = new QuickList<T>().cons(first);
		return rest.visit(this).visit(new AppendVisitor<T>(listFirst));
	}
	
	public IList<T> visitMt(Object param) {
		return this.visitMt();
	}
	
	public IList<T> visitCons(T first, IList<T> rest, Object param) {
		return this.visitCons(first, rest);
	}
}

//represents a visitor that appends the given list to the front of
//the accumulator. Note that the type of the produced list is the
//type of the accumulator
class AppendVisitor<T> implements IListVisitor<T, IList<T>> {
	
	IList<T> accumulator;
	
	AppendVisitor(IList<T> acc) {
		this.accumulator = acc;
	}
	
	public IList<T> visitMt() { 
		return accumulator;
	}
	
	public IList<T> visitCons(T first, IList<T> rest) { 
		return rest.visit(this).cons(first);
	}
}

//represents a visitor that runs a function over a given IList
//to produce a QuickList in which each value is a value from the
//given IList modified by the function
class MapVisitor<T, U> implements IListVisitor<T, IList<U>> {
	
	IFun<T, U> function;
	
	MapVisitor(IFun<T, U> fun) {
		this.function = fun;
	}
	
	public IList<U> visitMt() {
		return new QuickList<U>();
	}
	
	public IList<U> visitCons(T first, IList<T> rest) {
		return rest.visit(this).cons(function.apply(first));
	}
}

//-----------------------------------------------------------------------------
//IFunction Interface and Classes
//-----------------------------------------------------------------------------

//represents a function that takes a value and produces some other value
interface IFun<T, U> {
	U apply(T arg);
}

//represents a function that takes an integer and returns that integer plus 1
class Add1 implements IFun<Integer, Integer> {
	public Integer apply(Integer s) {
		return s + 1;
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//IList
//-----------------------------------------------------------------------------

//represents a list containing data all of type T
interface IList<T> {
	
	// Cons given element on to this list
	IList<T> cons(T t);

	// Get the first element of this list (or throw and error
	// if the list is empty)
	T first();
	
	// Get the rest of this list(or throw an error if the list
	// is empty)
	IList<T> rest();
	
	// Get the nth element of this list (or throw an error if 
	// the list is too short)
	T get(int n);
	
	// Compute the number of element in this list
	int length();
	
	// Allows a visitor to visit this list's data
	<U> U visit(IListVisitor<T, U> visitor); 
}

//-----------------------------------------------------------------------------
//QuickList
//-----------------------------------------------------------------------------

//Represents a QuickList which consists of a list of binary trees
//the inner List of Trees is organized in such a way that:
//-it consists of increasingly large full binary trees.
//-with the possible exception of the first two trees, every successive tree
//is strictly larger
class QuickList<T> implements IList<T> {
	
	TreeList<T> contents;
	
	QuickList() {
		contents = new MtBTList<T>();
	}
	
	QuickList(TreeList<T> contents) {
		this.contents = contents;
	}

	public IList<T> cons(T t) {
		TreeList<T> newContent = contents.addElement(t);
		return new QuickList<T>(newContent);
	}
	
	public int length() {
		return this.contents.totalSize();
	}
	
	public T first() {
		return this.contents.leadingElement();
	}
	
	public IList<T> rest() {
		TreeList<T> newContent = contents.allButFirst();
		return new QuickList<T>(newContent);
	}
	
	public T get(int k) {
		return this.contents.getElem(k);
	}
	
	public <U> U visit(IListVisitor<T, U> visitor) {
		if (this.contents.length() == 0) {
			return visitor.visitMt();
		} else {
			return visitor.visitCons(this.first(), this.rest());
		}
	}
}

//-----------------------------------------------------------------------------
//TreeList
//-----------------------------------------------------------------------------

//represents a standard list of conses with an empty at the end containing
//binary trees
abstract class TreeList<T> implements IList<IBinaryTree<T>> {
	
	/** Return true if this tree's first tree has the same size as that */
	abstract boolean equalSize(IBinaryTree<T> that);
	
	/** Add elem to this list */
	abstract TreeList<T> addElement(T elem);
	
	/** Return the leading element of this list */
	abstract T leadingElement();
	
	/** Return the rest of this list */
	public abstract TreeList<T> rest();
	
	/** Cons the given tree to this list */
	public TreeList<T> cons(IBinaryTree<T> that) {
		return new BTList<T>(that, this);
	}
	
	/** Return all elements but the first one of this list */
	abstract TreeList<T> allButFirst();
	
	/** Return the total size of all trees in this list */
	abstract int totalSize();
	
	/** Return the nth-element in all trees*/
	abstract T getElem(int n);
	
}

//represents a cons of a typical list exclusively containing binary trees
class BTList<T> extends TreeList<T> {
	
	IBinaryTree<T> first;
	TreeList<T> rest;
	
	BTList(IBinaryTree<T> first, TreeList<T> rest) {
		this.first = first;
		this.rest = rest;
	}
	
	public IBinaryTree<T> first() {
		return this.first;
	}
	
	public TreeList<T> addElement(T elem) {
		IBinaryTree<T> newFirst;
		TreeList<T> newRest;
		if (this.rest.equalSize(this.first)) {
			newFirst = this.rest.first().doubleTree(this.first, elem);
			newRest = this.rest.rest();
		} else {
			newFirst = new Node<T>(elem, new Leaf<T>(), new Leaf<T>());
			newRest = this.rest.cons(this.first);
		}
		return new BTList<T>(newFirst, newRest);
	}
	
	public TreeList<T> allButFirst() {
		if (this.length() == 1 && this.first.size() == 1) {
			return new MtBTList<T>();
		} else if (this.first.size() == 1) {
			IBinaryTree<T> newFirst = this.rest.first();
			return new BTList<T>(newFirst, this.rest.rest());
		} else {
			IBinaryTree<T> temp = this.first;
			IBinaryTree<T> newFirst = temp.getLeft();
			TreeList<T> newRest = this.rest.cons(temp.getRight());
			return new BTList<T>(newFirst, newRest);
		}
	}
	
	public TreeList<T> rest() {
		return this.rest;
	}
	
	boolean equalSize(IBinaryTree<T> that) {
		return this.first.size() == that.size(); 
	}
	
	public int length() {
		return 1 + this.rest.length();
	}
	
	public IBinaryTree<T> get(int t) {
		if (t == 0) {
			return this.first;
		} else {
			return this.rest.get(t - 1);
		}
	}
	
	T leadingElement() {
		return this.first.top();
	}
	
	int totalSize() {
		return this.first.size() + this.rest.totalSize();
	}
	
	T getElem(int n) {
		if (n < this.first.size()) {
			return this.first.searchFor(n);
		} else {
			return this.rest.getElem(n - this.first.size());
		}
	}
	
	public <U> U visit(IListVisitor<IBinaryTree<T>, U> visitor) {
		return visitor.visitCons(this.first(), this.rest());
	}
}

//represents an empty typical list that can be extended to only hold
//binary trees
class MtBTList<T> extends TreeList<T> {
	
	public TreeList<T> addElement(T elem) {
		Node<T> newNode = new Node<T>(elem, new Leaf<T>(), new Leaf<T>());
		return this.cons(newNode);
	}
	
	public IBinaryTree<T> first() {
		throw new RuntimeException("first: expect a non-empty tree-list");
	}
	
	public TreeList<T> allButFirst() {
		throw new RuntimeException("rest: expect a non-empty list");
	}
	
	public int length() {
		return 0;
	}
	
	public TreeList<T> rest() {
		return this;
	}
	
	boolean equalSize(IBinaryTree<T> that) {
		return false; 
	}
	
	public IBinaryTree<T> get(int t) {
		throw new RuntimeException("Error: this list is too short");
	}
	
	T leadingElement() {
		throw new RuntimeException("Error: first: expect a non-empty list");
	}
	
	int totalSize() {
		return 0;
	}
	
	T getElem(int n) {
		throw new RuntimeException("Error: this list is too short");
	}
	
	public <U> U visit(IListVisitor<IBinaryTree<T>, U> visitor) {
		return visitor.visitMt();
	}
}

//-----------------------------------------------------------------------------
//Test Class
//-----------------------------------------------------------------------------

class Examples8_2 {
	
	//QuickList Tests
	
	IList<Integer> mt = new QuickList<Integer>();
	IList<Integer> list1 = mt.cons(1).cons(2).cons(3).cons(4).cons(5);
	IList<Integer> list2 = mt.cons(1).cons(2).cons(3).cons(4);
	IList<Integer> list3 = mt.cons(6).cons(7).cons(8).cons(9);
	IList<Integer> list4 = mt.cons(1).cons(2);
	IList<Integer> list5 = mt.cons(3).cons(4).cons(5);
	
	void test(Tester t) {
		// Test length()
		t.checkExpect(list1.length(), 5);
		t.checkExpect(list2.length(), 4);
		t.checkExpect(list1.rest().length(), 4);
		t.checkExpect(list1.rest().rest().length(), 3);
		t.checkExpect(list1.rest().rest().rest().length(), 2);
		
		// Test rest()
		t.checkExpect(list1.cons(7).rest(), list1);
		t.checkExpect(list1.rest(), list2);
		t.checkExpect(list1.rest().rest(), list2.rest());
		t.checkExpect(list1.rest().rest().rest(), list2.rest().rest());
		
		// Test first()
		t.checkExpect(list2.first(), 4);
		t.checkExpect(list1.first(), 5);
		t.checkExpect(list1.rest().first(), 4);
		t.checkExpect(list1.rest().rest().first(), 3);
		t.checkExpect(list1.rest().rest().rest().first(), 2);
		t.checkExpect(mt.length(), 0);
		
		// Test get()
		t.checkExpect(list1.get(0), 5);
		t.checkExpect(list1.get(1), 4);
		t.checkExpect(list1.get(2), 3);
		t.checkExpect(list1.get(3), 2);
		t.checkExpect(list1.get(4), 1);
		t.checkExpect(list1.rest().get(1), 3);
		t.checkExpect(list1.rest().get(0), 4);		
		
		// Test LengthVisitor
		t.checkExpect(list1.visit(new LengthVisitor<Integer>()), 5);
		t.checkExpect(list2.visit(new LengthVisitor<Integer>()), 4);
		
		// Test AppendVisitor
		t.checkExpect(list5.visit(new AppendVisitor<Integer>(list4)),
				list1);
		t.checkExpect(list4.visit(new AppendVisitor<Integer>(list3)),
				list3.cons(1).cons(2));
		
		// Test ReverseVisitor
		t.checkExpect(list1.visit(new ReverseVisitor<Integer>()),
				mt.cons(5).cons(4).cons(3).cons(2).cons(1));
		t.checkExpect(list4.visit(new ReverseVisitor<Integer>()),
				mt.cons(2).cons(1));
		t.checkExpect(list5.visit(new ReverseVisitor<Integer>()),
				mt.cons(5).cons(4).cons(3));
		
		// Test MapVisitor
		t.checkExpect(list1.visit(new MapVisitor<Integer, Integer>(new Add1())),
				mt.cons(2).cons(3).cons(4).cons(5).cons(6));
		t.checkExpect(list5.visit(new MapVisitor<Integer, Integer>(new Add1())),
				mt.cons(4).cons(5).cons(6));
	}
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
