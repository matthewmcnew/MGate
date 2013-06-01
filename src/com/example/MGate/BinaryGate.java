package com.example.MGate;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;


public class BinaryGate extends Gate{
	
	public enum Type {AND, OR, NAND, XOR, NOR, EQUIV, ONE, ZERO};
	
	private Type type;
	private Gate input1;
	private Gate input2;
	private int literal1;
	private int literal2;
	
	private int cNode;
	
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
	
	public void addInput(Gate g) {
		if(cNode == 1) {
			input1 = g;
		} else {
			input2 = g;
		}
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
	
	public boolean inputFlip(float eX, float eY) {
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y-5) && eY < (y+bitmap.getHeight()/2) ){
				if(input1 == null)
					flipLiteral(0);				
				return true;
			}
		}
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y+bitmap.getHeight()/2) && eY < (y+bitmap.getHeight()+5) ){
				if(input2 == null)
					flipLiteral(1);
				return true;
			}
		}
		return false;
	}
	
	//This needs to be dryed up some day. 
	public Gate disconnectWire(float eX, float eY) {
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y-5) && eY < (y+bitmap.getHeight()/2) ){
				if(input1 != null) {
					return input1;
				}
									
			}
		}
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y+bitmap.getHeight()/2) && eY < (y+bitmap.getHeight()+5) ){
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
	
	
	
	
	public boolean snapWire(float eX, float eY, Gate selected) {
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y-5) && eY < (y+bitmap.getHeight()/2) ){
				if(this != selected && !selected.inPath(this)) {
					Gateway.p(selected);
					input1 = selected;
					return true;
				}
			}
		}
		if(eX > (x-35) && eX < (x+22) ){
			if(eY > (y+bitmap.getHeight()/2) && eY < (y+bitmap.getHeight()+5) ){
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
			if(glowing) {
				//Draw Circle mark
				c.drawBitmap(circles.get(6), x+bitmap.getWidth()-26, y + bitmap.getHeight()/2 - 19, null);
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
	
	public static void p(Object o) {
		System.out.println(o);
	}
	
	public void deleteWires(float x1, float y1, float x2, float y2) {
				
		//If user swiped right to left, reverse the coordinates
		if(x1>x2) {
			float t = x2;
			x2 = x1;
			x1 = t;
			
			t = y2;
			y2 = y1;
			y1 = t;
		}
		
		// Calc the swipe m and b
		float m = (y2-y1)/(x2-x1);
		float b = y1 - (x1*m);
		
		//Delete the wire on input1 if necessary
		if(input1 != null && !input1.isDeleted()) {
			
			float wy1, wy2, wx1, wx2;
			
			//Determine the rightmost point
			if(input1.getOutputX() > x-25) {
				wx2 = input1.getOutputX();
				wy2 = input1.getOutputY();
				
				wx1 = x - 25;
				wy1 = y + 19;
			} else {
				wx1 = input1.getOutputX();
				wy1 = input1.getOutputY();
				
				wx2 = x - 25;
				wy2 = y + 19;
			}
			
			//Calc the m and b for the wire
			float wm = (wy2-wy1)/(wx2-wx1);
			float wb = wy1 - (wx1*wm);
			
			//Make sure lines are not parallel, therefore they intersect
			if(wm != m) {

				
				//Calculate x and y of intersection
				float ix = (b-wb)/(wm-m);
				float iy = (ix*m) + b;
				
				//Check the x coordinate
				if((ix > wx1) && (ix < wx2) && (ix > x1) && (ix < x2)) {
					
					//Check the y coordinate
					if((((iy > wy1) && (iy < wy2)) || ((iy < wy1) && (iy > wy2))) && (((iy > y1) && (iy < y2)) || ((iy < y1) && (iy > y2)))) {
						p("y coord works");

						input1 = null;
					}
				}
			}
		}
		
		//Delete the wire on input2 if necessary
		if(input2 != null && !input2.isDeleted()) {
			float wy1, wy2, wx1, wx2;

			//Determine the rightmost point
			if(input2.getOutputX() > x-25) {
				wx2 = input2.getOutputX();
				wy2 = input2.getOutputY();

				wx1 = x - 25;
				wy1 = y + 19;
			} else {
				wx1 = input2.getOutputX();
				wy1 = input2.getOutputY();

				wx2 = x - 25;
				wy2 = y + 19;
			}

			//Calc the m and b for the wire
			float wm = (wy2-wy1)/(wx2-wx1);
			float wb = wy1 - (wx1*wm);

			//Make sure lines are not parallel, therefore they intersect
			if(wm != m) {
				//Calculate x and y of intersection
				float ix = (b-wb)/(wm-m);
				float iy = (ix*m) + b;

				//Check the x coordinate
				if((ix > wx1) && (ix < wx2) && (ix > x1) && (ix < x2)) {
					//Check the y coordinate
					if((((iy > wy1) && (iy < wy2)) || ((iy < wy1) && (iy > wy2))) && (((iy > y1) && (iy < y2)) || ((iy < y1) && (iy > y2)))) {
						input2 = null;
					}
				}
			}
		}
	}

	public void drawWires(Canvas c) {
		paint.setStrokeWidth(3);
		if(input1 != null && !input1.isDeleted()) {
			c.drawLine(x-13, y + 19, input1.getOutputX(), input1.getOutputY(), paint);
		}

		if(input2 != null && !input2.isDeleted()) {
			c.drawLine(x-13, y + 52, input2.getOutputX(), input2.getOutputY(), paint);
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
	
	public boolean isConnecting(Gate g){
		if(!g.inPath(this))
		{
			if((Math.abs(g.getOutputX()-10 - (x-25)) < 15) && (Math.abs(g.getOutputY() - (y + 7)) < 15)) {
				cNode = 1;
				return true;
			}
			
			if((Math.abs(g.getOutputX()-10 - (x-25)) < 15) && (Math.abs(g.getOutputY() - (y + 40)) < 15)) {
				cNode = 2;
				return true;
			}			
		}
		
		return false;
	}
	
	
}
