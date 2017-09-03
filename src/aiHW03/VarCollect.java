package aiHW03;

import java.util.LinkedHashSet;
import java.util.Set;

public class VarCollect implements AccessObject {

	public VarCollect() {
		
	}
	
	public Set<Variable> collectVars(Sentence sentence) {
		Set<Variable> vars = new LinkedHashSet<Variable>();
		sentence.take(this, vars);
		return vars;
	}
	
	public Set<Variable> collectVars(Terms term) {
		Set<Variable> vars = new LinkedHashSet<Variable>();
		term.take(this, vars);
		return vars;
	}
	
	public Set<Variable> collectVars(Clause clause){
		Set<Variable> vars = new LinkedHashSet<Variable>();
		
		for (Literal l: clause.getLiterals()) {
			l.getAtomS().take(this, vars);
		}
		return vars;
	}
	
	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		Set<Variable> vars = (Set<Variable>) arg;
		vars.add(var);
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		for (Terms t: p.getTerms()){
			t.take(this, arg);
		}
		return p;
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		st.getFirstST().take(this, arg);
		st.getSecondST().take(this, arg);
		return st;
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		nSt.getNegatedST().take(this, arg);
		return nSt;
	}

}
