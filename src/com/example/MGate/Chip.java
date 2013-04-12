package com.example.MGate;

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
			for(Gate in : newIns) {
				if(in.isInput()) {
					inputs.add(in);
				}
			}
		}
	}


	@Override
	protected void setInPath(boolean state) {
		inPath = state;
		
	}



	public ArrayList<Gate> getBaseInputs() {
		//WRONG
		return getInputs();

	}
	
	public boolean isConnecting(Gate g){
		return false;
	}
	
	public void deleteWires(float x1, float y1, float x2, float y2) {}

	@Override
	public boolean inputFlip(float eX, float eY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean snapWire(float eX, float eY, Gate input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Gate disconnectWire(float eX, float eY) {
		// TODO Auto-generated method stub
		return null;
	}




}
