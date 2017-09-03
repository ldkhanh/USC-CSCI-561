package aiHW03;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Substitution implements AccessObject {
	
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
