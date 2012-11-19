package com.example.gateway;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;


public class Input extends Gate {
	
	public enum Type {ONE, ZERO};
	
	private Type type;
	
	public Input() {
		type = Type.ZERO;
	}
	
	public Input(Type t, Bitmap b, float ix, float iy) {
		bitmap = b;
		type = t;
		x = ix;
		y = iy;
	}
	
	public int out() {
		switch(type) {
		case ONE:
			return 1;
		case ZERO:
			return 0;
		default:
			return 0;
		}
	}

	public int getOutput() {
		return out();
	}
	
	public boolean inPath(Gate g) {
		return false;
	}

	public ArrayList<Gate> getInputs() {
		return null;
	}
	
	public Gate clone() {
		return new Input(this.type, this.bitmap, this.x, this.y );
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
		return false;
	}
	
	public void flipLiteral() {
		if(type == Type.ZERO) {
			type = Type.ONE;
		} else {
			type = Type.ZERO;
		}
	}
	
	public void draw(Canvas c,ArrayList<Bitmap> circles) {
		super.draw(c,circles);
		if(!selected) {

			if(type == Type.ZERO) {
				c.drawBitmap(circles.get(1), x-25, y + bitmap.getHeight()/2 - 12, null);
			}else if(type == Type.ONE) {
				c.drawBitmap(circles.get(2), x-25, y + bitmap.getHeight()/2 - 12, null);
			}

			
			if(getOutput() == 0) {
				c.drawBitmap(circles.get(3), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}else if(getOutput() == 1) {
				c.drawBitmap(circles.get(4), x+bitmap.getWidth()-19, y + bitmap.getHeight()/2 - 12, null);
			}
			
		}
		
	}

	public String getHelp() {
		String str = "";
		str += "This is an INPUT gate.\n";
		str += "INPUT gates will only output the value they recieve as input. ";
		str += "Touch the blue input circle to change the value.";
		return str;
	}
	
}
