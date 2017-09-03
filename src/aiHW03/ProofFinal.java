package aiHW03;

import java.util.*;

public class ProofFinal implements Proof {
	private Map<Variable, Terms> answers = new LinkedHashMap<Variable, Terms>();
	
	public ProofFinal(Map<Variable, Terms> answer) {
		this.answers.putAll(answer);
	}

	@Override
	public Map<Variable, Terms> getAnswer() {
		// TODO Auto-generated method stub
		return this.answers;
	}

	@Override
	public void replaceAnswer(Map<Variable, Terms> updateB) {
		// TODO Auto-generated method stub
		this.answers.clear();
		this.answers.putAll(updateB);

	}

}
