package aiHW03;

public class CNFDistributeOrOverAnd implements AccessObject {
	public CNFDistributeOrOverAnd() {}

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

		if (st.getConnector().equals("|") && ConnectSentence.class.isInstance(st2)) {
			ConnectSentence st2AndSt3 = (ConnectSentence) st2;
			if (st2AndSt3.getConnector().equals("&")) {
				st2 = st2AndSt3.getFirstST();
				Sentence st3 = st2AndSt3.getSecondST();
				return new ConnectSentence("&",
						(Sentence) (new ConnectSentence("|", st1, st2)).take(this, arg),
						(Sentence) (new ConnectSentence("|", st1, st3)).take(this, arg));
			}
		}
		if (st.getConnector().equals("|") && ConnectSentence.class.isInstance(st1)) {
			ConnectSentence st1AndSt3 = (ConnectSentence) st1;
			if (st1AndSt3.getConnector().equals("&")) {
				st1 = st1AndSt3.getFirstST();
				Sentence st3 = st1AndSt3.getSecondST();
				return new ConnectSentence("&",
						(Sentence) (new ConnectSentence("|", st1, st2)).take(this, arg),
						(Sentence) (new ConnectSentence("|", st3, st2)).take(this, arg));
			}
		}
		return new ConnectSentence(st.getConnector(), st1, st2);
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		return new NegatedSentence((Sentence) nSt.getNegatedST().take(this, arg));
	}

}
