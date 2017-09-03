package aiHW03;

import java.util.HashSet;
import java.util.Set;

public class Domain {
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
