import java.util.ArrayList;


public class Input extends Gate {
	
	public enum Type {ONE, ZERO};
	
	private Type type;
	
	public Input() {
		type = Type.ZERO;
	}
	
	public Input(Type t) {
		type = t;
	}
	
	public int out() {
		switch(type) {
		case ONE:
			return 1;
		case ZERO:
			return 0;
		default:
			return 0;
		}
	}

	public int getOutput() {
		return out();
	}

	public ArrayList<Gate> getInputs() {
		return null;
	}
	
	public Gate clone() {
		return new Input(this.type);
	}
}
