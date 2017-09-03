package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectSentence implements Sentence {
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
