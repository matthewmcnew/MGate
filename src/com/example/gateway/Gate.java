package com.example.gateway;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.view.MotionEvent;

public abstract class Gate {

	public abstract int getOutput();
	public abstract boolean inPath(Gate g);
	public abstract Gate clone();
	public abstract ArrayList<Gate> getInputs();
	public abstract void drawWires(Canvas c);
	public abstract String getHelp();
	protected abstract void setInPath(boolean state);
	public abstract Gate disconnectWire(MotionEvent event);
	public boolean isInput() { return false; }
	public abstract ArrayList<Gate> getBaseInputs();
	public abstract boolean isConnecting(Gate g);
	public abstract void deleteWires(float x1, float y1, float x2, float y2);
	
	protected Bitmap bitmap;
	protected float x;
	protected float y;
	protected boolean selected = false;
	protected boolean deleting = false;
	protected boolean wiring = false;
	protected boolean deleted = false;
	protected boolean inPath = false;
	protected boolean glowing = false;
		
	protected static Paint paint = new Paint();
	
	public void setInput(Gate g) {
		System.out.print("Warning: gate does not take single input");
	}
	
	public void addInput(Gate g) {
		System.out.print("Warning: gate does not take input");
	}
	
	public void setInput(Gate g1, Gate g2){
		System.out.print("Warning: gate does not take double input");
	}
	
	public abstract boolean inputFlip(float eX, float eY);
	public abstract boolean snapWire(float eX, float eY, Gate input);
	
	
	
	public boolean outputTouched(float eX, float eY) {
		if(eX > (x + bitmap.getWidth() + -35) && eX < (x+ bitmap.getWidth() + 22) ){
			if(eY > (y-5) && eY < (y+bitmap.getHeight()) ){
				return true;
			}
		}	
		return false;
	}
	
	public void draw(Canvas c,ArrayList<Bitmap> circles) {
		Gateway.p("Drawing gate...");
		paint.setStrokeWidth(3);
		if(deleting) {
			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
			p.setFilterBitmap(true);
			p.setColorFilter(new LightingColorFilter(65280,0xFF0000)); // RED
			c.drawBitmap(bitmap, x, y, p);
		} else if(selected) {
			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
			p.setFilterBitmap(true);
			p.setColorFilter(new LightingColorFilter(65280,65280));    //GREEN
			c.drawBitmap(bitmap, x, y, p);
		} else if(wiring) {
			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
			p.setFilterBitmap(true);
			p.setColorFilter(new LightingColorFilter(65280,-255));    //YELLOW
			c.drawBitmap(bitmap, x, y, p);
		} else {
			c.drawBitmap(bitmap, x, y, null);
		}
	}
	
	public float getOutputX() {
		Gateway.p("Getting output x...");
		 return x+bitmap.getWidth()-6;
	}
	
	public float getOutputY() {
		Gateway.p("Getting output y...");
		 return y + bitmap.getHeight()/2;	
	}
	
	// 
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public boolean inGate(float eX, float eY){
		return((eX > x && eX < (x + bitmap.getWidth() * (3.0/4.0))) && (eY > y && eY < (y + bitmap.getHeight())));
				
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void flipSelected() {
		selected = !selected;
	}
	public void setDeleting(boolean b) {
		deleting = b;
	}
	public void setDeleted(boolean b) {
		deleted = b;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	public boolean isDeleting() {
		return deleting;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public boolean isWiring() {
		return wiring;
	}
	public void flipWiring() {
		this.wiring = !wiring;
		setInPath(this.wiring);
	}
	
	public void clearInput(Gate input){
		return;
	}
	
	public void setGlowing(boolean glow){
		glowing = glow;
	}
	
}
