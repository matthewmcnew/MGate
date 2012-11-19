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
	protected abstract void flipInPath();
	public abstract Gate disconnectWire(MotionEvent event);
	
	protected Bitmap bitmap;
	protected float x;
	protected float y;
	protected boolean selected = false;
	protected boolean deleting = false;
	protected boolean wiring = false;
	protected boolean deleted = false;
	protected boolean inPath = false;
		
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
	
	public abstract boolean inputFlip(MotionEvent event);
	public abstract boolean snapWire(MotionEvent event, Gate input);
	
	
	
	public boolean outputTouched(MotionEvent event) {
		if(event.getX() > (x + bitmap.getWidth() + -35) && event.getX() < (x+ bitmap.getWidth() + 22) ){
			if(event.getY() > (y-5) && event.getY() < (y+bitmap.getHeight()) ){
				return true;
			}
		}	
		return false;
	}
	
	public void draw(Canvas c,ArrayList<Bitmap> circles) {
		Gateway.p("Drawing gate...");
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
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public boolean inGate(MotionEvent event){
		return((event.getX() > x && event.getX() < (x + bitmap.getWidth() * (3.0/4.0))) && (event.getY() > y && event.getY() < (y + bitmap.getHeight())));
				
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
		flipInPath();
		this.wiring = !wiring;
	}
	
	public void clearInput(Gate input){
		return;
	}
	
}
