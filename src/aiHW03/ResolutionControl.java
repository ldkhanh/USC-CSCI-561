package aiHW03;

import java.util.*;

public class ResolutionControl implements ResolutionResult {
	private Literal answerLit = null;
	private Set<Variable> answerLitVars = null;
	private Clause ansClause = null;
	private boolean complete = false;
	private List<Proof> proofs = new ArrayList<Proof>();
	private long finishT = 0L; // add
	
	public ResolutionControl(Literal ansLit, Set<Variable> answerLitVars, Clause ansClause, long time) {
		this.answerLit = ansLit;
		this.answerLitVars = answerLitVars;
		this.ansClause = ansClause;
		this.finishT = System.currentTimeMillis() + time;
	}

	@Override
	public boolean isTrue() {
		// TODO Auto-generated method stub
		return this.proofs.size() > 0;
	}

	@Override
	public List<Proof> getProofs() {
		// TODO Auto-generated method stub
		return proofs;
	}
	
	public boolean isComplete() {
		return this.complete;
	}
	
	public void isAnswers(Set<Clause> resol) {
		for (Clause c : resol ) {
			if (ansClause.isEmpty()) {
				if (c.isEmpty()) {
					proofs.add(new ProofFinal(new HashMap<Variable, Terms>()));
					complete = true;
				}
			} else {
				if (c.isEmpty()) {
					throw new IllegalStateException("empty clause error");
				}
				if (c.isUnitClause() && c.isDefiniteClause() 
						&& c.getPosLiterals().get(0).getAtomS().getName().equals(answerLit.getAtomS().getName())) {
					Map<Variable, Terms> answerB = new HashMap<Variable, Terms>();
					List<Terms> answerT = c.getPosLiterals().get(0).getAtomS().getArguments();
					int idx = 0;
					for (Variable v: answerLitVars) {
						answerB.put(v, answerT.get(idx));
						idx++;
					}
					boolean newAnswer = true;
					for (Proof p: proofs) {
						if (p.getAnswer().equals(answerB)) {
							newAnswer = false;
							break;
						}
					}
					if (newAnswer) {
						proofs.add(new ProofFinal(answerB));
					}
				}
			}
			if (System.currentTimeMillis() > finishT) {		//add
				complete = true;
			}
		}
	}
	

}
