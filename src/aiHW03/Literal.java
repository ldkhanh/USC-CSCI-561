package aiHW03;

public class Literal {
	
	private AtomicSentence atomS = null;
	private boolean negated = false;
	private int hashCode = 0;
	
	public Literal(AtomicSentence atomS) {
		this.atomS = atomS;
	}
	public Literal(AtomicSentence atomS, boolean negated) {
		this.atomS = atomS;
		this.negated = negated;
	}
	
	public Literal getInstance(AtomicSentence atomS) {
		return new Literal(atomS, this.negated);
	}
	
	public boolean isPositive() {
		return !this.negated;
	}
	
	public boolean isNegative() {
		return this.negated;
	}
	
	public AtomicSentence getAtomS() {
		return this.atomS;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o.getClass() != this.getClass()) return false;
		if (!(o instanceof Literal)) return false;
		Literal lit = (Literal) o;
		return lit.isPositive() == this.isPositive() 
				&& lit.getAtomS().getName().equals(this.atomS.getName()) 
				&& lit.getAtomS().getArguments().equals(this.atomS.getArguments());
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (this.getClass().getSimpleName().hashCode() + 21*((25+this.atomS.getName().hashCode())*21 + this.atomS.getArguments().hashCode())); 
	}
}
