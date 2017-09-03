package aiHW03;

import java.util.*;
public class KB {
	private Parser parser;
	private Substitution subst;
	private VarCollect variable;
	private Standardize standardize;
	private CNFConverter cnfConv;
	private List<Sentence> orgSentences = new ArrayList<Sentence>();
	private Set<Clause> clauses = new LinkedHashSet<Clause>();
	private List<Clause> definiteClauses = new ArrayList<Clause>();
	private List<Clause> implicationDefClauses = new ArrayList<Clause>();
	private Map<String, List<Literal>> indexf = new HashMap<String, List<Literal>>();
	private Standardization varId = StandardGenerate.newId('x');
	private Standardization queryId = StandardGenerate.newId('q');
	
	public KB(Domain domain) {
		this.parser = new Parser(new Domain(domain));
		this.subst = new Substitution();
		this.variable = new VarCollect();
		this.standardize = new Standardize(variable, subst);
		this.cnfConv = new CNFConverter();
	}
	
	public Set<Clause> getClauses() {
		return Collections.unmodifiableSet(this.clauses);
	}
	
	public Sentence tell(String st) {
		Sentence s = parser.parse(st);
		store(s);
		return s;
	}
	public ResolutionResult ask(String query) {
		return ask(parser.parse(query));
	}
	
	public ResolutionResult ask(Sentence query) {
		StandardizeResult stResult = this.standardize.standardizeA(query, this.queryId);
		Resolution inference = new Resolution();
		ResolutionResult infResult = inference.ask(this, stResult.getStandardizedST());
		for (Proof p: infResult.getProofs()) {
			Map<Variable, Terms> im = p.getAnswer();
			Map<Variable, Terms> updateM = new LinkedHashMap<Variable, Terms>();
			for (Variable rev: stResult.getReserveSubst().keySet()) {
				updateM.put((Variable) stResult.getReserveSubst().get(rev), im.get(rev));
			}
			p.replaceAnswer(updateM);
		}
		return infResult;	
	}
	
	public Sentence standardizeA(Sentence st) {
		return this.standardize.standardizeA(st, varId).getStandardizedST();
	}
	public Clause standardizeA(Clause c) {
		return this.standardize.standardizeA(c, this.varId);
	}
	public Set<Variable> collectVars(Sentence st) {
		return this.variable.collectVars(st);
	}
	public CNF convertToCNF(Sentence st) {
		return this.cnfConv.convertToCNF(st);
	}
	public Set<Clause> convertToClauses(Sentence st) {
		CNF cnf = this.cnfConv.convertToCNF(st);
		return new LinkedHashSet<Clause>(cnf.getConjunctionOfClauses());
	}
	public Literal createAnswerLit(Sentence query) {
		String name = this.parser.getDomain().setAnsLiteral();
		List<Terms> terms = new ArrayList<Terms>();
		Set<Variable> vars = this.variable.collectVars(query);
		for (Variable v: vars) {
			terms.add(v.copy());
		}
		return new Literal(new Predicate(name, terms));
	}
	public Set<Clause> constructClauses(Sentence st) {
		CNF cnf = this.cnfConv.convertToCNF(st);
		return new LinkedHashSet<Clause>(cnf.getConjunctionOfClauses());
	}
	
	private void store(Sentence st) {
		this.orgSentences.add(st);
		CNF cnfSt = this.cnfConv.convertToCNF(st);
		for (Clause c : cnfSt.getConjunctionOfClauses()) {
			if (c.isEmpty()){
				throw new IllegalArgumentException("Wrong sentence format to KB");
			}
			c = standardize.standardizeA(c, this.varId);
			c.setImmutable();
			if (clauses.add(c)) {
				if (c.isDefiniteClause()){
					this.definiteClauses.add(c);
				}
				if (c.isImplicationDefClause()) {
					this.implicationDefClauses.add(c);
				}
				if (c.isUnitClause()) {
					indexF(c.getLiterals().iterator().next());
				}
			}
		}
	}
	private void indexF(Literal l) {
		StringBuilder key = new StringBuilder();
		if (l.isPositive()) {
			key.append("+");
		} else {
			key.append("=");
		}
		key.append(l.getAtomS().getName());
		String litKey = key.toString();
		if (! this.indexf.containsKey(litKey)){
			this.indexf.put(litKey, new ArrayList<Literal>());
		}
		this.indexf.get(litKey).add(l);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Sentence s : orgSentences) {
			sb.append(s.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
