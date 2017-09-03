package aiHW03;

public class Token {
	private int type;
	private String txt;
	private int startPosition;
	
	public Token(int type, String text, int position) {
		this.type = type;
		this.txt = text;
		this.startPosition = position;
	}
	
	public String getText() {
		return this.txt;
	}
	
	public int getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		Token tk = (Token) o;
		return ((tk.type == type) && (tk.txt.equals(this.txt)) && (tk.startPosition == this.startPosition));
	}
	
	@Override
	public int hashCode() {
		return this.startPosition + 21*((19+this.type)*21 + this.txt.hashCode()); 
	}
}
