package aiHW03;

import java.util.List;

public class Variable implements Terms {
	private String val;
	private int hashCode = 0;
	private int index = -1;
	
	public Variable(String str) {
		this.val = str.trim();
	}
	public Variable(String str, int id) {
		this.val = str.trim();
		this.index = id;
	}
	
	public String getVal() {
		return this.val;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int id) {
		this.index = id;
		this.hashCode = 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.val;
	}

	@Override
	public Object take(AccessObject v, Object arg) {
		// TODO Auto-generated method stub
		return v.accessVariable(this, arg);
	}

	@Override
	public boolean checkCompoundSen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Variable copy() {
		// TODO Auto-generated method stub
		return new Variable(val, index);
	}

	@Override
	public List<Terms> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int hashCode() {
		return (hashCode != 0)? hashCode : ((this.index+13)*21 + val.hashCode()); 
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
		if (!(o instanceof Variable)) {
			return false;
		}

		Variable v = (Variable) o;
		return v.getVal().equals(getVal())
				&& v.getIndex() == getIndex();
	}
}
