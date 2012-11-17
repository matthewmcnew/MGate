package com.example.gateway;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.util.Log;


public class Orange {
	
	 public Bitmap orange;
	 public int x;
	 public int y;
	 private boolean frozen = false;

	public Orange(int x,int y, Bitmap orange) {
        this.orange = orange;
		this.x = x;
		this.y = y;
	}

	public boolean touched(float xT, float yT) {
		  if(frozen)
		  {
			  return false;
		  }
		  if (xT > x && xT <  (x + orange.getWidth()) )
          {
          	if (yT > y && yT < (y + orange.getHeight()) ) {
          		return true;
          	}
         } 

		return false;
	}

	public void freeze() {
		frozen = true;
		
	}

}
