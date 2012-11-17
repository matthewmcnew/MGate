import java.util.ArrayList;


public class UnaryGate extends Gate {
	
	public enum Type {NOT, BUFF, ONE, ZERO};
	
	private Type type;
	private Gate input;
	
	public UnaryGate() {
		// Initialize a standard gate to only output zero
		type = Type.ZERO;
	}
	
	public UnaryGate(Type t) {
		type = t;
	}

	public int out(int in) {

		/* Convert the inputs to booleans
		 *   1 => True
		 *   0 => False
		 */

		boolean bin = (in==1);
		boolean bout = false;

		// Calculate the output
		switch(type) {
		case NOT: 
			bout = (!bin);
			break;
		case BUFF:
			bout = bin;
			break;
		case ONE:
			bout = true;
			break;
		case ZERO:
			bout = false;
			break;
		}
		
		// Convert and return the output
		if(bout) {
			return 1;
		} else {
			return 0;
		}
	}
	
	// Set the gate that will provide the input for this gate
	public void setInput(Gate g) {
		input = g;
	}
	
	public int getOutput() {
		// If the input is not connected, return -1
		if(input == null) {
			return -1;
		}
		return out(input.getOutput());
	}

	public ArrayList<Gate> getInputs() {
		ArrayList<Gate> inputs = new ArrayList<Gate>();
		inputs.add(input);
		return inputs;
	}
	
	public Gate clone() {
		return new UnaryGate(this.type);
	}
}
