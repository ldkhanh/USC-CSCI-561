package aiHW03;

import java.util.Map;

public interface Proof {
	Map<Variable, Terms> getAnswer();
	void replaceAnswer(Map<Variable, Terms> updateB);
	

}
