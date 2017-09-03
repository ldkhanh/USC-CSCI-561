package aiHW03;

public class CNFDeleteImplications implements AccessObject {

	public CNFDeleteImplications() {
	}
	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		return cons;
	}

	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		return p;
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		Sentence st1 = (Sentence) st.getFirstST().take(this, arg);
		Sentence st2 = (Sentence) st.getSecondST().take(this, arg);
		if (st.getConnector().equals("=>")) {
			return new ConnectSentence("|", new NegatedSentence(st1), st2);
		}
		return new ConnectSentence(st.getConnector(), st1, st2);	
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		Sentence negated = nSt.getNegatedST();

		return new NegatedSentence((Sentence) negated.take(this, arg));
	}

}
