package aiHW03;

public class NewIndexical implements Standardization {

	private String prefix = null;
	private int index = 0;
	
	public NewIndexical(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return this.prefix;
	}

	@Override
	public int getNextId() {
		// TODO Auto-generated method stub
		return this.index++;
	}

}
