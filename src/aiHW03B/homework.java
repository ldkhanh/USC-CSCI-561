package aiHW03B;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class homework {
	
public static void main(String[] args) {
		KM52a run = new KM52a();
		run.input("input.txt");
		run.Resolution();
	}
}

class KM52a {
	private  List<String> querys;
	private  List<String> clauses;
	private  List<String> allSt;
	
	public KM52a(){
		querys = new ArrayList<String>();
		clauses = new ArrayList<String>();
		allSt = new ArrayList<String>();
		
	}
	
	public  void Resolution() {
		StringBuilder sb = new StringBuilder();
		Domain domain = new Domain();
		Lexer lexer = new Lexer();
		for (int i = 0; i < allSt.size(); i++) {
			System.out.println(allSt.get(i));
			lexer.setIn(allSt.get(i));
			Token t = lexer.extractDomain();
			while (!t.getText().equals("EOI")) {
				t = lexer.extractDomain();
				if (t.getType() == 8) {
					System.out.println("Constant: " + t.getText());
					domain.setConstant(t.getText());
				} else if (t.getType() == 6) {
					System.out.println("PREDICATE: " + t.getText());
					domain.setPredicate(t.getText());
				} 
			}
		}

		KB kb = new KB(domain);
		for (String c : clauses) {
			kb.tell(c);
		}
		System.out.println("Knowledge Base:");
		System.out.println(kb.toString());
		for (int i = 0; i < querys.size(); i++) {
			ResolutionResult answer = kb.ask(querys.get(i));
			System.out.print(querys.get(i));
			if (answer.isTrue()) {
				System.out.println("   This query is True");
				if (i != querys.size()-1)
					sb.append("TRUE"+"\n");
				else 
					sb.append("TRUE");
			} else {
				System.out.println("   This query is False");
				if (i != querys.size()-1)
					sb.append("FALSE"+"\n");
				else 
					sb.append("FALSE");
			}
		}
		output(sb.toString());
		
	}

	public void input(String fileName) {
		BufferedReader br = null;
		try {
			int nQuery, nClause;
			String line;
			br = new BufferedReader(new FileReader(fileName));
			// Get Query
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				nQuery = Integer.parseInt(line);
			else
				return;
			for (int i = 0; i < nQuery; i++) {
				line = br.readLine();
				querys.add(line);
				if (line.charAt(0) != '(')
					allSt.add("("+line+")");
				else 
					allSt.add(line);
			}
			// Get KB
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				nClause = Integer.parseInt(line);
			else
				return;

			for (int i = 0; i < nClause; i++) {
				line = br.readLine();
				clauses.add(line);
				if (line.charAt(0) != '(')
					allSt.add("("+line+")");
				else 
					allSt.add(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void output(String out) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			writer.write(out);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}



interface AccessObject {
	public Object accessConstant(Constant cons, Object arg);
	public Object accessVariable(Variable var, Object arg);
	public Object accessPredicate(Predicate p, Object arg);
	public Object accessConnectSentence(ConnectSentence st, Object arg);
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg);
}
interface Node {
	String getName();
	List<? extends Node> getArguments();
	Object take(AccessObject v, Object arg);
	boolean checkCompoundSen();
	Node copy();
}
interface Sentence extends Node {
	Sentence copy();
}
interface AtomicSentence extends Sentence {
	AtomicSentence copy();
	List<Terms> getArguments();
}
interface Terms extends Node {
	Terms copy();
	List<Terms> getArguments();
}
class Variable implements Terms {
	private String val;
	private int hashCode = 0;
	private int index = -1;
	
	public Variable(String str) {
		this.val = str.trim();
	}
	public Variable(String str, int id) {
		this.val = str.trim();
		this.index = id;
	}
	
	public String getVal() {
		return this.val;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int id) {
		this.index = id;
		this.hashCode = 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.val;
	}

	@Override
	public Object take(AccessObject v, Object arg) {
		// TODO Auto-generated method stub
		return v.accessVariable(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Variable copy() {
		// TODO Auto-generated method stub
		return new Variable(val, index);
	}

	@Override
	public List<Terms> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : ((this.index+13)*21 + val.hashCode()); 
	}
	
	@Override
	public String toString() {
		return this.val;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof Variable)) {
			return false;
		}

		Variable v = (Variable) o;
		return v.getVal().equals(getVal())
				&& v.getIndex() == getIndex();
	}
}
class Constant implements Terms {
	private String val;
	private int hashCode = 0;
	
	public Constant(String str) {
		this.val = str;
	}

	public String getVal() {
		return this.val;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.val;
	}

	@Override
	public Object take(AccessObject v, Object arg) {
		// TODO Auto-generated method stub
		return v.accessConstant(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Constant copy() {
		// TODO Auto-generated method stub
		return new Constant(val);
	}

	@Override
	public List<Terms> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (val.hashCode() + 21*13); 
	}

	@Override
	public String toString() {
		return this.val;
	}
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof Constant)) {
			return false;
		}
		Constant c = (Constant) o;
		return c.getVal().equals(getVal());

	}
}
class Predicate implements AtomicSentence {
	private String name;
	private List<Terms> terms = new ArrayList<Terms>();
	private String strRepresentation = null;
	private int hashCode = 0;
	
	public Predicate(String preName, List<Terms> terms) {
		this.name = preName;
		this.terms.addAll(terms);
	}
	
	public String getPreName() {
		return this.name;
	}
	
	public List<Terms> getTerms() {
		return Collections.unmodifiableList(terms);
	}
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public Object take(AccessObject p, Object arg) {
		// TODO Auto-generated method stub
		return p.accessPredicate(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Predicate copy() {
		// TODO Auto-generated method stub
		List<Terms> copyT = new ArrayList<Terms>();
		copyT.addAll(this.terms);
		return new Predicate(this.name, copyT);
	}

	@Override
	public List<Terms> getArguments() {
		// TODO Auto-generated method stub
		return getTerms();
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (name.hashCode() + 21*(15+terms.hashCode())); 
	}
	
	@Override
	public String toString() {
		if (this.strRepresentation == null) {
			StringBuilder st = new StringBuilder();
			st.append(this.name);
			st.append("(");
			boolean c = true;
			for (Terms term: this.terms) {
				if (c)
					c = false;
				else 
					st.append(",");
				st.append(term.toString());
			}
			st.append(")");
			this.strRepresentation = st.toString();
		}
		return this.strRepresentation;
	}

}
class ConnectSentence implements Sentence {
	private String connector;
	private Sentence st1, st2;
	private List<Sentence> arguments = new ArrayList<Sentence>();
	private int hashCode = 0;
	private String strP = null;

	
	public ConnectSentence(String con, Sentence st1, Sentence st2) {
		this.connector = con;
		this.st1 = st1;
		this.st2 = st2;
		this.arguments.add(st1);
		this.arguments.add(st2);
	}
	
	public String getConnector() {
		return this.connector;
	}
	
	public Sentence getFirstST() {
		return this.st1;
	}
	
	public Sentence getSecondST() {
		return this.st2;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.connector;
	}

	@Override
	public List<Sentence> getArguments() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(this.arguments);
	}

	@Override
	public Object take(AccessObject st, Object arg) {
		// TODO Auto-generated method stub
		return st.accessConnectSentence(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ConnectSentence copy() {
		// TODO Auto-generated method stub
		return new ConnectSentence(this.connector, st1.copy(), st2.copy());
	}

	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (this.connector.hashCode() + 21*((15+this.st1.hashCode())*21 + this.st2.hashCode())); 
	}
	
	@Override
	public String toString() {
		if (null == strP) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(st1.toString());
			sb.append(" ");
			sb.append(connector);
			sb.append(" ");
			sb.append(st2.toString());
			sb.append(")");
			strP = sb.toString();
		}
		return strP;
	}
}
class NegatedSentence implements Sentence {
	private Sentence negatedST;
	private List<Sentence> arguments = new ArrayList<Sentence>();
	private int hashCode = 0;
	private String strP = null;
	
	public NegatedSentence(Sentence negST) {
		this.negatedST = negST;
		arguments.add(negST);
	}

	public Sentence getNegatedST() {
		return this.negatedST;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "~";
	}

	@Override
	public List<Sentence> getArguments() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(this.arguments);
	}

	@Override
	public Object take(AccessObject nST, Object arg) {
		// TODO Auto-generated method stub
		return nST.accessNegatedSentence(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Sentence copy() {
		// TODO Auto-generated method stub
		return new NegatedSentence(this.negatedST.copy());
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (this.negatedST.hashCode() + 21*18); 
	}

	@Override
	public String toString() {
		if (null == strP) {
			StringBuilder sb = new StringBuilder();
			sb.append("~(");
			sb.append(negatedST.toString());
			sb.append(")");
			strP = sb.toString();
		}
		return strP;
	}
}
class Domain {
	private Set<String> constants, predicates;
	private int ansLIndex = 0;
	
	public Domain() {
		this.constants = new HashSet<String>();
		this.predicates = new HashSet<String>();
	}
	
	public Domain(Domain copyD) {
		this(copyD.getConstants(), copyD.getPredicates());
		
	}
	
	public Domain(Set<String> cons, Set<String> pred) {
		this.constants = new HashSet<String>(cons);
		this.predicates = new HashSet<String>(pred);
	}
	
	public Set<String> getConstants() {
		return this.constants;
	}
	
	public void setConstant(String constant) {
		this.constants.add(constant);
	}
	
	public Set<String> getPredicates() {
		return this.predicates;
	}
	
	public void setPredicate(String predicate) {
		this.predicates.add(predicate);
	}
	
	public String setAnsLiteral() {
		String al = null;
		do {
			al = "Answer" + (ansLIndex++);
		} while (constants.contains(al) || predicates.contains(al));
		this.setPredicate(al);
		return al;
	}

}
class Token {
	private int type;
	private String txt;
	private int startPosition;
	
	public Token(int type, String text, int position) {
		this.type = type;
		this.txt = text;
		this.startPosition = position;
	}
	
	public String getText() {
		return this.txt;
	}
	
	public int getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		Token tk = (Token) o;
		return ((tk.type == type) && (tk.txt.equals(this.txt)) && (tk.startPosition == this.startPosition));
	}
	
	@Override
	public int hashCode() {
		return this.startPosition + 21*((19+this.type)*21 + this.txt.hashCode()); 
	}
}

interface TokenTypes {
	static final int SYMBOL = 1;

	static final int LPAREN = 2; 

	static final int RPAREN = 3; 

	static final int COMMA = 4;

	static final int CONNECTIVE = 5;

	static final int PREDICATE = 6;

	static final int VARIABLE = 7;

	static final int CONSTANT = 8;

	static final int WHITESPACE = 10000;

	static final int EOI = 9999;
}

class Lexer {
	private Domain domain;
	private Set<String> connectors;

	protected int bSize = 1;
	private Reader input;
	private int[] buffer;
	private int currPosition;

	public Lexer() {
		connectors = new HashSet<String>();
		connectors.add("~");
		connectors.add("&");
		connectors.add("|");
		connectors.add("=>");
	}

	public Lexer(Domain domain) {
		this.domain = domain;
		connectors = new HashSet<String>();
		connectors.add("~");
		connectors.add("&");
		connectors.add("|");
		connectors.add("=>");
	}

	public Domain getDomain() {
		return this.domain;
	}

	public Token nextTk() {
		int position = this.currPosition;
		if (getNextChar(1) == '(') {
			loadChar();
			return new Token(TokenTypes.LPAREN, "(", position);
		} else if (getNextChar(1) == ')') {
			loadChar();
			return new Token(TokenTypes.RPAREN, ")", position);
		} else if (getNextChar(1) == ',') {
			loadChar();
			return new Token(TokenTypes.COMMA, ",", position);
		} else if (getNextChar(1) == '~') {
			loadChar();
			return new Token(TokenTypes.CONNECTIVE, "~", position);
		} else if (checkIdentify()) {
			return identify();
		} else if (Character.isWhitespace(getNextChar(1))) {
			loadChar();
			return nextTk();
		} else if (getNextChar(1) == (char) -1) {
			return new Token(TokenTypes.EOI, "EOI", position);
		} else {
			throw new RuntimeException(
					"K1: Lexing error on character " + getNextChar(1) + " at position " + this.currPosition);
		}
	}

	public void setIn(String str) {
		setIn(new StringReader(str));
	}

	public void setIn(Reader input) {
		this.input = input;
		this.buffer = new int[this.bSize];
		currPosition = 0;
		initializeB();
	}

	private int readIn() {
		int read = -1;
		try {
			read = this.input.read();

		} catch (IOException ie) {
			System.out.println(ie.toString());
		}
		return read;
	}

	private void initializeB() {
		for (int i = 0; i < this.bSize; i++) {
			buffer[i] = -1;
		}
		for (int i = 0; i < bSize; i++) {
			buffer[i] = readIn();
			if (buffer[i] == -1) {
				break;
			}
		}
	}

	private char getNextChar(int position) {
		return (char) buffer[position - 1];
	}

	// Should combine 2 methods bellow into one
	private void loadChar() {
		this.currPosition++;
		loadNext();
	}

	private void loadNext() {
		boolean count = false;
		for (int i = 0; i < this.bSize - 1; i++) {
			buffer[i] = buffer[i + 1];
			if (buffer[i] == -1) {
				count = true;
				break;
			}
		}
		if (!count) {
			buffer[this.bSize - 1] = readIn();
		}
	}

	private Token identify() {
		int position = this.currPosition;
		StringBuffer sbuf = new StringBuffer();
		while (checkIdentify()) {
			sbuf.append(getNextChar(1));
			loadChar();
		}
		String readString = new String(sbuf);
		if (connectors.contains(readString)) {
			return new Token(TokenTypes.CONNECTIVE, readString, position);
		} else if (domain.getPredicates().contains(readString)) {
			return new Token(TokenTypes.PREDICATE, readString, position);
		} else if (domain.getConstants().contains(readString)) {
			return new Token(TokenTypes.CONSTANT, readString, position);
		} else if (Character.isLowerCase(readString.charAt(0))) { // check
																	// variable
			return new Token(TokenTypes.VARIABLE, readString, position);
		} else {
			throw new RuntimeException(
					"K2: Lexing error on character " + getNextChar(1) + " at position " + this.currPosition);
		}
	}

	private boolean checkIdentify() {
		return (Character.isJavaIdentifierStart(getNextChar(1))) || (getNextChar(1) == '=') || (getNextChar(1) == '<')
				|| (getNextChar(1) == '>') || (getNextChar(1) == '~') || (getNextChar(1) == '&')
				|| (getNextChar(1) == '|');
	}

	public Token extractDomain() {
		int position = this.currPosition;
		if (getNextChar(1) == '(') {
			loadChar();
			return new Token(TokenTypes.LPAREN, "(", position);
		} else if (getNextChar(1) == ')') {
			loadChar();
			return new Token(TokenTypes.RPAREN, ")", position);
		} else if (getNextChar(1) == ',') {
			loadChar();
			return new Token(TokenTypes.COMMA, ",", position);
		} else if (getNextChar(1) == '~') {
			loadChar();
			return new Token(TokenTypes.CONNECTIVE, "~", position);
		} else if (checkIdentify()) {
			return extract();
		} else if (Character.isWhitespace(getNextChar(1))) {
			loadChar();
			return extractDomain();
		} else if (getNextChar(1) == (char) -1) {
			return new Token(TokenTypes.EOI, "EOI", position);
		} else {
			throw new RuntimeException(
					"K3: Lexing error on character " + getNextChar(1) + " at position " + this.currPosition);
		}
	}
	

	private Token extract() {
		int position = this.currPosition;
		StringBuffer sbuf = new StringBuffer();
		while (checkIdentify()) {
			sbuf.append(getNextChar(1));
			loadChar();
		}
		String readString = new String(sbuf);
		if (connectors.contains(readString)) {
			return new Token(TokenTypes.CONNECTIVE, readString, position);
		} else if (Character.isLowerCase(readString.charAt(0))) { 																// variable
			return new Token(TokenTypes.VARIABLE, readString, position);
		} else if (!readString.isEmpty()) {
			if (getNextChar(1) == '(') {
				return new Token(TokenTypes.PREDICATE, readString, position);
			} else {
				return new Token(TokenTypes.CONSTANT, readString, position);
			}
		} else {
			throw new RuntimeException(
					"K4: Lexing error on character " + getNextChar(1) + " at position " + this.currPosition);
		}
	}
}
class Parser {
	private Lexer lexer;
	protected Token[] buffer;
	protected int n = 1;
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
		buffer = new Token[n];
	}
	
	public Parser(Domain domain) {
		this(new Lexer(domain));
	}
	
	public Domain getDomain() {
		return lexer.getDomain();
	}
	
	public Sentence parse(String s) {
		buffer = new Token[1];
		lexer.setIn(s);
		for (int i = 0; i < n; i++) {
			buffer[i] = lexer.nextTk();
		}
		return parseResult();
	}
	
	protected Sentence parseResult() {
		Token t = buffer[0];
		if (t.getType() == TokenTypes.LPAREN) {
			return parseParent();
		} else if ((t.getType() == TokenTypes.CONNECTIVE) && (t.getText().equals("~"))) {
			return parseNegSentence();
		} else if (t.getType() == TokenTypes.PREDICATE) {
			return parsePredicate();
		}
		throw new RuntimeException("parse failed with Token " + t.getText());
	}
	
	private Sentence parseParent() {
		match("(");
		Sentence sent = parseResult();
		while ((buffer[0].getType() == TokenTypes.CONNECTIVE) && (!(buffer[0].getText().equals("~")))) {
			String connector = buffer[0].getText();
			loadNextToken();
			Sentence other = parseResult();
			sent = new ConnectSentence(connector, sent, other);
		}
		match(")");
		return sent;
	}
	
	private Sentence parseNegSentence() {
		match("~");
		return new NegatedSentence(parseResult());
	}
	
	private Sentence parsePredicate() {
		Token t = buffer[0];
		String preName = t.getText();
		List<Terms> terms = processTerms();
		return new Predicate(preName, terms);
		
	}
	
	private List<Terms> processTerms() {
		// TODO Auto-generated method stub
		loadNextToken();
		List<Terms> terms = new ArrayList<Terms>();
		match("(");
		Terms term = parseTerm();
		terms.add(term);
		while (buffer[0].getType() == TokenTypes.COMMA) {
			match(",");
			term = parseTerm();
			terms.add(term);
		}
		match(")");
		return terms;
	}
	private Terms parseTerm() {
		Token t = buffer[0];
		int tokenType = t.getType();
		if (tokenType == TokenTypes.CONSTANT) {
			return parseConstant();
		} else if (tokenType == TokenTypes.VARIABLE) {
			return parseVariable();
		} else {
			return null;
		}
	}
	private Terms parseConstant() {
		Token t = buffer[0];
		String value = t.getText();
		loadNextToken();
		return new Constant(value);
	}
	
	private Terms parseVariable() {
		Token t = buffer[0];
		String value = t.getText();
		loadNextToken();
		return new Variable(value);
	}
	

	protected void match(String st) {
		if (buffer[0].getText().equals(st)) {
			loadNextToken();
		} else {
			throw new RuntimeException("Syntax error detected at match. Expected ");
		}
	}
	
	protected void loadNextToken() {
		boolean endOI = false;
		for (int i = 0; i < n - 1; i++) {
			buffer[i] = buffer[i+1];
			if (buffer[i].getType() == TokenTypes.EOI) {
				endOI = true;
				break;
			}
		}
		if (!endOI) {
			try {
				buffer[n-1] = lexer.nextTk();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
class Literal {
	
	private AtomicSentence atomS = null;
	private boolean negated = false;
	private int hashCode = 0;
	
	public Literal(AtomicSentence atomS) {
		this.atomS = atomS;
	}
	public Literal(AtomicSentence atomS, boolean negated) {
		this.atomS = atomS;
		this.negated = negated;
	}
	
	public Literal getInstance(AtomicSentence atomS) {
		return new Literal(atomS, this.negated);
	}
	
	public boolean isPositive() {
		return !this.negated;
	}
	
	public boolean isNegative() {
		return this.negated;
	}
	
	public AtomicSentence getAtomS() {
		return this.atomS;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o.getClass() != this.getClass()) return false;
		if (!(o instanceof Literal)) return false;
		Literal lit = (Literal) o;
		return lit.isPositive() == this.isPositive() 
				&& lit.getAtomS().getName().equals(this.atomS.getName()) 
				&& lit.getAtomS().getArguments().equals(this.atomS.getArguments());
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (this.getClass().getSimpleName().hashCode() + 21*((25+this.atomS.getName().hashCode())*21 + this.atomS.getArguments().hashCode())); 
	}
}
class LiteralsSorter implements Comparator<Literal> {
	public int compare(Literal l1, Literal l2) {
		int value = 0;
		if (l1.isPositive() != l2.isPositive()) {
			return ((l1.isPositive()) ? 1: -1);
		}
		value = l1.getAtomS().getName().compareTo(l2.getAtomS().getName());
		if (value == 0) {
			value = compareArgs(l1.getAtomS().getArguments(), l2.getAtomS().getArguments());
		}
		
		return value;
	}
	private int compareArgs(List<Terms> arg1, List<Terms> arg2) {
		int value= arg1.size() - arg2.size();
		
		if (value == 0 && arg1.size() > 0) {
			Terms term1 = arg1.get(0);
			Terms term2 = arg2.get(0);
			
			if (term1.getClass() == term2.getClass()){
				if (term1 instanceof Constant){
					value = term1.getName().compareTo(term2.getName());
				}
				if (value == 0) {
					value = compareArgs(arg1.subList(1, arg1.size()), arg2.subList(1, arg2.size()));
				}
			} else {
				if (term1 instanceof Constant) {
					value = 1;
				} else if (term2 instanceof Constant) {
					value = -1;
				} else {
					value = -1;
				}
			}
		}
		return value;
		
	}

}
class Clause {
	private static Standardization stIndex = StandardGenerate.newId('c');
	private static Unifier unifier = new Unifier();
	private static Substitution substA = new Substitution();
	private static VarCollect varCollect = new VarCollect();
	private static Standardize standardize = new Standardize();
	private static LiteralsSorter litSort = new LiteralsSorter();
	private final Set<Literal> literals = new LinkedHashSet<Literal>();
	private final List<Literal> positiveLit = new ArrayList<Literal>();
	private final List<Literal> negativeLit = new ArrayList<Literal>();
	private boolean immutable = false;
	private boolean saCheckRequired = true;
	private String equalityIdentity = "";
	private Set<Clause> factors = null;
	private Set<Clause> nonTrivialFactors = null;
	
	public Clause() {}
	
	public Clause(List<Literal> lits) {
		this.literals.addAll(lits);
		for (Literal l: literals) {
			if (l.isPositive()){
				this.positiveLit.add(l);
			} else {
				this.negativeLit.add(l);
			}
		}
		recalculateIdentity();		// check to delete
	}
	
	public Clause(List<Literal> lits1, List<Literal> lits2) {
		literals.addAll(lits1);
		literals.addAll(lits2);
		for (Literal l: literals) {
			if (l.isPositive()) {
				this.positiveLit.add(l);
			} else {
				this.negativeLit.add(l);
			}
		}
		recalculateIdentity();
	}
	
	public void addLiteral(Literal literal) {
		if (isImmutable()) {
			throw new IllegalStateException("Clause is immutable, cannot updated");
		} 
		int sizeO = literals.size();
		literals.add(literal);
		if (literals.size() > sizeO) {
			if (literal.isPositive()) {
				this.positiveLit.add(literal);
			} else {
				this.negativeLit.add(literal);
			}
		}
		recalculateIdentity();		
 	}
	public void addPosLiteral(AtomicSentence atom) {
		addLiteral(new Literal(atom));
	}
	public void addNegLiteral(AtomicSentence atom) {
		addLiteral(new Literal(atom, true));
	}
	public Set<Literal> getLiterals() {
		return Collections.unmodifiableSet(literals);
	}
	public List<Literal> getPosLiterals() {
		return Collections.unmodifiableList(this.positiveLit);
	}
	public int getNumLiterals() {
		return literals.size();
	}
	public int getNumPosLiterals() {
		return this.positiveLit.size();
	}
	public int getNumNegLiterals() {
		return this.negativeLit.size();
	}
	public Set<Clause> getFactors() {
		if (this.factors == null) {
			calculateFactors(null);
		}
		return Collections.unmodifiableSet(this.factors);
	}
	
	public boolean isImmutable() {
		return this.immutable;
	}
	public void setImmutable() {
		this.immutable = true;
	}
	public boolean isEmpty() {
		return this.literals.size() == 0;
	}
	public boolean isStandardizeRequired() {
		return this.saCheckRequired;
	}
	public void setStandardizeNotRequired() {
		this.saCheckRequired =false;
	}
	public boolean isUnitClause() {
		return this.literals.size() == 1;
	}
	public boolean isDefiniteClause() {
		return !isEmpty() && positiveLit.size() == 1;
	}
	public boolean isImplicationDefClause() {
		return this.isDefiniteClause() && this.negativeLit.size() >= 1;
	}
	
	public Set<Clause> binaryResol(Clause other) {
		Set<Clause> resol = new LinkedHashSet<Clause>();
		if (this.isEmpty() && other.isEmpty()) {
			resol.add(new Clause());
			return resol;
		}
		other = checkStandardized(other);
		
		List<Literal> allPosLits = new ArrayList<Literal>();
		List<Literal> allNegLits = new ArrayList<Literal>();
		allPosLits.addAll(this.positiveLit);
		allPosLits.addAll(other.positiveLit);
		allNegLits.addAll(this.negativeLit);
		allNegLits.addAll(other.negativeLit);
		
		List<Literal> oPosLits = new ArrayList<Literal>();
		List<Literal> oNegLits = new ArrayList<Literal>();
		List<Literal> copyRPosLits = new ArrayList<Literal>();
		List<Literal> copyRNegLits = new ArrayList<Literal>();
		
		for (int i = 0; i < 2; i++) {
			oPosLits.clear();
			oNegLits.clear();
			if (i == 0) {
				oPosLits.addAll(this.positiveLit);
				oNegLits.addAll(other.negativeLit);
			} else {
				oPosLits.addAll(other.positiveLit);
				oNegLits.addAll(this.negativeLit);
			}
			Map<Variable, Terms> copyTheta = new LinkedHashMap<Variable, Terms>();
			for (Literal pl: oPosLits) {
				for (Literal nl: oNegLits) {
					copyTheta.clear();
					if (unifier.unify(pl.getAtomS(), nl.getAtomS(), copyTheta) != null){
						copyRPosLits.clear();
						copyRNegLits.clear();
						boolean found = false;
						for (Literal l: allPosLits) {
							if (!found && pl.equals(l)) {
								found = true;
								continue;
							}
							copyRPosLits.add(substA.subst(copyTheta, l));
						}
						found = false;
						for (Literal l: allNegLits) {
							if (!found && nl.equals(l)) {
								found = true;
								continue;
							}
							copyRNegLits.add(substA.subst(copyTheta, l));
						}
						Clause c = new Clause(copyRPosLits, copyRNegLits);
						if (isImmutable()){
							c.setImmutable();
						}
						if (!isStandardizeRequired()){
							c.setStandardizeNotRequired();
						}
						resol.add(c);
					}
				}
			}
		}
		return resol;
	}
	
	private void recalculateIdentity() {
		List<Literal> sortedLiterals = new ArrayList<Literal>(literals);
		Collections.sort(sortedLiterals, litSort);
		
		ClauseEqIdentity ceic = new ClauseEqIdentity(sortedLiterals, litSort);
		equalityIdentity = ceic.getIdentity();
		factors = null;
		nonTrivialFactors = null;
	}
	
	@Override
	public int hashCode() {
		return equalityIdentity.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (null == other) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (!(other instanceof Clause)) {
			return false;
		}
		Clause othClause = (Clause) other;

		return equalityIdentity.equals(othClause.equalityIdentity);
	}
	
	private Clause checkStandardized(Clause other) {
		if (this.isStandardizeRequired() || this == other) {
			Set<Variable> mVar = varCollect.collectVars(this);
			Set<Variable> oVar = varCollect.collectVars(other);
			
			Set<Variable> cVar = new HashSet<Variable>();
			cVar.addAll(mVar);
			cVar.addAll(oVar);
			if (cVar.size() < (mVar.size() + oVar.size())) {
				other = standardize.standardizeA(other, stIndex);
			}
		}
		return other;
	}
	
	private void calculateFactors(Set<Clause> pFactors) {
		this.nonTrivialFactors = new LinkedHashSet<Clause>();
		Map<Variable, Terms> theta = new HashMap<Variable, Terms>();
		List<Literal> lits = new ArrayList<Literal>();
		
		for (int i = 0; i < 2; i++) {
			lits.clear();
			if (i == 0) {
				lits.addAll(this.positiveLit);
			} else {
				lits.addAll(this.negativeLit);
			}
			for (int x = 0; x < lits.size(); x++) {
				for (int y = x + 1; y < lits.size(); y++) {
					Literal litX = lits.get(x);
					Literal litY = lits.get(y);
					theta.clear();
					Map<Variable, Terms> subst = unifier.unify(litX.getAtomS(), litY.getAtomS(), theta);
					if (subst != null) {
						List<Literal> posLits = new ArrayList<Literal>();
						List<Literal> negLits = new ArrayList<Literal>();
						if (i == 0) {
							posLits.add(substA.subst(subst, litX));
						} else {
							negLits.add(substA.subst(subst, litX));
						}
						for (Literal pl: positiveLit) {
							if (pl == litX || pl == litY) {
								continue;
							}
							posLits.add(substA.subst(subst, pl));
						}
						for (Literal nl: negativeLit) {
							if (nl == litX || nl == litY) {
								continue;
							}
							negLits.add(substA.subst(subst, nl));
						}
						Clause c = new Clause(posLits, negLits);
						if (isImmutable()) {
							c.setImmutable();
						}
						if (!isStandardizeRequired()) {
							c.setStandardizeNotRequired();
						}
						if (pFactors == null) {
							c.calculateFactors(this.nonTrivialFactors);
							this.nonTrivialFactors.addAll(c.getFactors());
						} else {
							if (!pFactors.contains(c)) {
								c.calculateFactors(this.nonTrivialFactors);
								this.nonTrivialFactors.addAll(c.getFactors());
							}
						}
					}
				}
			}
		}
		factors = new LinkedHashSet<Clause>();
		factors.add(this);
		factors.addAll(this.nonTrivialFactors);	
	}
}
class ClauseEqIdentity implements AccessObject {
	private StringBuilder idStr = new StringBuilder();
	private int noVarPos = 0;
	private int[] clauseVar = null;
	private int currLiteral = 0;
	private Map<String, List<Integer>> varPos = new HashMap<String, List<Integer>>();

	public ClauseEqIdentity(List<Literal> lits,LiteralsSorter sorter) {

		clauseVar = new int[lits.size()];
		for (Literal l : lits) {
			if (l.isNegative()) {
				idStr.append("~");
			}
			idStr.append(l.getAtomS().getName());
			idStr.append("(");
			boolean firstTerm = true;
			for (Terms t : l.getAtomS().getArguments()) {
				if (firstTerm) {
					firstTerm = false;
				} else {
					idStr.append(",");
				}
				t.take(this, null);
			}
			idStr.append(")");
			currLiteral++;
		}
		int min, max;
		min = max = 0;
		for (int i = 0; i < lits.size(); i++) {
			int incITo = i;
			int next = i + 1;
			max += clauseVar[i];
			while (next < lits.size()) {
				if (0 != sorter.compare(lits.get(i), lits.get(next))) {
					break;
				}
				max += clauseVar[next];
				incITo = next; 
				next++;
			}
			if ((next - i) > 1) {
				for (String key : varPos.keySet()) {
					List<Integer> positions = varPos.get(key);
					List<Integer> addPos = new ArrayList<Integer>();
					for (int pos : positions) {
						if (pos >= min && pos < max) {
							int pPos = pos;
							int nPos = pos;
							for (int slot = i; slot < (next - 1); slot++) {
								pPos += clauseVar[i];
								if (pPos >= min && pPos < max) {
									if (!positions.contains(pPos)
											&& !addPos.contains(pPos)) {
										addPos.add(pPos);
									}
								}
								nPos -= clauseVar[i];
								if (nPos >= min && nPos < max) {
									if (!positions.contains(nPos) && !addPos.contains(nPos)) {
										addPos.add(nPos);
									}
								}
							}
						}
					}
					positions.addAll(addPos);
				}
			}
			min = max;
			i = incITo;
		}
		int maxW = 1;
		while (noVarPos >= 10) {
			noVarPos = noVarPos / 10;
			maxW++;
		}
		List<String> varOS = new ArrayList<String>();
		for (String key : varPos.keySet()) {
			List<Integer> positions = varPos.get(key);
			Collections.sort(positions);
			StringBuilder sb = new StringBuilder();
			for (int pos : positions) {
				String posStr = Integer.toString(pos);
				int posStL = posStr.length();
				int padLen = maxW-posStL;
				for (int i=0;i<padLen;i++) {
					sb.append('0');
				}
				sb.append(posStr);
			}
			varOS.add(sb.toString());
		}
		Collections.sort(varOS);
		for (int i = 0; i < varOS.size(); i++) {
			idStr.append(varOS.get(i));
			if (i < (varOS.size() - 1)) {
				idStr.append(",");
			}
		}
	}

	public String getIdentity() {
		return idStr.toString();
	}

	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		idStr.append(cons.getVal());
		return cons;
	}

	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		idStr.append("*");
		List<Integer> positions = varPos.get(var.getVal());
		if (null == positions) {
			positions = new ArrayList<Integer>();
			varPos.put(var.getVal(), positions);
		}
		positions.add(noVarPos);
		noVarPos++;
		clauseVar[currLiteral]++;
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}
}
class CNF {
	private List<Clause> CNClauses = new ArrayList<Clause>();
	public CNF(List<Clause> clauses) {
		this.CNClauses.addAll(clauses);
	}
	public List<Clause> getConjunctionOfClauses() {
		return Collections.unmodifiableList(CNClauses);
	}

}
class CNFConst implements AccessObject {
	public CNFConst() {}

	public CNF construct(Sentence orDisAnd) {
		ArgumentC argC = new ArgumentC();

		orDisAnd.take(this, argC);

		return new CNF(argC.clauses);
	}

	public Object accessPredicate(Predicate p, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		if (argC.negated) {
			argC.clauses.get(argC.clauses.size() - 1).addNegLiteral(p);
		} else {
			argC.clauses.get(argC.clauses.size() - 1).addPosLiteral(p);
		}
		return p;
	}

	public Object accessVariable(Variable var, Object arg) {
		throw new IllegalStateException("accessVariable() error");
	}

	public Object accessConstant(Constant cons, Object arg) {
		throw new IllegalStateException("accessConstant() error");
	}

	public Object accessNegatedSentence(NegatedSentence nSen, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		argC.negated = true;
		nSen.getNegatedST().take(this, arg);
		argC.negated = false;
		return nSen;
	}

	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		Sentence first = st.getFirstST();
		Sentence second = st.getSecondST();

		first.take(this, arg);
		if (st.getConnector().equals("&")) {
			argC.clauses.add(new Clause());
		}
		second.take(this, arg);

		return st;
	}	
	
	class ArgumentC {
		public List<Clause> clauses = new ArrayList<Clause>();
		public boolean negated = false;

		public ArgumentC() {
			clauses.add(new Clause());
		}
	}
}

class CNFConverter {

	public CNFConverter() { 

	}
	public CNF convertToCNF(Sentence aSentence) {
		Sentence deleteImplications = (Sentence) aSentence.take(new CNFDeleteImplications(), null);
		Sentence negationsIn = (Sentence) deleteImplications.take(new CNFNegationsIn(), null);
		Sentence orDistributedOverAnd = (Sentence) negationsIn.take(new CNFDistributeOrOverAnd(), null);
		return (new CNFConst()).construct(orDistributedOverAnd);
	}
}

class CNFDeleteImplications implements AccessObject {

	public CNFDeleteImplications() {
	}
	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}

	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		return p;
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		Sentence st1 = (Sentence) st.getFirstST().take(this, arg);
		Sentence st2 = (Sentence) st.getSecondST().take(this, arg);
		if (st.getConnector().equals("=>")) {
			return new ConnectSentence("|", new NegatedSentence(st1), st2);
		}
		return new ConnectSentence(st.getConnector(), st1, st2);	
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		Sentence negated = nSt.getNegatedST();

		return new NegatedSentence((Sentence) negated.take(this, arg));
	}

}
class CNFDistributeOrOverAnd implements AccessObject {
	public CNFDistributeOrOverAnd() {}

	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}

	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		return p;
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		Sentence st1 = (Sentence) st.getFirstST().take(this, arg);
		Sentence st2 = (Sentence) st.getSecondST().take(this, arg);

		if (st.getConnector().equals("|") && ConnectSentence.class.isInstance(st2)) {
			ConnectSentence st2AndSt3 = (ConnectSentence) st2;
			if (st2AndSt3.getConnector().equals("&")) {
				st2 = st2AndSt3.getFirstST();
				Sentence st3 = st2AndSt3.getSecondST();
				return new ConnectSentence("&",
						(Sentence) (new ConnectSentence("|", st1, st2)).take(this, arg),
						(Sentence) (new ConnectSentence("|", st1, st3)).take(this, arg));
			}
		}
		if (st.getConnector().equals("|") && ConnectSentence.class.isInstance(st1)) {
			ConnectSentence st1AndSt3 = (ConnectSentence) st1;
			if (st1AndSt3.getConnector().equals("&")) {
				st1 = st1AndSt3.getFirstST();
				Sentence st3 = st1AndSt3.getSecondST();
				return new ConnectSentence("&",
						(Sentence) (new ConnectSentence("|", st1, st2)).take(this, arg),
						(Sentence) (new ConnectSentence("|", st3, st2)).take(this, arg));
			}
		}
		return new ConnectSentence(st.getConnector(), st1, st2);
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		return new NegatedSentence((Sentence) nSt.getNegatedST().take(this, arg));
	}

}
class CNFNegationsIn implements AccessObject {

	public CNFNegationsIn() {}
	@Override
	public Object accessConstant(Constant cons, Object arg) {	
		// TODO Auto-generated method stub
		return cons;
	}
	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		return var;
	}
	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		return p;
	}
	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		return new ConnectSentence(st.getConnector(), (Sentence) st.getFirstST().take(this, arg),
				(Sentence) st.getSecondST().take(this, arg));
	}
	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		Sentence negSt = nSt.getNegatedST();

		if (negSt instanceof NegatedSentence) {
			return ((NegatedSentence) negSt).getNegatedST().take(this, arg);
		}

		if (negSt instanceof ConnectSentence) {
			ConnectSentence negConnect = (ConnectSentence) negSt;
			Sentence st1 = negConnect.getFirstST();
			Sentence st2 = negConnect.getSecondST();
			if (negConnect.getConnector().equals("&")) {
				Sentence notSt1 = (Sentence) (new NegatedSentence(st1)).take(this, arg);
				Sentence notSt2 = (Sentence) (new NegatedSentence(st2)).take(this, arg);
				return new ConnectSentence("|", notSt1, notSt2);
			}
			if (negConnect.getConnector().equals("|")) {
				Sentence notSt1 = (Sentence) (new NegatedSentence(st1)).take(this, arg);
				Sentence notSt2 = (Sentence) (new NegatedSentence(st2)).take(this, arg);
				return new ConnectSentence("&", notSt1, notSt2);
			}
		}
		return new NegatedSentence((Sentence) negSt.take(this, arg));
	}
}
interface Proof {
	Map<Variable, Terms> getAnswer();
	void replaceAnswer(Map<Variable, Terms> updateB);
}

class ProofFinal implements Proof {
	private Map<Variable, Terms> answers = new LinkedHashMap<Variable, Terms>();
	
	public ProofFinal(Map<Variable, Terms> answer) {
		this.answers.putAll(answer);
	}

	@Override
	public Map<Variable, Terms> getAnswer() {
		// TODO Auto-generated method stub
		return this.answers;
	}

	@Override
	public void replaceAnswer(Map<Variable, Terms> updateB) {
		// TODO Auto-generated method stub
		this.answers.clear();
		this.answers.putAll(updateB);

	}
}
interface Standardization {
	
	String getPrefix();
	int getNextId();
}
class Standardize {
	private VarCollect vars = null;
	private Substitution subst = null;
	
	public Standardize() {
		vars = new VarCollect();
		subst = new Substitution();
	}
	
	public Standardize(VarCollect varCollect, Substitution subst ) {
		this.vars = varCollect;
		this.subst = subst;
	}
	
	public StandardizeResult standardizeA(Sentence sentence, Standardization indexical) {
		Set<Variable> rename = this.vars.collectVars(sentence);
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		Map<Variable, Terms> reverseSubst = new HashMap<Variable, Terms>();
		
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(indexical.getPrefix() + indexical.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
			reverseSubst.put(v, var);
		}
		Sentence standardized = this.subst.subst(renameSubst, sentence);
		return new StandardizeResult(standardized, reverseSubst);
	}
	public Clause standardizeA(Clause clause, Standardization index) {
		Set<Variable> rename = vars.collectVars(clause);
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(index.getPrefix() + index.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
		}
		if (renameSubst.size() > 0) {
			List<Literal> literals = new ArrayList<Literal>();
			
			for (Literal l: clause.getLiterals()) {
				literals.add(this.subst.subst(renameSubst, l));
			}
			Clause renamed = new Clause(literals);
			return renamed;
		}
		return clause;
	}
	public Map<Variable, Terms> standardizeA(List<Literal> posLiterals, List<Literal> negLiterals, Standardization index) {
		Set<Variable> rename = new HashSet<Variable>();
		
		for (Literal pl : posLiterals) {
			rename.addAll(this.vars.collectVars(pl.getAtomS()));
		}
		for (Literal nl : negLiterals) {
			rename.addAll(this.vars.collectVars(nl.getAtomS()));
		}
		
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(index.getPrefix() + index.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
		}
		List<Literal> posLits = new ArrayList<Literal>();
		List<Literal> negLits = new ArrayList<Literal>();
		
		for (Literal pl: posLiterals) {
			posLits.add(this.subst.subst(renameSubst, pl));
		}
		for (Literal nl: negLiterals) {
			negLits.add(this.subst.subst(renameSubst, nl));
		}
		posLiterals.clear();
		posLiterals.addAll(posLits);
		negLiterals.clear();
		negLiterals.addAll(negLits);
		
		return renameSubst;
	}
}

class StandardGenerate {
	private static Map<Character, Integer> index = new HashMap<Character, Integer>();
	public static Standardization newId(Character nameVar) {
		Integer currNInt = index.get(nameVar);
		if (currNInt == null) {
			currNInt = 0;
		} else {
			currNInt += 1;
		}
		index.put(nameVar, currNInt);
		StringBuilder str = new StringBuilder();
		str.append(nameVar);
		for (int i = 0; i < currNInt; i++) {
			str.append(nameVar);
		}
		return new NewIndexical(str.toString());
	}

}
class NewIndexical implements Standardization {

	private String prefix = null;
	private int index = 0;
	
	public NewIndexical(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return this.prefix;
	}

	@Override
	public int getNextId() {
		// TODO Auto-generated method stub
		return this.index++;
	}

}

class StandardizeResult {
	
	private Sentence standardizedST = null;
	private Map<Variable, Terms> reverseSubst = null;

	public StandardizeResult(Sentence standardized, Map<Variable, Terms> reserveSubst) {
		this.standardizedST = standardized;
		this.reverseSubst = reserveSubst;
	}
	public Sentence getStandardizedST() {
		return this.standardizedST;
	}
	public Map<Variable, Terms> getReserveSubst() {
		return this.reverseSubst;
	}
}
class Substitution implements AccessObject {
	
	public Substitution() {}

	public Sentence subst(Map<Variable, Terms> theta, Sentence sentence) {
		return (Sentence) sentence.take(this, theta);
	}
	public Terms subst(Map<Variable, Terms> theta, Terms aTerm) {
		return (Terms) aTerm.take(this, theta);
	}
	
	public Literal subst(Map<Variable, Terms> theta, Literal literal) {
		return literal.getInstance((AtomicSentence) literal.getAtomS().take(this, theta));
	}
	
	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		Map<Variable, Terms> substitution = (Map<Variable, Terms>)arg;
		if (substitution.containsKey(var)) {
			return substitution.get(var).copy();
		}
		return var.copy();
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		List<Terms> terms = p.getTerms();
		List<Terms> newT = new ArrayList<Terms>();
		for (int i = 0; i < terms.size(); i++) {
			Terms t = terms.get(i);
			Terms subsTerm = (Terms) t.take(this, arg);
			newT.add(subsTerm);
		}
		return new Predicate(p.getPreName(), newT);
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		Sentence subst1 = (Sentence) st.getFirstST().take(this, arg);
		Sentence subst2 = (Sentence) st.getSecondST().take(this, arg);
		return new ConnectSentence(st.getConnector(), subst1, subst2);
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		return new NegatedSentence((Sentence) nSt.getNegatedST().take(this, arg));
	}
}

class VarCollect implements AccessObject {

	public VarCollect() {
		
	}
	
	public Set<Variable> collectVars(Sentence sentence) {
		Set<Variable> vars = new LinkedHashSet<Variable>();
		sentence.take(this, vars);
		return vars;
	}
	
	public Set<Variable> collectVars(Terms term) {
		Set<Variable> vars = new LinkedHashSet<Variable>();
		term.take(this, vars);
		return vars;
	}
	
	public Set<Variable> collectVars(Clause clause){
		Set<Variable> vars = new LinkedHashSet<Variable>();
		
		for (Literal l: clause.getLiterals()) {
			l.getAtomS().take(this, vars);
		}
		return vars;
	}
	
	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		Set<Variable> vars = (Set<Variable>) arg;
		vars.add(var);
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		for (Terms t: p.getTerms()){
			t.take(this, arg);
		}
		return p;
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		st.getFirstST().take(this, arg);
		st.getSecondST().take(this, arg);
		return st;
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		nSt.getNegatedST().take(this, arg);
		return nSt;
	}

}

class KB {
	private Parser parser;
	private Substitution subst;
	private VarCollect variable;
	private Standardize standardize;
	private CNFConverter cnfConv;
	private List<Sentence> orgSentences = new ArrayList<Sentence>();
	private Set<Clause> clauses = new LinkedHashSet<Clause>();
	private List<Clause> definiteClauses = new ArrayList<Clause>();
	private List<Clause> implicationDefClauses = new ArrayList<Clause>();
	private Map<String, List<Literal>> indexf = new HashMap<String, List<Literal>>();
	private Standardization varId = StandardGenerate.newId('x');
	private Standardization queryId = StandardGenerate.newId('q');
	
	public KB(Domain domain) {
		this.parser = new Parser(new Domain(domain));
		this.subst = new Substitution();
		this.variable = new VarCollect();
		this.standardize = new Standardize(variable, subst);
		this.cnfConv = new CNFConverter();
	}
	
	public Set<Clause> getClauses() {
		return Collections.unmodifiableSet(this.clauses);
	}
	
	public Sentence tell(String st) {
		Sentence s = parser.parse(st);
		store(s);
		return s;
	}
	public ResolutionResult ask(String query) {
		return ask(parser.parse(query));
	}
	
	public ResolutionResult ask(Sentence query) {
		StandardizeResult stResult = this.standardize.standardizeA(query, this.queryId);
		Resolution inference = new Resolution();
		ResolutionResult infResult = inference.ask(this, stResult.getStandardizedST());
		for (Proof p: infResult.getProofs()) {
			Map<Variable, Terms> im = p.getAnswer();
			Map<Variable, Terms> updateM = new LinkedHashMap<Variable, Terms>();
			for (Variable rev: stResult.getReserveSubst().keySet()) {
				updateM.put((Variable) stResult.getReserveSubst().get(rev), im.get(rev));
			}
			p.replaceAnswer(updateM);
		}
		return infResult;	
	}
	
	public Sentence standardizeA(Sentence st) {
		return this.standardize.standardizeA(st, varId).getStandardizedST();
	}
	public Clause standardizeA(Clause c) {
		return this.standardize.standardizeA(c, this.varId);
	}
	public Set<Variable> collectVars(Sentence st) {
		return this.variable.collectVars(st);
	}
	public CNF convertToCNF(Sentence st) {
		return this.cnfConv.convertToCNF(st);
	}
	public Set<Clause> convertToClauses(Sentence st) {
		CNF cnf = this.cnfConv.convertToCNF(st);
		return new LinkedHashSet<Clause>(cnf.getConjunctionOfClauses());
	}
	public Literal createAnswerLit(Sentence query) {
		String name = this.parser.getDomain().setAnsLiteral();
		List<Terms> terms = new ArrayList<Terms>();
		Set<Variable> vars = this.variable.collectVars(query);
		for (Variable v: vars) {
			terms.add(v.copy());
		}
		return new Literal(new Predicate(name, terms));
	}
	public Set<Clause> constructClauses(Sentence st) {
		CNF cnf = this.cnfConv.convertToCNF(st);
		return new LinkedHashSet<Clause>(cnf.getConjunctionOfClauses());
	}
	
	private void store(Sentence st) {
		this.orgSentences.add(st);
		CNF cnfSt = this.cnfConv.convertToCNF(st);
		for (Clause c : cnfSt.getConjunctionOfClauses()) {
			if (c.isEmpty()){
				throw new IllegalArgumentException("Wrong sentence format to KB");
			}
			c = standardize.standardizeA(c, this.varId);
			c.setImmutable();
			if (clauses.add(c)) {
				if (c.isDefiniteClause()){
					this.definiteClauses.add(c);
				}
				if (c.isImplicationDefClause()) {
					this.implicationDefClauses.add(c);
				}
				if (c.isUnitClause()) {
					indexF(c.getLiterals().iterator().next());
				}
			}
		}
	}
	private void indexF(Literal l) {
		StringBuilder key = new StringBuilder();
		if (l.isPositive()) {
			key.append("+");
		} else {
			key.append("=");
		}
		key.append(l.getAtomS().getName());
		String litKey = key.toString();
		if (! this.indexf.containsKey(litKey)){
			this.indexf.put(litKey, new ArrayList<Literal>());
		}
		this.indexf.get(litKey).add(l);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Sentence s : orgSentences) {
			sb.append(s.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
class Resolution {
	private long maxRunningTime = 15000; // add
	public Resolution() {}
	public Resolution(long time) { // add
		setMaxTime(time);
	}

	public long getMaxTime() {		//add
		return maxRunningTime;
	}

	public void setMaxTime(long time) {		//add
		this.maxRunningTime = time;
	}
	public ResolutionResult ask(KB KBase, Sentence query) {
		Set<Clause> clauses = new LinkedHashSet<Clause>();
		for (Clause c: KBase.getClauses()) {
			c = KBase.standardizeA(c);
			c.setStandardizeNotRequired();
			clauses.addAll(c.getFactors());
		}
		Sentence negQuery = new NegatedSentence(query);
		Literal answerLit = KBase.createAnswerLit(negQuery);
		Set<Variable> answerLitVars = KBase.collectVars(answerLit.getAtomS());
		Clause answerClause = new Clause();
		
		if (answerLitVars.size() > 0) {
			Sentence negQueryAnswer = new ConnectSentence("|", negQuery, answerLit.getAtomS());
			for (Clause c: KBase.constructClauses(negQueryAnswer)) {
				c = KBase.standardizeA(c);
				c.setStandardizeNotRequired();
				clauses.addAll(c.getFactors());
			}
			answerClause.addLiteral(answerLit);
		} else {
			for (Clause c: KBase.constructClauses(negQuery)) {
				c = KBase.standardizeA(c);
				c.setStandardizeNotRequired();
				clauses.addAll(c.getFactors());
			}
		}
		
		ResolutionControl ansCheck = new ResolutionControl(answerLit, answerLitVars, answerClause, maxRunningTime);
		Set<Clause> newCl = new LinkedHashSet<Clause>();
		Set<Clause> add = new LinkedHashSet<Clause>();
		int sizePrevC = clauses.size();
		do {
			newCl.clear();
			Clause[] clausesA = new Clause[clauses.size()];
			clauses.toArray(clausesA);
			for (int i = 0; i < clausesA.length; i++) {
				Clause cI = clausesA[i];
				for (int j= i ; j  < clausesA.length; j++) {
					Clause cJ = clausesA[j];
					Set<Clause> binaryResol = cI.binaryResol(cJ);
					if (binaryResol.size() > 0) {
						add.clear();
						for (Clause rc: binaryResol) {
							add.addAll(rc.getFactors());
						}
						ansCheck.isAnswers(add);
						
						if (ansCheck.isComplete()) {
							break;
						}
						newCl.addAll(add);
					}
					if (ansCheck.isComplete()) {
						break;
					}
				}
				if (ansCheck.isComplete()) {
					break;
				}
			}
			sizePrevC = clauses.size();
			clauses.addAll(newCl);
			if (ansCheck.isComplete()){
				break;
			}
		} while (sizePrevC < clauses.size());
		return ansCheck;
	}
}

class ResolutionControl implements ResolutionResult {
	private Literal answerLit = null;
	private Set<Variable> answerLitVars = null;
	private Clause ansClause = null;
	private boolean complete = false;
	private List<Proof> proofs = new ArrayList<Proof>();
	private long finishT = 0L; // add
	
	public ResolutionControl(Literal ansLit, Set<Variable> answerLitVars, Clause ansClause, long time) {
		this.answerLit = ansLit;
		this.answerLitVars = answerLitVars;
		this.ansClause = ansClause;
		this.finishT = System.currentTimeMillis() + time;
	}

	@Override
	public boolean isTrue() {
		// TODO Auto-generated method stub
		return this.proofs.size() > 0;
	}

	@Override
	public List<Proof> getProofs() {
		// TODO Auto-generated method stub
		return proofs;
	}
	
	public boolean isComplete() {
		return this.complete;
	}
	
	public void isAnswers(Set<Clause> resol) {
		for (Clause c : resol ) {
			if (ansClause.isEmpty()) {
				if (c.isEmpty()) {
					proofs.add(new ProofFinal(new HashMap<Variable, Terms>()));
					complete = true;
				}
			} else {
				if (c.isEmpty()) {
					throw new IllegalStateException("empty clause error");
				}
				if (c.isUnitClause() && c.isDefiniteClause() 
						&& c.getPosLiterals().get(0).getAtomS().getName().equals(answerLit.getAtomS().getName())) {
					Map<Variable, Terms> answerB = new HashMap<Variable, Terms>();
					List<Terms> answerT = c.getPosLiterals().get(0).getAtomS().getArguments();
					int idx = 0;
					for (Variable v: answerLitVars) {
						answerB.put(v, answerT.get(idx));
						idx++;
					}
					boolean newAnswer = true;
					for (Proof p: proofs) {
						if (p.getAnswer().equals(answerB)) {
							newAnswer = false;
							break;
						}
					}
					if (newAnswer) {
						proofs.add(new ProofFinal(answerB));
					}
				}
			}
			if (System.currentTimeMillis() > finishT) {		//add
				complete = true;
			}
		}
	}
}


interface ResolutionResult {

	boolean isTrue();
	List<Proof> getProofs();
}

class Unifier {
	private static Substitution subst = new Substitution();
	public Unifier() {}
	
	public Map<Variable, Terms> unify(Node x, Node y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else if (x instanceof Variable) {
			return unifyVar((Variable) x, y, theta);
		} else if (y instanceof Variable) {
			return unifyVar((Variable) y, x, theta);
		} else if (x.checkCompoundSen() && y.checkCompoundSen()) {
			return unify(x.getArguments(), y.getArguments(), unifyOps(x.getName(), y.getName(), theta));
		} else {
			return null;
		}
	}
	
	public Map<Variable,Terms> unify(List<? extends Node> x, List<? extends Node> y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.size() != y.size()) {
			return null;
		} else if (x.size() == 0 && y.size() == 0) {
			return theta;
		} else if (x.size() == 1 && y.size() == 1) {
			return unify(x.get(0), y.get(0), theta);
		} else {
			return unify(x.subList(1, x.size()), y.subList(1, y.size()), unify(x.get(0), y.get(0), theta));
		}
	}
	
	private Map<Variable, Terms> unifyVar(Variable var, Node x, Map<Variable, Terms> theta) {
		if (!Terms.class.isInstance(x)){
			return null;
		} else if (theta.keySet().contains(var)) {
			return unify(theta.get(var), x, theta);
		} else if (theta.keySet().contains(x)) {
			return unify(var, theta.get(x), theta);
		} else if (occurCheck(theta,var,x)) {
			return null;
		} else {
			addToTheta(theta, var, (Terms) x);
			return theta;
		}
	}
	
	protected boolean occurCheck(Map<Variable, Terms> theta, Variable var, Node x) {
		if (var.equals(x)) {
			return true;
		} else if (theta.containsKey(x)) {
			return occurCheck(theta, var, theta.get(x));
		}
		return false;
	}
	 
	private Map<Variable, Terms> unifyOps(String x, String y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else {
			return null;
		}
	}
	
	private Map<Variable, Terms> addToTheta(Map<Variable, Terms> theta, Variable var, Terms x) {
		theta.put(var, x);
		for (Variable v : theta.keySet()) {
			theta.put(v, subst.subst(theta,theta.get(v)));
		}
		return theta;
	}	
}














