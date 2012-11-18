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
				flipLiteral(0);
				return true;
			}
		}
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y+bitmap.getHeight()/2) && event.getY() < (y+bitmap.getHeight()+5) ){
				flipLiteral(1);
				return true;
			}
		}
		return false;
	}
	
	public boolean snapWire(MotionEvent event, Gate selected) {
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()/2) ){
				input1 = selected;
				return true;
			}
		}
		if(event.getX() > (x-35) && event.getX() < (x+22) ){
			if(event.getY() > (y+bitmap.getHeight()/2) && event.getY() < (y+bitmap.getHeight()+5) ){
				input2 = selected;
				return true;
			}
		}
		return false;
	}
	
	public void draw(Canvas c,ArrayList<Bitmap> circles) {
		super.draw(c,circles);
		if(!selected) {
			if(input1 == null) {
				if(literal1 == 0) {
					c.drawBitmap(circles.get(1), x-25, y + 7, null);
				}else if(literal1 == 1) {
					c.drawBitmap(circles.get(2), x-25, y + 7, null);
				}
			}

			if(input2 == null) {
				if(literal2 == 0) {
					c.drawBitmap(circles.get(1), x-25, y + 40, null);
				}else if(literal2 == 1) {
					c.drawBitmap(circles.get(2), x-25, y + 40, null);
				}
			}
			
			if(getOutput() == 0) {
				c.drawBitmap(circles.get(3), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}else if(getOutput() == 1) {
				c.drawBitmap(circles.get(4), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}
			
		}
		
		
	}
}
