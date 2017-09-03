package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CNF {
	private List<Clause> CNClauses = new ArrayList<Clause>();
	public CNF(List<Clause> clauses) {
		this.CNClauses.addAll(clauses);
	}
	public List<Clause> getConjunctionOfClauses() {
		return Collections.unmodifiableList(CNClauses);
	}

}
