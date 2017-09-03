package aiHW03;

import java.util.List;

public interface AtomicSentence extends Sentence {
	AtomicSentence copy();
	List<Terms> getArguments();
}
