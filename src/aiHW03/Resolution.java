package aiHW03;

import java.util.*;

public class Resolution {
	private long maxRunningTime = 15000; // add
	public Resolution() {}
	public Resolution(long time) { // add
		setMaxTime(time);
	}

	public long getMaxTime() {		//add
		return maxRunningTime;
	}

	public void setMaxTime(long time) {		//add
		this.maxRunningTime = time;
	}
	public ResolutionResult ask(KB KBase, Sentence query) {
		Set<Clause> clauses = new LinkedHashSet<Clause>();
		for (Clause c: KBase.getClauses()) {
			c = KBase.standardizeA(c);
			c.setStandardizeNotRequired();
			clauses.addAll(c.getFactors());
		}
		Sentence negQuery = new NegatedSentence(query);
		Literal answerLit = KBase.createAnswerLit(negQuery);
		Set<Variable> answerLitVars = KBase.collectVars(answerLit.getAtomS());
		Clause answerClause = new Clause();
		
		if (answerLitVars.size() > 0) {
			Sentence negQueryAnswer = new ConnectSentence("|", negQuery, answerLit.getAtomS());
			for (Clause c: KBase.constructClauses(negQueryAnswer)) {
				c = KBase.standardizeA(c);
				c.setStandardizeNotRequired();
				clauses.addAll(c.getFactors());
			}
			answerClause.addLiteral(answerLit);
		} else {
			for (Clause c: KBase.constructClauses(negQuery)) {
				c = KBase.standardizeA(c);
				c.setStandardizeNotRequired();
				clauses.addAll(c.getFactors());
			}
		}
		
		ResolutionControl ansCheck = new ResolutionControl(answerLit, answerLitVars, answerClause, maxRunningTime);
		Set<Clause> newCl = new LinkedHashSet<Clause>();
		Set<Clause> add = new LinkedHashSet<Clause>();
		int sizePrevC = clauses.size();
		do {
			newCl.clear();
			Clause[] clausesA = new Clause[clauses.size()];
			clauses.toArray(clausesA);
			for (int i = 0; i < clausesA.length; i++) {
				Clause cI = clausesA[i];
				for (int j= i ; j  < clausesA.length; j++) {
					Clause cJ = clausesA[j];
					Set<Clause> binaryResol = cI.binaryResol(cJ);
					if (binaryResol.size() > 0) {
						add.clear();
						for (Clause rc: binaryResol) {
							add.addAll(rc.getFactors());
						}
						ansCheck.isAnswers(add);
						
						if (ansCheck.isComplete()) {
							break;
						}
						newCl.addAll(add);
					}
					if (ansCheck.isComplete()) {
						break;
					}
				}
				if (ansCheck.isComplete()) {
					break;
				}
			}
			sizePrevC = clauses.size();
			clauses.addAll(newCl);
			if (ansCheck.isComplete()){
				break;
			}
		} while (sizePrevC < clauses.size());
		return ansCheck;
	}
}
