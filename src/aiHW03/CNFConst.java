package aiHW03;

import java.util.ArrayList;
import java.util.List;


public class CNFConst implements AccessObject {
	public CNFConst() {}

	public CNF construct(Sentence orDisAnd) {
		ArgumentC argC = new ArgumentC();

		orDisAnd.take(this, argC);

		return new CNF(argC.clauses);
	}

	public Object accessPredicate(Predicate p, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		if (argC.negated) {
			argC.clauses.get(argC.clauses.size() - 1).addNegLiteral(p);
		} else {
			argC.clauses.get(argC.clauses.size() - 1).addPosLiteral(p);
		}
		return p;
	}

	public Object accessVariable(Variable var, Object arg) {
		throw new IllegalStateException("accessVariable() error");
	}

	public Object accessConstant(Constant cons, Object arg) {
		throw new IllegalStateException("accessConstant() error");
	}

	public Object accessNegatedSentence(NegatedSentence nSen, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		argC.negated = true;
		nSen.getNegatedST().take(this, arg);
		argC.negated = false;
		return nSen;
	}

	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		ArgumentC argC = (ArgumentC) arg;
		Sentence first = st.getFirstST();
		Sentence second = st.getSecondST();

		first.take(this, arg);
		if (st.getConnector().equals("&")) {
			argC.clauses.add(new Clause());
		}
		second.take(this, arg);

		return st;
	}	
	
	class ArgumentC {
		public List<Clause> clauses = new ArrayList<Clause>();
		public boolean negated = false;

		public ArgumentC() {
			clauses.add(new Clause());
		}
	}
}

