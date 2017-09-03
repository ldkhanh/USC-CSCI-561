package aiHW03;

import java.util.List;
import java.util.Map;

public class Unifier {
	private static Substitution subst = new Substitution();
	public Unifier() {}
	
	public Map<Variable, Terms> unify(Node x, Node y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else if (x instanceof Variable) {
			return unifyVar((Variable) x, y, theta);
		} else if (y instanceof Variable) {
			return unifyVar((Variable) y, x, theta);
		} else if (x.checkCompoundSen() && y.checkCompoundSen()) {
			return unify(x.getArguments(), y.getArguments(), unifyOps(x.getName(), y.getName(), theta));
		} else {
			return null;
		}
	}
	
	public Map<Variable,Terms> unify(List<? extends Node> x, List<? extends Node> y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.size() != y.size()) {
			return null;
		} else if (x.size() == 0 && y.size() == 0) {
			return theta;
		} else if (x.size() == 1 && y.size() == 1) {
			return unify(x.get(0), y.get(0), theta);
		} else {
			return unify(x.subList(1, x.size()), y.subList(1, y.size()), unify(x.get(0), y.get(0), theta));
		}
	}
	
	private Map<Variable, Terms> unifyVar(Variable var, Node x, Map<Variable, Terms> theta) {
		if (!Terms.class.isInstance(x)){
			return null;
		} else if (theta.keySet().contains(var)) {
			return unify(theta.get(var), x, theta);
		} else if (theta.keySet().contains(x)) {
			return unify(var, theta.get(x), theta);
		} else if (occurCheck(theta,var,x)) {
			return null;
		} else {
			addToTheta(theta, var, (Terms) x);
			return theta;
		}
	}
	
	protected boolean occurCheck(Map<Variable, Terms> theta, Variable var, Node x) {
		if (var.equals(x)) {
			return true;
		} else if (theta.containsKey(x)) {
			return occurCheck(theta, var, theta.get(x));
		}
		return false;
	}
	 
	private Map<Variable, Terms> unifyOps(String x, String y, Map<Variable, Terms> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else {
			return null;
		}
	}
	
	private Map<Variable, Terms> addToTheta(Map<Variable, Terms> theta, Variable var, Terms x) {
		theta.put(var, x);
		for (Variable v : theta.keySet()) {
			theta.put(v, subst.subst(theta,theta.get(v)));
		}
		return theta;
	}	
}
