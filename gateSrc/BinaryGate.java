import java.util.ArrayList;


public class BinaryGate extends Gate{
	
	public enum Type {AND, OR, NAND, XOR, NOR, EQUIV, ONE, ZERO};
	
	private Type type;
	private Gate input1;
	private Gate input2;
	
	public BinaryGate() {
		//Initialize a standard gate to only output zero
		type = Type.ZERO;
	}
	
	public BinaryGate(Type t) {
		type = t;
	}
	
	public int out(int in1, int in2) {
		
		/* Convert the inputs to booleans
		 *   1 => True
		 *   0 => False
		 */
		
		boolean b1 = (in1==1);
		boolean b2 = (in2==1);
		boolean bout = false;
		
		// Calculate the output
		switch(type) {
		case AND: 
			bout = (b1 && b2);
			break;
		case OR:
			bout = (b1 || b2);
			break;
		case NAND:
			bout =  (!(b1 && b2));
			break;
		case XOR:
			bout =  (b1 != b2);
			break;
		case NOR:
			bout =  (!(b1 || b2));
			break;
		case EQUIV:
			bout =  (b1 == b2);
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
	
	public void setInput(Gate g1, Gate g2) {
		input1 = g1;
		input2 = g2;
	}

	public int getOutput() {
		// If either input is not connected, return -1
		if((input1 == null) || (input2 == null)) {
			return -1;
		}
		return out(input1.getOutput(), input2.getOutput());
	}

	public ArrayList<Gate> getInputs() {
		ArrayList<Gate> inputs = new ArrayList<Gate>();
		inputs.add(input1);
		inputs.add(input2);
		return inputs;
	}

	public Gate clone() {
		return new BinaryGate(this.type);
	}
}
