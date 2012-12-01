package com.example.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class Chip extends Gate{
	
	private ArrayList<Gate> inputs;
	private ArrayList<Integer> literals;
	private Map<String, Integer> logic;

	public Chip() {
		inputs = new ArrayList<Gate>();
		literals = new ArrayList<Integer>();
		logic = new HashMap<String, Integer>();
	}
	
	public int getOutput() {
		String in = "";
		for(int i=0; i < inputs.size(); i++) {
			Gate g = inputs.get(i);
			if(g == null) {
				in += literals.get(i);
			} else {
				in += g.getOutput();
			}
		}
		return logic.get(in);
	}

	public boolean inPath(Gate g) {
		if(this == g) {
			return true;
		} else {
			
			for(Gate input : inputs) {
				if(input != null) {
					if(input == g) {
						return true;
					} else {
						if(input.inPath(g)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	public Gate clone() {
		Chip newChip = new Chip();
		newChip.setLogic(logic);
		return newChip;
	}

	public ArrayList<Gate> getInputs() {
		return inputs;
	}

	public void drawWires(Canvas c) {
		
	}

	public String getHelp() {
		return "";
	}

	protected void flipInPath() {
		
	}

	public Gate disconnectWire(MotionEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean inputFlip(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean snapWire(MotionEvent event, Gate input) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setLogic(Map<String, Integer> map) {
		logic = map;
	}
	
	public void setLogic(Gate g) {
		ArrayList<Gate> newIns = g.getInputs();
		if(newIns.size() == 0) {
			
		} else {
			
		}
	}

	@Override
	protected void setInPath(boolean state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Gate> getBaseInputs() {
		// TODO Auto-generated method stub
		return null;
	}

}
