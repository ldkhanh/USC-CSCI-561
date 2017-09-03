package aiHW03;

import java.util.ArrayList;
import java.util.List;

public class Parser {
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
