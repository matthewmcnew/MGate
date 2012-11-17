package com.example.gateway;

import java.util.ArrayList;

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
    	
    	
        private Bitmap and,or,nand,nor,not,xor;
        private ArrayList<Bitmap> menu; 
        DisplayMetrics metrics;

        private Orange[] oranges;
        private int currentOrange = -1;

		public PlayAreaView(Context context) {       	
            super(context);
            loadImages();
            reset();
        }
		
		protected void onDraw(Canvas canvas) {
		    drawMenu(canvas,false);
			//canvas.drawBitmap(basket, getMeasuredWidth() - basket.getWidth(), getMeasuredHeight() - basket.getHeight(), null);
			Log.d("canvas height",Integer.toString(getMeasuredHeight()));
//			for(int i=0; i<3; i++){ 
//				canvas.drawBitmap(oranges[i].orange, oranges[i].x, oranges[i].y, null);
//			}
		}
		
		private void drawMenu(Canvas canvas, Boolean grey) {		 
			int distance = 0;
			for(Bitmap b : menu){
				canvas.drawBitmap(b, distance, 0, null);
				distance = distance + b.getWidth();
			}
		}
			
		public boolean onTouchEvent(MotionEvent event) {
		    int eventaction = event.getAction();

//		    switch (eventaction) {
//		        case MotionEvent.ACTION_DOWN: 
//		        	
//		        	//menu touch?
//		        	int distance = 0;
//					for(Bitmap b : menu){
//						if(){
//							
//						}
//						canvas.drawBitmap(b, distance, 0, null);
//						distance = distance + b.getWidth();
//					}
//		        	
//		        	for(int i=0; i<3; i++){ 
//						if(oranges[i].touched( event.getX(), event.getY())  ){
//							oranges[i].orange = orangeHighlight;
//							currentOrange = i;
//							this.invalidate();
//							return true;	
//						}
//					}
//
//		            break;
//
//		        case MotionEvent.ACTION_MOVE:
//		        	if(currentOrange != -1)
//		        	{
//		        		oranges[currentOrange].x = (int) event.getX();
//		        		oranges[currentOrange].y = (int) event.getY();
//		        		this.invalidate();
//		        	}
//		            // finger moves on the screen
//		            break;
//
//		        case MotionEvent.ACTION_UP:   
//		            // finger leaves the screen
//		        	if(currentOrange != -1)
//		        	{
//		        		if(inBasket(event)){
//		        			oranges[currentOrange].orange = orangePlaced;
//		        			oranges[currentOrange].freeze();
//		        		}else {
//		        			oranges[currentOrange].orange = orange;
//		        		}
//		        		this.invalidate();
//		        		currentOrange = -1;
//		        	}
//		            break;
//		    }
//
//		    // tell the system that we handled the event and no further processing is required
			return true;
		}
		
        
        
//        private boolean inBasket(MotionEvent event) {
//        	if(event.getX() > (getMeasuredWidth() - basket.getWidth())) {
//        		if(event.getY() > (getMeasuredHeight() - (3*basket.getHeight()/4))) {
//        			return true;
//        		}
//        		
//        	}
//			return false;
//		}

		private void reset() {
			and.getWidth();

//            oranges = new Orange[3];
//            
//            
//            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
//       
//            
//            
//            oranges[0] = new Orange(metrics.widthPixels/3,metrics.widthPixels/8,orange);
//            oranges[1] = new Orange(metrics.widthPixels/2,metrics.widthPixels/5,orange);
//            oranges[2] = new Orange(2*metrics.widthPixels/3,metrics.widthPixels/8,orange);
//            
        }
		
		private void loadImages() {
			metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
            and = BitmapFactory.decodeResource(getResources(),
                    R.drawable.and);
            nand = BitmapFactory.decodeResource(getResources(),
                    R.drawable.nand);
            nor = BitmapFactory.decodeResource(getResources(),
                    R.drawable.nor);
            not = BitmapFactory.decodeResource(getResources(),
                    R.drawable.not);
            or = BitmapFactory.decodeResource(getResources(),
                    R.drawable.or);
            xor = BitmapFactory.decodeResource(getResources(),
                    R.drawable.xor);
            
            menu = new ArrayList<Bitmap>();
            menu.add(and);
            menu.add(nand);
            menu.add(nor);
            menu.add(not);
            menu.add(or);
            menu.add(xor);
		}
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gateway, menu);
        return true;
    }
}
