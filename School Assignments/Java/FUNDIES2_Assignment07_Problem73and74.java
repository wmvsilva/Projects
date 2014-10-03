/* 
Nam Luu Nhat, William Silva
Professor Lerner
Fundamentals II (Honors)
28 February 2014

Assignment #7: Trie-d and True Java
*/

import tester.*;

//-----------------------------------------------------------------------------
//Problems 7.3+7.4
//Run Settings- 
//Main Class: tester.Main
//Arguments: ExampleToTest
//-----------------------------------------------------------------------------

/*******************************************************************************************/
// 7.3 Reprise: String dictionaries

interface IStringDict {
    // Returns true if the given key exists in this dictionary
	boolean hasKey(int key);

    // Returns the string associated with the given key, if present in this
    // dictionary, or throws an error if not found
	String lookup(int key);

    // Returns a new IStringDict which consists of all the mappings of this
    // current IStringDict, and with the given key mapped to the given string
	IStringDict set(int key, String value);
}

class LDEmpty implements IStringDict {

	LDEmpty() {}

	public boolean hasKey(int key) { return false; }

	public String lookup(int key)  { throw new RuntimeException("There's nothing to lookup here"); }

	public IStringDict set(int key, String value) {
		return new LDCons(key, value, this);
	}
}

class LDCons implements IStringDict {

	int key;
	String value;
	IStringDict rest;

	LDCons(int key, String value, IStringDict rest) {
		this.key = key;
		this.value = value;
		this.rest = rest;
	}

	public boolean hasKey(int key) {
		if (this.key == key) return true;
		else {
			return this.rest.hasKey(key);
		} 
	}

	public String lookup(int key) {
		if (this.key == key) return this.value;
		else {
			return this.rest.lookup(key);
		}
	} 

	public IStringDict set(int key, String value) {
		return new LDCons(key, value, this);
	}
}

/***********************************************************************************************/
// 7.4 Try and trie again: A New kind of dictionary

interface ILoNum {
	
	String searchHelperWordT9Dict(WordT9Dict dict);
	
	IT9DictData insertHelperWordT9Dict(WordT9Dict dict, String value);
	
	IT9DictData insertHelperMtT9Dict(EmptyT9Dict dict, String value);
	
	boolean containKeyHelperWordT9Dict(WordT9Dict dict);
	
}

class MtLoNum implements ILoNum {

	MtLoNum() {}
	
	public String searchHelperWordT9Dict(WordT9Dict dict) {
		return dict.completeWord();
	}
	
	public IT9DictData insertHelperWordT9Dict(WordT9Dict dict, String value) {
		if (dict.completeWord() == null) {
			dict.setCompleteWord(value);
		} else {
			String storedWord = dict.completeWord();
			if (storedWord.compareTo(value) < 0) {
				dict.digit0 = dict.digit0.insertWord(new MtLoNum(), value);
			} else {
				dict.setCompleteWord(value);
				dict.digit0 = dict.digit0.insertWord(new MtLoNum(), storedWord);
			}
		}
		return dict;
	}
	
	public IT9DictData insertHelperMtT9Dict(EmptyT9Dict dict, String value) {
		return new WordT9Dict(value);
	}
	
	public boolean containKeyHelperWordT9Dict(WordT9Dict dict) {
		return !(dict.completeWord() == null);
	}
}

class ConsLoNum implements ILoNum {

	int first;
	ILoNum rest;

	ConsLoNum(int first, ILoNum rest) {
		this.first = first;
		this.rest = rest;
	}
	
	public String searchHelperWordT9Dict(WordT9Dict dict) {
		IT9DictData data = dict.int2IT9Dict(this.first);
		return data.search(this.rest);
	}
	
	public IT9DictData insertHelperWordT9Dict(WordT9Dict dict, String value) {
		switch (this.first) {
			case 0: dict.digit0 = dict.digit0.insertWord(rest, value);
					break;
			case 2: dict.digit2 = dict.digit2.insertWord(rest, value);
					break;
			case 3: dict.digit3 = dict.digit3.insertWord(rest, value);
					break;
			case 4: dict.digit4 = dict.digit4.insertWord(rest, value);
			        break;
			case 5: dict.digit5 = dict.digit5.insertWord(rest, value);
					break;
			case 6: dict.digit6 = dict.digit6.insertWord(rest, value);
					break;
			case 7: dict.digit7 = dict.digit7.insertWord(rest, value);
					break;
			case 8: dict.digit8 = dict.digit8.insertWord(rest, value);
					break;
			case 9: dict.digit9 = dict.digit9.insertWord(rest, value);
			 		break;
			default: throw new RuntimeException("Invalid key");
		}
		return dict;			
	}
	
	public IT9DictData insertHelperMtT9Dict(EmptyT9Dict dict, String value) {
		return new WordT9Dict().insertWord(this, value);
	}
	
	public boolean containKeyHelperWordT9Dict(WordT9Dict dict) {
		IT9DictData data = dict.int2IT9Dict(this.first);
		if (data == null) { return false; }
		else 			  { return data.containKey(this.rest); }
	}
}

/**
 * Interface IT9DictData
 *
 */
interface IT9DictData {
    // Returns the complete word, if any, that has been spelled so far
	String completeWord();

    // Returns a new IT9Dict which contains all the same content as this
    // IT9Dict, and creates a new IT9Dict with the given word and inserts it
    // at the given digit in this IT9Dict
	IT9DictData insertWord(ILoNum keys, String value);
	
	// Return the value that is mapped to this key
	String search(ILoNum keys);
	
	// Return true if this contains the given key
	boolean containKey(ILoNum key);
}


class WordT9Dict implements IT9DictData {

	String completeWord;
	IT9DictData digit0, digit2, digit3;
	IT9DictData digit4, digit5, digit6;
	IT9DictData digit7, digit8, digit9;
	
	public WordT9Dict() {
		construct();
	}
	
	
    // Constructs a new dictionary containing the given word and nothing else
	public WordT9Dict(String word) {
		this.completeWord = word;
		construct();
	}
	
	private void construct() {
		digit0 = new EmptyT9Dict();
		digit2 = new EmptyT9Dict();
		digit3 = new EmptyT9Dict();
		digit4 = new EmptyT9Dict();
		digit5 = new EmptyT9Dict();
		digit6 = new EmptyT9Dict();
		digit7 = new EmptyT9Dict();
		digit8 = new EmptyT9Dict();
		digit9 = new EmptyT9Dict();
	}

	public String completeWord() {
		return this.completeWord;
	}
	
	public void setCompleteWord(String word) {
		this.completeWord = word;
	}
	
	IT9DictData int2IT9Dict(int k) {
		if (k == 0) { return this.digit0; }
		if (k == 2) { return this.digit2; }
		if (k == 3) { return this.digit3; }
		if (k == 4) { return this.digit4; }
		if (k == 5) { return this.digit5; }
		if (k == 6) { return this.digit6; }
		if (k == 7) { return this.digit7; }
		if (k == 8) { return this.digit8; }
		if (k == 9) { return this.digit9; }
		return null;
	}
	
	public String search(ILoNum keys) {
		return keys.searchHelperWordT9Dict(this);
	}
	
	public IT9DictData insertWord(ILoNum keys, String value) {
		return keys.insertHelperWordT9Dict(this, value);
	}
	
	public boolean containKey(ILoNum keys) {
		return keys.containKeyHelperWordT9Dict(this);
	}
}

class EmptyT9Dict implements IT9DictData { 
	
	EmptyT9Dict() {}
	
	public String search(ILoNum k) {
		throw new RuntimeException("Key doesn't exist");
	}
	
	public String completeWord() {
		return null;
	}
	
	public IT9DictData insertWord(ILoNum keys, String value) {
		return keys.insertHelperMtT9Dict(this, value);
	}
	
	public boolean containKey(ILoNum keys) {
		return false;
	}
}


class T9Dict implements IStringDict {
    
    // The actual contents of the dictionary.  Your IStringDict methods should 
    // use this field somehow
	private IT9DictData contents;
	
	T9Dict() {
		contents = new WordT9Dict();
	}
	
	T9Dict(IT9DictData dict) {
		this.contents = dict;
	}
	
	ILoNum toList(int num) {
		// Convert the given number to a list of number
		return this.toListHelp(num, new MtLoNum());
	}

	ILoNum toListHelp(int num, ILoNum leadingDigits) {
		if (num == 0) {
			return leadingDigits; // We're done
		} else {
			int ones = num % 10; // Computes num modulo 10, i.e. the units digit
			int rest = num / 10; // Does integer division, which truncates
			return this.toListHelp(rest, new ConsLoNum(ones, leadingDigits));
		}
	}

	public boolean hasKey(int key) {
		return this.contents.containKey(this.toList(key));
	}

	public String lookup(int key) {
		return this.contents.search(this.toList(key));
	}
	
	public IStringDict set(int key, String value) {
		contents = this.contents.insertWord(this.toList(key), value);
		return this;
	}
}


/*********************************************************************************
 * Examples and tests
 *********************************************************************************/

class ExampleToTest {
	// Examples and tests for problem 7.2
	IStringDict mt = new LDEmpty();

	IStringDict dict1 = new LDCons(2, "a", mt);
	IStringDict dict2 = new LDCons(3, "b", dict1);
	IStringDict dict3 = new LDCons(4, "c", dict2);

	boolean testHasKey(Tester t) {
		return t.checkExpect(dict1.hasKey(5), false) &&
			   t.checkExpect(dict2.hasKey(3), true);
	}

	boolean testLookUp(Tester t) {
		return t.checkExpect(dict1.lookup(2), "a") &&
			   t.checkExpect(dict2.lookup(3), "b");
	}
	
	// Examples and tests for Dictionary
	IStringDict theDict = new T9Dict().set(43356, "HELLO").set(2276, "BARN").set(4653, "HOLE").set(4335, "HELL");
	IStringDict mtDict = new T9Dict();
	IStringDict theDict2 = mtDict.set(4663, "IONE").set(4663, "HOME").set(4663, "GONE").set(4663, "GOOD");
	
	void testLookup(Tester t) {
		t.checkExpect(theDict.lookup(43356), "HELLO");
		t.checkExpect(theDict.lookup(2276),  "BARN");
		t.checkExpect(theDict.lookup(4653),  "HOLE");
		t.checkExpect(theDict.lookup(4335),  "HELL");
		t.checkExpect(theDict2.lookup(4663),  "GONE");
		t.checkExpect(theDict2.lookup(46630), "GOOD");
		t.checkExpect(theDict2.lookup(466300), "HOME");
		t.checkExpect(theDict2.lookup(4663000), "IONE");
	}
	
	void testHasKeyDict(Tester t) {
		t.checkExpect(theDict.hasKey(43356), true);
		t.checkExpect(theDict.hasKey(4335), true);
		t.checkExpect(theDict.hasKey(1234), false);
		t.checkExpect(theDict.hasKey(43), false);
		t.checkExpect(theDict.hasKey(4), false);
	}
}