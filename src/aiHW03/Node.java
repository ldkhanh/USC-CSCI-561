package aiHW03;

import java.util.List;

public interface Node {
	
	String getName();
	List<? extends Node> getArguments();
	Object take(AccessObject v, Object arg);
	boolean checkCompoundSen();
	Node copy();
	
	

}
