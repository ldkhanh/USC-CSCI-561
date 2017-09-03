package aiHW03;

import java.util.Map;

public class StandardizeResult {
	
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
