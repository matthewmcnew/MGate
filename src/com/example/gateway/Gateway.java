package com.example.gateway;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class Gateway extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        FrameLayout frame = (FrameLayout) findViewById(R.id.graphics_holder);
        PlayAreaView image = new PlayAreaView(this);

        frame.addView(image);
    }
    
    class PlayAreaView extends View {
    	
    	
        private Bitmap orange;
        private Bitmap orangeHighlight;
        private Bitmap orangePlaced;
        private Bitmap basket;
        private Orange[] oranges;
        private int currentOrange = -1;

		public PlayAreaView(Context context) {       	
            super(context);
            reset();
        }
		
		protected void onDraw(Canvas canvas) {
		
			canvas.drawBitmap(basket, getMeasuredWidth() - basket.getWidth(), getMeasuredHeight() - basket.getHeight(), null);
			Log.d("canvas height",Integer.toString(getMeasuredHeight()));
			for(int i=0; i<3; i++){ 
				canvas.drawBitmap(oranges[i].orange, oranges[i].x, oranges[i].y, null);
			}
		}
		
		public boolean onTouchEvent(MotionEvent event) {
		    int eventaction = event.getAction();

		    switch (eventaction) {
		        case MotionEvent.ACTION_DOWN: 
		        	Log.d("canvas height",Integer.toString((int) event.getY()));
		        	for(int i=0; i<3; i++){ 
						if(oranges[i].touched( event.getX(), event.getY())  ){
							oranges[i].orange = orangeHighlight;
							currentOrange = i;
							this.invalidate();
							return true;	
						}
					}

		            break;

		        case MotionEvent.ACTION_MOVE:
		        	if(currentOrange != -1)
		        	{
		        		oranges[currentOrange].x = (int) event.getX();
		        		oranges[currentOrange].y = (int) event.getY();
		        		this.invalidate();
		        	}
		            // finger moves on the screen
		            break;

		        case MotionEvent.ACTION_UP:   
		            // finger leaves the screen
		        	if(currentOrange != -1)
		        	{
		        		if(inBasket(event)){
		        			oranges[currentOrange].orange = orangePlaced;
		        			oranges[currentOrange].freeze();
		        		}else {
		        			oranges[currentOrange].orange = orange;
		        		}
		        		this.invalidate();
		        		currentOrange = -1;
		        	}
		            break;
		    }

		    // tell the system that we handled the event and no further processing is required
		    return true; 
		}
		
        
        
        private boolean inBasket(MotionEvent event) {
        	if(event.getX() > (getMeasuredWidth() - basket.getWidth())) {
        		if(event.getY() > (getMeasuredHeight() - (3*basket.getHeight()/4))) {
        			return true;
        		}
        		
        	}
			return false;
		}

		private void reset() {
            
            orange = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            orangeHighlight = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            orangePlaced = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            basket = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);

            oranges = new Orange[3];
            
            
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            
            
            
            oranges[0] = new Orange(metrics.widthPixels/3,metrics.widthPixels/8,orange);
            oranges[1] = new Orange(metrics.widthPixels/2,metrics.widthPixels/5,orange);
            oranges[2] = new Orange(2*metrics.widthPixels/3,metrics.widthPixels/8,orange);
            
            
        }
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gateway, menu);
        return true;
    }
}
