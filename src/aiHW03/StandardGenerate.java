package aiHW03;

import java.util.HashMap;
import java.util.Map;

public class StandardGenerate {
	private static Map<Character, Integer> index = new HashMap<Character, Integer>();
	public static Standardization newId(Character nameVar) {
		Integer currNInt = index.get(nameVar);
		if (currNInt == null) {
			currNInt = 0;
		} else {
			currNInt += 1;
		}
		index.put(nameVar, currNInt);
		StringBuilder str = new StringBuilder();
		str.append(nameVar);
		for (int i = 0; i < currNInt; i++) {
			str.append(nameVar);
		}
		return new NewIndexical(str.toString());
	}

}
