/* 
Nam Luu Nhat, William Silva
Professor Lerner
Fundamentals II (Honors)
3 March 2014

Assignment #8: Quick Lists and Visits,Revisited
*/

import tester.*;

//-----------------------------------------------------------------------------
//Problem 8.1 Lab Completion
//Run Settings- 
//Main Class: tester.Main
//Arguments: Examples8_1
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//Visitor Classes
//-----------------------------------------------------------------------------

//Represents a visitor which will perform its operation on the specified type
//of expression
//the inner List of Trees is organized in such a way that:
//-it consists of increasingly large full binary trees.
//-with the possible exception of the first two trees, every successive tree
//is strictly larger
interface IAExpVisitor<T> {
	T visitAConst(AConst exp);
	T visitAPlus(APlus exp);
	T visitATimes(ATimes exp);
	T visitASub(ASub exp);
}

//represents a visitor that evaluates a given expression to an integer value
class EvalVisitor implements IAExpVisitor<Integer> {
	
	EvalVisitor() {}
	
	public Integer visitAConst(AConst exp){
		return exp.val;
	}
	
	public Integer visitAPlus(APlus exp){
		return exp.firstValue.visit(this) + exp.secondValue.visit(this);
	}
	
	public Integer visitATimes(ATimes exp){
		return exp.firstValue.visit(this) * exp.secondValue.visit(this);
	}
	
	public Integer visitASub(ASub exp){
		return exp.firstValue.visit(this) -
				exp.secondValue.visit(this);
	}
}

//reperesents a visitor that produces a string representation of the
//expression given to it
class PrintVisitor implements IAExpVisitor<String> {
	
	PrintVisitor() {}
	
	public String visitAConst(AConst exp){
		return "" + exp.val;
	}
	
	public String visitAPlus(APlus exp){
		return "(" + exp.firstValue.visit(this) + " + "
					+ exp.secondValue.visit(this) + ")";
	}
	
	public String visitATimes(ATimes exp){
		return "(" + exp.firstValue.visit(this) + " * " 
				+ exp.secondValue.visit(this) + ")";
	}
	
	public String visitASub(ASub exp){
		return "(" + exp.firstValue.visit(this) + " - "
				+ exp.secondValue.visit(this) + ")";
	}
}

//represents a visitor that produces a mirrored version of the
//expression given to it
class MirrorVisitor implements IAExpVisitor<AExp> {
	
	MirrorVisitor() {}
	
	public AExp visitAConst(AConst exp) {
		return exp;
	}
	
	public AExp visitAPlus(APlus exp) {
		return new APlus(exp.secondValue.visit(this),
				exp.firstValue.visit(this));
	}
	
	public AExp visitATimes(ATimes exp) {
		return new ATimes(exp.secondValue.visit(this),
				exp.firstValue.visit(this));
	}
	
	public AExp visitASub(ASub exp){
		return new ASub(exp.secondValue.visit(this),
				exp.firstValue.visit(this));
	}
}

//-----------------------------------------------------------------------------
//Expression Classes
//-----------------------------------------------------------------------------

//represents a mathematical expression
abstract class AExp {
	
	//is the given expression the same as this one?
	abstract boolean sameExp(AExp that);
	
	//is the given Constant expression the same as this?
	public boolean sameConst(AConst that){
		return false;
	}
	
	//is the given Addition expression the same as this?
	public boolean samePlus(APlus that){
		return false;
	}
	
	//is the given Multiplication expression the same as this?
	public boolean sameTimes(ATimes that){
		return false;
	}
	
	//is the given Subtraction expression the same as this?
	public boolean sameSub(ASub that){
		return false;
	}
	
	//sends the expression to the given visitor to return some T value
	abstract <T> T visit(IAExpVisitor<T> visitor);
}

//represents a integer constant
class AConst extends AExp {
	
	int val;
	
	AConst(int v){
		this.val = v;
	}
	
	public boolean sameExp(AExp e){
		return e.sameConst(this);
	}
	
	public boolean sameConst(AConst c){
		return this.val == c.val;
	}
	
	public <T> T visit(IAExpVisitor<T> visitor){
		return visitor.visitAConst(this);
	}
}

//represents the addition of two expressions
class APlus extends AExp {
	
	AExp firstValue, secondValue;
	
	APlus(AExp f, AExp s) {
		this.firstValue = f;
		this.secondValue = s;
	}
	
	public boolean sameExp(AExp e) {
		return e.samePlus(this);
	}
	
	public boolean samePlus(APlus s) {
		return s.firstValue.sameExp(this.firstValue) &&
				s.secondValue.sameExp(this.secondValue);
	}
	
	public <T> T visit(IAExpVisitor<T> visitor){
		return visitor.visitAPlus(this);
	}
}

//represents the multiplication of two expressions
class ATimes extends AExp {
	
	AExp firstValue, secondValue;
	
	ATimes(AExp f, AExp s) {
		this.firstValue = f;
		this.secondValue = s;
	}
	
	public boolean sameExp(AExp e) {
		return e.sameTimes(this);
	}
	
	public boolean sameTimes(ATimes s) {
		return s.firstValue.sameExp(this.firstValue) &&
				s.secondValue.sameExp(this.secondValue);
	}
	
	public <T> T visit(IAExpVisitor<T> visitor){
		return visitor.visitATimes(this);
	}
}

//represents the subtraction of two expressions
//(the second field is subtracted from the first field)
class ASub extends AExp {
	
	AExp firstValue;
	AExp secondValue;
	
	ASub(AExp f, AExp s) {
		this.firstValue = f;
		this.secondValue = s;
	}
	
	public boolean sameExp(AExp exp) {
		return exp.sameSub(this);
	}
	
	public boolean sameSub(ASub s){
		return s.firstValue.sameExp(this.firstValue) &&
				s.secondValue.sameExp(this.secondValue);
	}
	
	public <T> T visit(IAExpVisitor<T> visitor){
		return visitor.visitASub(this);
	}
}

//-----------------------------------------------------------------------------
//Test Class
//-----------------------------------------------------------------------------

class Examples8_1 {
	
	AExp con1 = new AConst(1);
	AExp con2 = new AConst(2);
	AExp con3 = new AConst(3);
	
	AExp plus1 = new APlus(con1, con2);
	AExp plus2 = new APlus(con2, con3);
	
	AExp time1 = new ATimes(plus1, plus2);
	AExp time2 = new ATimes(plus2, con3);
	
	AExp sub1 = new ASub(con1, con2);
	AExp sub2 = new ASub (plus2, time1);
	
	AExp onePlusTwoTimesThree =
		    new APlus(new AConst(1), new ATimes(new AConst(2), new AConst(3)));
	
	Examples8_1 () {}
	
	boolean testSameExp(Tester t) {
		return t.checkExpect(con1.sameExp(con1), true) &&
				t.checkExpect(con1.sameExp(con2), false) &&
				t.checkExpect(plus1.sameExp(time1), false) &&
				t.checkExpect(time1.sameExp(time2), false) &&
				t.checkExpect(sub1.sameExp(sub1), true) &&
				t.checkExpect(sub1.sameExp(sub2), false) &&
				t.checkExpect(sub1.sameExp(time1), false);
	}
	
	boolean testEvalVisit(Tester t) {
		return t.checkExpect(con1.visit(new EvalVisitor()), 1) &&
				t.checkExpect(plus1.visit(new EvalVisitor()), 3) &&
				t.checkExpect(time1.visit(new EvalVisitor()), 15) &&
				t.checkExpect(sub1.visit(new EvalVisitor()), -1) &&
				t.checkExpect(onePlusTwoTimesThree.visit(new EvalVisitor()),
			              7);
	}
	
	boolean testPrintVisit(Tester t){
		return t.checkExpect(con1.visit(new PrintVisitor()), "1") &&
				t.checkExpect(plus1.visit(new PrintVisitor()), "(1 + 2)") &&
				t.checkExpect(time1.visit(new PrintVisitor()), "((1 + 2) * (2 + 3))") &&
				t.checkExpect(sub1.visit(new PrintVisitor()), "(1 - 2)") &&
				t.checkExpect(onePlusTwoTimesThree.visit(new PrintVisitor()),
		              "(1 + (2 * 3))");	             
	}
	
	boolean testMirrorVisit(Tester t) {
		return t.checkExpect(con1.visit(new MirrorVisitor()), con1) &&
				t.checkExpect(plus1.visit(new MirrorVisitor()).visit(new PrintVisitor()),
						"(2 + 1)") &&
				t.checkExpect(sub1.visit(new MirrorVisitor()).visit(new PrintVisitor()),
						"(2 - 1)") &&
				t.checkExpect(onePlusTwoTimesThree.visit(new MirrorVisitor()).visit(new PrintVisitor()),
						"((3 * 2) + 1)");
	}
}