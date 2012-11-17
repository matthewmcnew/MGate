
public class test {
	
	public static void main(String args[]) {
		Gate in1 = new Input(Input.Type.ONE);
		Gate in2 = new Input(Input.Type.ONE);
		Gate and = new BinaryGate(BinaryGate.Type.AND);
		
		and.setInput(in1, in2);
		p("Output: "+and.getOutput());
	}
	
	public static void p(Object o) {
		System.out.println(o);
	}

}
