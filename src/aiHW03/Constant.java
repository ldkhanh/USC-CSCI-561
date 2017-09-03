package aiHW03;

import java.util.List;
public class Constant implements Terms {
	private String val;
	private int hashCode = 0;
	
	public Constant(String str) {
		this.val = str;
	}

	public String getVal() {
		return this.val;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.val;
	}

	@Override
	public Object take(AccessObject v, Object arg) {
		// TODO Auto-generated method stub
		return v.accessConstant(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Constant copy() {
		// TODO Auto-generated method stub
		return new Constant(val);
	}

	@Override
	public List<Terms> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : (val.hashCode() + 21*13); 
	}

	@Override
	public String toString() {
		return this.val;
	}
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof Constant)) {
			return false;
		}
		Constant c = (Constant) o;
		return c.getVal().equals(getVal());

	}
}
