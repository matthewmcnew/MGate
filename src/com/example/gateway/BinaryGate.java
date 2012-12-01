package com.example.gateway;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;


public class BinaryGate extends Gate{
	
	public enum Type {AND, OR, NAND, XOR, NOR, EQUIV, ONE, ZERO};
	
	private Type type;
	private Gate input1;
	private Gate input2;
	private int literal1;
	private int literal2;
	
	public BinaryGate() {
		//Initialize a standard gate to only output zero
		type = Type.ZERO;
	}
	
	public BinaryGate(Type t, Bitmap b, float ix, float iy) {
		type = t;
		bitmap = b;
		x = ix;
		y = iy;
		literal1 = 0;
		literal2 = 0;
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
	
	public void setInput(Gate g){
		input1 = g;
		input2 = null;
	}

	public int getOutput() {
		int in1;
		int in2;
		
		// If either input is not connected, use literals
		if(input1 == null) {
			in1 = literal1;
		} else {
			in1 = input1.getOutput();
		}
		
		if(input2 == null) {
			in2 = literal2;
		} else {
			in2 = input2.getOutput();
		}
		return out(in1, in2);
	}
	
	public boolean inPath(Gate g) {
		if(this == g) {
			return true;
		} else {
			if(input1 != null) {
				if(input1 == g) {
					return true;
				}
			}
			
			if(input2 != null) {
				if(input2 == g) {
					return true;
				}
			}
			
			if(input1 == null) {
				if(input2 == null) {
					return false;
				} else {
					return input2.inPath(g);
				}
			} else {
				if(input2 == null) {
					return input1.inPath(g);
				} else {
					return (input1.inPath(g) || input2.inPath(g));
				}
			}
		}
		
	}

	public ArrayList<Gate> getInputs() {
		ArrayList<Gate> inputs = new ArrayList<Gate>();
		inputs.add(input1);
		inputs.add(input2);
		return inputs;
	}

	public Gate clone() {
		return new BinaryGate(this.type, this.bitmap, this.x, this.y);
	}
	
	public void flipLiteral(int i) {
		if(i==0) {
			literal1 = (literal1+1)%2;
		} else {
			literal2 = (literal2+1)%2;
		}
	}
	
	public boolean inputFlip(MotionEvent event) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()/2) ){
				if(input1 == null)
					flipLiteral(0);				
				return true;
			}
		}
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y+bitmap.getHeight()/2) && event.getY() < (y+bitmap.getHeight()+5) ){
				if(input2 == null)
					flipLiteral(1);
				return true;
			}
		}
		return false;
	}
	
	//This needs to be dryed up some day. 
	public Gate disconnectWire(MotionEvent event) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()/2) ){
				if(input1 != null) {
					return input1;
				}
									
			}
		}
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y+bitmap.getHeight()/2) && event.getY() < (y+bitmap.getHeight()+5) ){
				if(input2 != null) {
					return input2;	
				}
			}
		}
		return null;
	}
	
	public void clearInput(Gate input) {
		if(input1 == input)
			input1 = null;
		else if (input2 == input) {
			input2 = null;
		}
	}
	
	
	
	
	public boolean snapWire(MotionEvent event, Gate selected) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()/2) ){
				if(this != selected && !selected.inPath(this)) {
					Gateway.p(selected);
					input1 = selected;
					return true;
				}
			}
		}
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y+bitmap.getHeight()/2) && event.getY() < (y+bitmap.getHeight()+5) ){
				if(this != selected && !selected.inPath(this)) {
					input2 = selected;
					return true;
				}
			}
		}
		return false;
	}
	
	public void draw(Canvas c,ArrayList<Bitmap> circles) {
		super.draw(c,circles);
		if(!selected) {
			if(inPath) {
				c.drawBitmap(circles.get(5), x-25, y + 7, null);
				c.drawBitmap(circles.get(5), x-25, y + 40, null);	
			}else {
				if(input1 == null || input1.isDeleted()) {
					input1 = null;
					if(literal1 == 0) {
						c.drawBitmap(circles.get(1), x-25, y + 7, null);
					}else if(literal1 == 1) {
						c.drawBitmap(circles.get(2), x-25, y + 7, null);
					}
				} else {
					c.drawBitmap(circles.get(0), x-25, y + 7, null);
				}

				if(input2 == null || input2.isDeleted()) {
					input2 = null;
					if(literal2 == 0) {
						c.drawBitmap(circles.get(1), x-25, y + 40, null);
					}else if(literal2 == 1) {
						c.drawBitmap(circles.get(2), x-25, y + 40, null);
					}
				} else {
					c.drawBitmap(circles.get(0), x-25, y + 40, null);
				}	
				
			}
			if(getOutput() == 0) {
				c.drawBitmap(circles.get(3), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}else if(getOutput() == 1) {
				c.drawBitmap(circles.get(4), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}
			
			
		} else {
			if(input1 != null) {
				c.drawBitmap(circles.get(0), x-25, y + 7, null);
			}
			
			if(input2 != null) {
				c.drawBitmap(circles.get(0), x-25, y + 40, null);
			}
		}
		
	}
	
	public void drawWires(Canvas c) {
		if(input1 != null && !input1.isDeleted()) {
			c.drawLine(x-25, y + 19, input1.getOutputX(), input1.getOutputY(), paint);
		}

		if(input2 != null && !input2.isDeleted()) {
			c.drawLine(x-25, y + 52, input2.getOutputX(), input2.getOutputY(), paint);
		}
	}
	
	protected void setInPath(boolean state){
		inPath = state;
		if(input1 != null) {
			input1.setInPath(state);
		}
		
		if(input2 != null) {
			input2.setInPath(state);
		}	
	}

	public String getHelp() {
		String str = "";
		switch(type) {
		case AND:
			str += "This is an AND gate.\n";
			str += "AND gates outputs one if both inputs are one. It outputs zero otherwise.\n";
			str += "The AND truth table is given below:\n";
			str += "c = a AND b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|0\n";
			str += "0|1|0\n";
			str += "1|0|0\n";
			str += "1|1|1\n";
			break;
		case OR:
			str += "This is an OR gate.\n";
			str += "OR gates output one if either input is one. It outputs zero otherwise.\n";
			str += "The OR truth table is given below:\n";
			str += "c = a OR b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|0\n";
			str += "0|1|1\n";
			str += "1|0|1\n";
			str += "1|1|1\n";
			break;

		case NAND:
			str += "This is a NAND gate.\n";
			str += "NAND gates output the logical opposite of the AND gate. a NAND b = NOT(a AND b).\n";
			str += "The NAND truth table is given below:\n";
			str += "c = a NAND b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|1\n";
			str += "0|1|1\n";
			str += "1|0|1\n";
			str += "1|1|0\n";
			break;
		case XOR:
			str += "This is an XOR gate.\n";
			str += "XOR gates output one if one input is the opposite of the other. It outputs zero otherwise.\n";
			str += "The XOR truth table is given below:\n";
			str += "c = a XOR b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|0\n";
			str += "0|1|1\n";
			str += "1|0|1\n";
			str += "1|1|0\n";
			break;
		case NOR:
			str += "This is a NOR gate.\n";
			str += "NOR gates outputs the logical opposite of the OR gate. a NOR b = NOT(a OR b).\n";
			str += "The NOR truth table is given below:\n";
			str += "c = a NOR b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|1\n";
			str += "0|1|0\n";
			str += "1|0|0\n";
			str += "1|1|0\n";
			break;
		case EQUIV:
			str += "This is an EQUIV (equivilency) gate.\n";
			str += "EQUIV gates output one if the inputs are the same. It outputs zero otherwise.\n";
			str += "The EQUIV truth table is given below:\n";
			str += "c = a EQUIV b\n\n";
			str += "a|b|c\n";
			str += "-----\n";
			str += "0|0|1\n";
			str += "0|1|0\n";
			str += "1|0|0\n";
			str += "1|1|1\n";
			break;
		case ONE:
			break;
		case ZERO:
			break;
		}
		return str;
	}


	public ArrayList<Gate> getBaseInputs() {
		ArrayList<Gate> ins = new ArrayList<Gate>();
		if(input1!=null) {
			ins.addAll(input1.getBaseInputs());
		}
		
		if(input2!=null) {
			ins.addAll(input2.getBaseInputs());
		}
		
		return ins;

	}
	
	
}
