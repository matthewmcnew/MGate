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
				flipLiteral();
				return true;
			}
		}
		return false;
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
			Gateway.p("Not selected");
			Gateway.p(input);
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
			
			if(getOutput() == 0) {
				c.drawBitmap(circles.get(3), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}else if(getOutput() == 1) {
				c.drawBitmap(circles.get(4), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}
			
		} else {
			if (input != null) {
				c.drawBitmap(circles.get(0), x-25, y + bitmap.getHeight()/2 - 12, null);
>>>>>>>>>>>>>>>>>>>> File 1
				c.drawLine(x-25, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
>>>>>>>>>>>>>>>>>>>> File 2
				c.drawLine(x-25, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
>>>>>>>>>>>>>>>>>>>> File 3
<<<<<<<<<<<<<<<<<<<<
			}
		}
		
	}
	
	public void drawWires(Canvas c) {
		if(!selected) {
			if(!(input == null || input.isDeleted())) {
				c.drawLine(x-25, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
			}
		} else {
			if (input != null) {
				c.drawLine(x-25, y + bitmap.getHeight()/2, input.getOutputX(), input.getOutputY(), paint);
				
			}
		}
	}
	
	protected void flipInPath(){
		inPath = !inPath;
		if(input != null) {
			input.flipInPath();
		}
	}
}
