package aiHW03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClauseEqIdentity implements AccessObject {
	private StringBuilder idStr = new StringBuilder();
	private int noVarPos = 0;
	private int[] clauseVar = null;
	private int currLiteral = 0;
	private Map<String, List<Integer>> varPos = new HashMap<String, List<Integer>>();

	public ClauseEqIdentity(List<Literal> lits,LiteralsSorter sorter) {

		clauseVar = new int[lits.size()];
		for (Literal l : lits) {
			if (l.isNegative()) {
				idStr.append("~");
			}
			idStr.append(l.getAtomS().getName());
			idStr.append("(");
			boolean firstTerm = true;
			for (Terms t : l.getAtomS().getArguments()) {
				if (firstTerm) {
					firstTerm = false;
				} else {
					idStr.append(",");
				}
				t.take(this, null);
			}
			idStr.append(")");
			currLiteral++;
		}
		int min, max;
		min = max = 0;
		for (int i = 0; i < lits.size(); i++) {
			int incITo = i;
			int next = i + 1;
			max += clauseVar[i];
			while (next < lits.size()) {
				if (0 != sorter.compare(lits.get(i), lits.get(next))) {
					break;
				}
				max += clauseVar[next];
				incITo = next; 
				next++;
			}
			if ((next - i) > 1) {
				for (String key : varPos.keySet()) {
					List<Integer> positions = varPos.get(key);
					List<Integer> addPos = new ArrayList<Integer>();
					for (int pos : positions) {
						if (pos >= min && pos < max) {
							int pPos = pos;
							int nPos = pos;
							for (int slot = i; slot < (next - 1); slot++) {
								pPos += clauseVar[i];
								if (pPos >= min && pPos < max) {
									if (!positions.contains(pPos)
											&& !addPos.contains(pPos)) {
										addPos.add(pPos);
									}
								}
								nPos -= clauseVar[i];
								if (nPos >= min && nPos < max) {
									if (!positions.contains(nPos) && !addPos.contains(nPos)) {
										addPos.add(nPos);
									}
								}
							}
						}
					}
					positions.addAll(addPos);
				}
			}
			min = max;
			i = incITo;
		}
		int maxW = 1;
		while (noVarPos >= 10) {
			noVarPos = noVarPos / 10;
			maxW++;
		}
		List<String> varOS = new ArrayList<String>();
		for (String key : varPos.keySet()) {
			List<Integer> positions = varPos.get(key);
			Collections.sort(positions);
			StringBuilder sb = new StringBuilder();
			for (int pos : positions) {
				String posStr = Integer.toString(pos);
				int posStL = posStr.length();
				int padLen = maxW-posStL;
				for (int i=0;i<padLen;i++) {
					sb.append('0');
				}
				sb.append(posStr);
			}
			varOS.add(sb.toString());
		}
		Collections.sort(varOS);
		for (int i = 0; i < varOS.size(); i++) {
			idStr.append(varOS.get(i));
			if (i < (varOS.size() - 1)) {
				idStr.append(",");
			}
		}
	}

	public String getIdentity() {
		return idStr.toString();
	}

	@Override
	public Object accessConstant(Constant cons, Object arg) {
		// TODO Auto-generated method stub
		idStr.append(cons.getVal());
		return cons;
	}

	@Override
	public Object accessVariable(Variable var, Object arg) {
		// TODO Auto-generated method stub
		idStr.append("*");
		List<Integer> positions = varPos.get(var.getVal());
		if (null == positions) {
			positions = new ArrayList<Integer>();
			varPos.put(var.getVal(), positions);
		}
		positions.add(noVarPos);
		noVarPos++;
		clauseVar[currLiteral]++;
		return var;
	}

	@Override
	public Object accessPredicate(Predicate p, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}

	@Override
	public Object accessConnectSentence(ConnectSentence st, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}

	@Override
	public Object accessNegatedSentence(NegatedSentence nSt, Object arg) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Error");
	}

}
