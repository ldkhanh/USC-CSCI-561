package aiHW03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Standardize {
	private VarCollect vars = null;
	private Substitution subst = null;
	
	public Standardize() {
		vars = new VarCollect();
		subst = new Substitution();
	}
	
	public Standardize(VarCollect varCollect, Substitution subst ) {
		this.vars = varCollect;
		this.subst = subst;
	}
	
	public StandardizeResult standardizeA(Sentence sentence, Standardization indexical) {
		Set<Variable> rename = this.vars.collectVars(sentence);
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		Map<Variable, Terms> reverseSubst = new HashMap<Variable, Terms>();
		
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(indexical.getPrefix() + indexical.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
			reverseSubst.put(v, var);
		}
		Sentence standardized = this.subst.subst(renameSubst, sentence);
		return new StandardizeResult(standardized, reverseSubst);
	}
	public Clause standardizeA(Clause clause, Standardization index) {
		Set<Variable> rename = vars.collectVars(clause);
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(index.getPrefix() + index.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
		}
		if (renameSubst.size() > 0) {
			List<Literal> literals = new ArrayList<Literal>();
			
			for (Literal l: clause.getLiterals()) {
				literals.add(this.subst.subst(renameSubst, l));
			}
			Clause renamed = new Clause(literals);
			return renamed;
		}
		return clause;
	}
	public Map<Variable, Terms> standardizeA(List<Literal> posLiterals, List<Literal> negLiterals, Standardization index) {
		Set<Variable> rename = new HashSet<Variable>();
		
		for (Literal pl : posLiterals) {
			rename.addAll(this.vars.collectVars(pl.getAtomS()));
		}
		for (Literal nl : negLiterals) {
			rename.addAll(this.vars.collectVars(nl.getAtomS()));
		}
		
		Map<Variable, Terms> renameSubst = new HashMap<Variable, Terms>();
		for (Variable var : rename) {
			Variable v = null;
			do {
				v = new Variable(index.getPrefix() + index.getNextId());
			} while (rename.contains(v));
			renameSubst.put(var, v);
		}
		List<Literal> posLits = new ArrayList<Literal>();
		List<Literal> negLits = new ArrayList<Literal>();
		
		for (Literal pl: posLiterals) {
			posLits.add(this.subst.subst(renameSubst, pl));
		}
		for (Literal nl: negLiterals) {
			negLits.add(this.subst.subst(renameSubst, nl));
		}
		posLiterals.clear();
		posLiterals.addAll(posLits);
		negLiterals.clear();
		negLiterals.addAll(negLits);
		
		return renameSubst;
	}
}







