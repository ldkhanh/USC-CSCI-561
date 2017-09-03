package aiHW03;

import java.util.Comparator;
import java.util.List;

public class LiteralsSorter implements Comparator<Literal> {
	public int compare(Literal l1, Literal l2) {
		int value = 0;
		if (l1.isPositive() != l2.isPositive()) {
			return ((l1.isPositive()) ? 1: -1);
		}
		value = l1.getAtomS().getName().compareTo(l2.getAtomS().getName());
		if (value == 0) {
			value = compareArgs(l1.getAtomS().getArguments(), l2.getAtomS().getArguments());
		}
		
		return value;
	}
	private int compareArgs(List<Terms> arg1, List<Terms> arg2) {
		int value= arg1.size() - arg2.size();
		
		if (value == 0 && arg1.size() > 0) {
			Terms term1 = arg1.get(0);
			Terms term2 = arg2.get(0);
			
			if (term1.getClass() == term2.getClass()){
				if (term1 instanceof Constant){
					value = term1.getName().compareTo(term2.getName());
				}
				if (value == 0) {
					value = compareArgs(arg1.subList(1, arg1.size()), arg2.subList(1, arg2.size()));
				}
			} else {
				if (term1 instanceof Constant) {
					value = 1;
				} else if (term2 instanceof Constant) {
					value = -1;
				} else {
					value = -1;
				}
			}
		}
		return value;
		
	}

}
