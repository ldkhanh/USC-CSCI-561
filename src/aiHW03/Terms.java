package aiHW03;

import java.util.List;

public interface Terms extends Node {

	Terms copy();
	List<Terms> getArguments();
}
