package aiHW03;

public class CNFConverter {

	public CNFConverter() { 

	}
	public CNF convertToCNF(Sentence aSentence) {
		Sentence deleteImplications = (Sentence) aSentence.take(new CNFDeleteImplications(), null);
		Sentence negationsIn = (Sentence) deleteImplications.take(new CNFNegationsIn(), null);
		Sentence orDistributedOverAnd = (Sentence) negationsIn.take(new CNFDistributeOrOverAnd(), null);
		return (new CNFConst()).construct(orDistributedOverAnd);
	}
}

