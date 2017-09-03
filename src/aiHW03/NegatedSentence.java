package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NegatedSentence implements Sentence {
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
