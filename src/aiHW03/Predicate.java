package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Predicate implements AtomicSentence {
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
