package aiHW03;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class Lexer {
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
