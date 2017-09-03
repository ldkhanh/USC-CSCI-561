package aiHW03;

public class CNFNegationsIn implements AccessObject {

	public CNFNegationsIn() {}
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
		return new ConnectSentence(st.getConnector(), (Sentence) st.getFirstST().take(this, arg),
				(Sentence) st.getSecondST().take(this, arg));
	}
	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		Sentence negSt = nSt.getNegatedST();

		if (negSt instanceof NegatedSentence) {
			return ((NegatedSentence) negSt).getNegatedST().take(this, arg);
		}

		if (negSt instanceof ConnectSentence) {
			ConnectSentence negConnect = (ConnectSentence) negSt;
			Sentence st1 = negConnect.getFirstST();
			Sentence st2 = negConnect.getSecondST();
			if (negConnect.getConnector().equals("&")) {
				Sentence notSt1 = (Sentence) (new NegatedSentence(st1)).take(this, arg);
				Sentence notSt2 = (Sentence) (new NegatedSentence(st2)).take(this, arg);
				return new ConnectSentence("|", notSt1, notSt2);
			}
			if (negConnect.getConnector().equals("|")) {
				Sentence notSt1 = (Sentence) (new NegatedSentence(st1)).take(this, arg);
				Sentence notSt2 = (Sentence) (new NegatedSentence(st2)).take(this, arg);
				return new ConnectSentence("&", notSt1, notSt2);
			}
		}
		return new NegatedSentence((Sentence) negSt.take(this, arg));
	}
}
