package aiHW03;

public interface AccessObject {
	
	public Object accessConstant(Constant cons, Object arg);
	public Object accessVariable(Variable var, Object arg);
	public Object accessPredicate(Predicate p, Object arg);
	public Object accessConnectSentence(ConnectSentence st, Object arg);
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg);

}
