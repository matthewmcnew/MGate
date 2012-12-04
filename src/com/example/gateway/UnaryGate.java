package com.example.gateway;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;


public class UnaryGate extends Gate {
	
	public enum Type {NOT, BUFF, ONE, ZERO};
	
	private Type type;
	private Gate input;
	private int literal;
	
	public UnaryGate() {
		// Initialize a standard gate to only output zero
		type = Type.ZERO;
	}
	
	public UnaryGate(Type t, Bitmap b, float ix, float iy) {
		type = t;
		bitmap = b;
		x = ix;
		y = iy;
		literal = 0;
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
	
	public void addInput(Gate g) {
		setInput(g);
	}
	
	public int getOutput() {
		int in;
		
		// If the input is not connected, return -1
		if(input == null) {
			in = literal;
		} else {
			in = input.getOutput();
		}
		return out(in);
	}

	public ArrayList<Gate> getInputs() {
		ArrayList<Gate> inputs = new ArrayList<Gate>();
		inputs.add(input);
		return inputs;
	}
	
	public Gate clone() {
		return new UnaryGate(this.type, this.bitmap, this.x, this.y);
	}
	
	public void flipLiteral() {
		literal = (literal+1)%2;
	}
	
	public boolean inPath(Gate g) {
		if(this == g) {
			return true;
		} else if(input == null) {
			return false;
		} else {
			return (input.inPath(g));
		}
	}
	
	public boolean inputFlip(MotionEvent event) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()) ){
				System.out.print("insideFlip -----------------------------------------------------------------------------------\n");
				if(input == null)
					flipLiteral();
				return true;
			}
		}
		return false;
	}
	
	
	public Gate disconnectWire(MotionEvent event) {
		
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()) ){
				System.out.print("inside -----------------------------------------------------------------------------------\n");
				if(input != null) {
					return input;
				}
			}
		}

		return null;
	}
	
	public void clearInput(Gate input) {
		if(this.input == input) {
			this.input = null;
		}
	}
	
	public boolean snapWire(MotionEvent event, Gate selected) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()) ){
				if(selected != this && !selected.inPath(this)) {
					input = selected;
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
				c.drawBitmap(circles.get(5), x-25, y + bitmap.getHeight()/2 - 12, null);
			}else {
				if(input == null || input.isDeleted()) {
					input = null;
					if(literal == 0) {
						c.drawBitmap(circles.get(1), x-25, y + bitmap.getHeight()/2 - 12, null);
					}else if(literal == 1) {
						c.drawBitmap(circles.get(2), x-25, y + bitmap.getHeight()/2 - 12, null);
					}
				} else {
					c.drawBitmap(circles.get(0), x-25, y + bitmap.getHeight()/2 - 12, null);
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
			if (input != null) {
				c.drawBitmap(circles.get(0), x-25, y + bitmap.getHeight()/2 - 12, null);
			}
		}
		
	}
	
	public void drawWires(Canvas c) {
		if(!selected) {
			if(!(input == null || input.isDeleted())) {
				c.drawLine(x-13, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
			}
		} else {
			if (input != null) {
				c.drawLine(x-13, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
				
			}
		}
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
				if(input != null && !input.isDeleted()) {
					
					float wy1, wy2, wx1, wx2;
					
					//Determine the rightmost point
					if(input.getOutputX() > x-25) {
						wx2 = input.getOutputX();
						wy2 = input.getOutputY();
						
						wx1 = x - 25;
						wy1 = y + 19;
					} else {
						wx1 = input.getOutputX();
						wy1 = input.getOutputY();
						
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
								
								input = null;
							}
						}
					}
				}
	}
	
	protected void setInPath(boolean state){
		inPath = state;
		if(input != null) {
			input.setInPath(state);
		}
	}
	
	public String getHelp() {
		String str = "";
		switch(type) {
		case NOT:
			str += "This is a NOT gate.\n";
			str += "NOT gates output a value opposite of the input. ";
			str += "The opposite of 1 is considered 0, and vice cersa. ";
			str += "The NOT truth table is given below: \n";
			str += "b is NOT a\n\n;";
			str += "a|b\n";
			str += "---\n";
			str += "0|1\n";
			str += "1|0\n";
			break;
		case BUFF:
			str += "This is a BUFFER gate.\n";
			str += "BUFFER gates output the value of the input. ";
			str += "The BUFFER truth table is given below: \n";
			str += "b is BUFFER of a\n\n;";
			str += "a|b\n";
			str += "---\n";
			str += "0|0\n";
			str += "1|1\n";
			break;
		case ONE:
			str += "This is a ONE gate.\n";
			str += "ONE always gates output one. ";
			break;
		case ZERO:
			str += "This is a ZERO gate.\n";
			str += "ZERO always gates output zero. ";
			break;
		}
		return str;
	}

	@Override
	public ArrayList<Gate> getBaseInputs() {
		ArrayList<Gate> ins = new ArrayList<Gate>();

		if(input!=null) {
			ins.addAll(input.getBaseInputs());
		}
		return ins;
	}
	
	public boolean isConnecting(Gate g){
		if((Math.abs(g.getOutputX()-10 - (x-25)) < 15) && (Math.abs(g.getOutputY() - (y + bitmap.getHeight()/2 - 12)) < 15)) {
			if(!g.inPath(this))
				return true;
		}
		
		return false;
	}
	@Override
	public boolean inGate(MotionEvent event){
		return((event.getX() > x && event.getX() < (x + bitmap.getWidth() * (2.0/3.0))) && (event.getY() > y && event.getY() < (y + bitmap.getHeight())));
				
	}

}
