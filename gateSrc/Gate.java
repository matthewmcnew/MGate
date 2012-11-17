import java.util.ArrayList;


public abstract class Gate {

	public abstract int getOutput();
	public abstract Gate clone();
	public abstract ArrayList<Gate> getInputs();
	
	public void setInput(Gate g) {
		System.out.print("Warning: gate does not take single input");
	}
	
	public void addInput(Gate g) {
		System.out.print("Warning: gate does not take input");
	}
	
	public void setInput(Gate g1, Gate g2){
		System.out.print("Warning: gate does not take double input");
	}
}
