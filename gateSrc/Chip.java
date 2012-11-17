import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Chip extends Gate{

	private ArrayList<Gate> inputs;
	private ArrayList<Gate> outputs;
	
	// Holds a map of hashcodes to previously used gates
	private Map<Integer, Gate> prev;
	
	public Chip() {
		inputs = new ArrayList<Gate>();
		prev = new HashMap<Integer, Gate>();
	}
	
	/* Add an output.
	 * This function looks at g's inputs and copies the whole structure.
	 * Gate g is the root gate whose structure is being added.
	 * out is where the output of the gate is being routed to.
	 * If out is null, the gate is added to the outputs
	 */
	public void addOutput(Gate g, Gate out) {
		
		if(out == null) {
			outputs.add(g);
		}
		
		for(Gate i : g.getInputs()) {
			if(i==null) {
				inputs.add(g.clone());
			} else {
				if(prev.containsKey(i.hashCode())) {
					
				}
			}
		}
	}
	
	public int getOutput() {
		return 0;
	}


	public ArrayList<Gate> getInputs() {
		return inputs;
	}
	
	public Gate clone() {
		return null;
	}

}
