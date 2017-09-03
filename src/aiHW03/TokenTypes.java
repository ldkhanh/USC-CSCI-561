package aiHW03;

public interface TokenTypes {
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