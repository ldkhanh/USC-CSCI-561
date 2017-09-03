package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Clause {
	private static Standardization stIndex = StandardGenerate.newId('c');
	private static Unifier unifier = new Unifier();
	private static Substitution substA = new Substitution();
	private static VarCollect varCollect = new VarCollect();
	private static Standardize standardize = new Standardize();
	private static LiteralsSorter litSort = new LiteralsSorter();
	private final Set<Literal> literals = new LinkedHashSet<Literal>();
	private final List<Literal> positiveLit = new ArrayList<Literal>();
	private final List<Literal> negativeLit = new ArrayList<Literal>();
	private boolean immutable = false;
	private boolean saCheckRequired = true;
	private String equalityIdentity = "";
	private Set<Clause> factors = null;
	private Set<Clause> nonTrivialFactors = null;
	
	public Clause() {}
	
	public Clause(List<Literal> lits) {
		this.literals.addAll(lits);
		for (Literal l: literals) {
			if (l.isPositive()){
				this.positiveLit.add(l);
			} else {
				this.negativeLit.add(l);
			}
		}
		recalculateIdentity();		// check to delete
	}
	
	public Clause(List<Literal> lits1, List<Literal> lits2) {
		literals.addAll(lits1);
		literals.addAll(lits2);
		for (Literal l: literals) {
			if (l.isPositive()) {
				this.positiveLit.add(l);
			} else {
				this.negativeLit.add(l);
			}
		}
		recalculateIdentity();
	}
	
	public void addLiteral(Literal literal) {
		if (isImmutable()) {
			throw new IllegalStateException("Clause is immutable, cannot updated");
		} 
		int sizeO = literals.size();
		literals.add(literal);
		if (literals.size() > sizeO) {
			if (literal.isPositive()) {
				this.positiveLit.add(literal);
			} else {
				this.negativeLit.add(literal);
			}
		}
		recalculateIdentity();		
 	}
	public void addPosLiteral(AtomicSentence atom) {
		addLiteral(new Literal(atom));
	}
	public void addNegLiteral(AtomicSentence atom) {
		addLiteral(new Literal(atom, true));
	}
	public Set<Literal> getLiterals() {
		return Collections.unmodifiableSet(literals);
	}
	public List<Literal> getPosLiterals() {
		return Collections.unmodifiableList(this.positiveLit);
	}
	public int getNumLiterals() {
		return literals.size();
	}
	public int getNumPosLiterals() {
		return this.positiveLit.size();
	}
	public int getNumNegLiterals() {
		return this.negativeLit.size();
	}
	public Set<Clause> getFactors() {
		if (this.factors == null) {
			calculateFactors(null);
		}
		return Collections.unmodifiableSet(this.factors);
	}
	
	public boolean isImmutable() {
		return this.immutable;
	}
	public void setImmutable() {
		this.immutable = true;
	}
	public boolean isEmpty() {
		return this.literals.size() == 0;
	}
	public boolean isStandardizeRequired() {
		return this.saCheckRequired;
	}
	public void setStandardizeNotRequired() {
		this.saCheckRequired =false;
	}
	public boolean isUnitClause() {
		return this.literals.size() == 1;
	}
	public boolean isDefiniteClause() {
		return !isEmpty() && positiveLit.size() == 1;
	}
	public boolean isImplicationDefClause() {
		return this.isDefiniteClause() && this.negativeLit.size() >= 1;
	}
	
	public Set<Clause> binaryResol(Clause other) {
		Set<Clause> resol = new LinkedHashSet<Clause>();
		if (this.isEmpty() && other.isEmpty()) {
			resol.add(new Clause());
			return resol;
		}
		other = checkStandardized(other);
		
		List<Literal> allPosLits = new ArrayList<Literal>();
		List<Literal> allNegLits = new ArrayList<Literal>();
		allPosLits.addAll(this.positiveLit);
		allPosLits.addAll(other.positiveLit);
		allNegLits.addAll(this.negativeLit);
		allNegLits.addAll(other.negativeLit);
		
		List<Literal> oPosLits = new ArrayList<Literal>();
		List<Literal> oNegLits = new ArrayList<Literal>();
		List<Literal> copyRPosLits = new ArrayList<Literal>();
		List<Literal> copyRNegLits = new ArrayList<Literal>();
		
		for (int i = 0; i < 2; i++) {
			oPosLits.clear();
			oNegLits.clear();
			if (i == 0) {
				oPosLits.addAll(this.positiveLit);
				oNegLits.addAll(other.negativeLit);
			} else {
				oPosLits.addAll(other.positiveLit);
				oNegLits.addAll(this.negativeLit);
			}
			Map<Variable, Terms> copyTheta = new LinkedHashMap<Variable, Terms>();
			for (Literal pl: oPosLits) {
				for (Literal nl: oNegLits) {
					copyTheta.clear();
					if (unifier.unify(pl.getAtomS(), nl.getAtomS(), copyTheta) != null){
						copyRPosLits.clear();
						copyRNegLits.clear();
						boolean found = false;
						for (Literal l: allPosLits) {
							if (!found && pl.equals(l)) {
								found = true;
								continue;
							}
							copyRPosLits.add(substA.subst(copyTheta, l));
						}
						found = false;
						for (Literal l: allNegLits) {
							if (!found && nl.equals(l)) {
								found = true;
								continue;
							}
							copyRNegLits.add(substA.subst(copyTheta, l));
						}
						Clause c = new Clause(copyRPosLits, copyRNegLits);
						if (isImmutable()){
							c.setImmutable();
						}
						if (!isStandardizeRequired()){
							c.setStandardizeNotRequired();
						}
						resol.add(c);
					}
				}
			}
		}
		return resol;
	}
	
	private void recalculateIdentity() {
		List<Literal> sortedLiterals = new ArrayList<Literal>(literals);
		Collections.sort(sortedLiterals, litSort);
		
		ClauseEqIdentity ceic = new ClauseEqIdentity(sortedLiterals, litSort);
		equalityIdentity = ceic.getIdentity();
		factors = null;
		nonTrivialFactors = null;
	}
	
	@Override
	public int hashCode() {
		return equalityIdentity.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (null == other) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (!(other instanceof Clause)) {
			return false;
		}
		Clause othClause = (Clause) other;

		return equalityIdentity.equals(othClause.equalityIdentity);
	}
	
	private Clause checkStandardized(Clause other) {
		if (this.isStandardizeRequired() || this == other) {
			Set<Variable> mVar = varCollect.collectVars(this);
			Set<Variable> oVar = varCollect.collectVars(other);
			
			Set<Variable> cVar = new HashSet<Variable>();
			cVar.addAll(mVar);
			cVar.addAll(oVar);
			if (cVar.size() < (mVar.size() + oVar.size())) {
				other = standardize.standardizeA(other, stIndex);
			}
		}
		return other;
	}
	
	private void calculateFactors(Set<Clause> pFactors) {
		this.nonTrivialFactors = new LinkedHashSet<Clause>();
		Map<Variable, Terms> theta = new HashMap<Variable, Terms>();
		List<Literal> lits = new ArrayList<Literal>();
		
		for (int i = 0; i < 2; i++) {
			lits.clear();
			if (i == 0) {
				lits.addAll(this.positiveLit);
			} else {
				lits.addAll(this.negativeLit);
			}
			for (int x = 0; x < lits.size(); x++) {
				for (int y = x + 1; y < lits.size(); y++) {
					Literal litX = lits.get(x);
					Literal litY = lits.get(y);
					theta.clear();
					Map<Variable, Terms> subst = unifier.unify(litX.getAtomS(), litY.getAtomS(), theta);
					if (subst != null) {
						List<Literal> posLits = new ArrayList<Literal>();
						List<Literal> negLits = new ArrayList<Literal>();
						if (i == 0) {
							posLits.add(substA.subst(subst, litX));
						} else {
							negLits.add(substA.subst(subst, litX));
						}
						for (Literal pl: positiveLit) {
							if (pl == litX || pl == litY) {
								continue;
							}
							posLits.add(substA.subst(subst, pl));
						}
						for (Literal nl: negativeLit) {
							if (nl == litX || nl == litY) {
								continue;
							}
							negLits.add(substA.subst(subst, nl));
						}
						Clause c = new Clause(posLits, negLits);
						if (isImmutable()) {
							c.setImmutable();
						}
						if (!isStandardizeRequired()) {
							c.setStandardizeNotRequired();
						}
						if (pFactors == null) {
							c.calculateFactors(this.nonTrivialFactors);
							this.nonTrivialFactors.addAll(c.getFactors());
						} else {
							if (!pFactors.contains(c)) {
								c.calculateFactors(this.nonTrivialFactors);
								this.nonTrivialFactors.addAll(c.getFactors());
							}
						}
					}
				}
			}
		}
		factors = new LinkedHashSet<Clause>();
		factors.add(this);
		factors.addAll(this.nonTrivialFactors);	
	}
}
