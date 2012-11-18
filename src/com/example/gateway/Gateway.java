package com.example.gateway;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    	
    	
        private Bitmap and,or,nand,nor,not,xor,remove,input;
        private ArrayList<Gate> gates;
        private ArrayList<Bitmap> menu; 
        private ArrayList<Bitmap> circles; 
        DisplayMetrics metrics;
        
        Gate selected;
        Gate modifyingOutputGate;
        
        float wireX,wireY;
        float beginX,beginY;
        

        
        private int currentOrange = -1;

		public PlayAreaView(Context context) {       	
            super(context);
            loadImages();
            reset();
        }
		
		protected void onDraw(Canvas canvas) {
		    Paint paint = new Paint();
			drawMenu(canvas);

		    
			if(modifyingOutputGate != null){
				paint.setColor(Color.GREEN);
				canvas.drawLine( beginX, beginY, wireX, wireY, paint);
			}
		    
		    for(Gate g : gates){
		    	g.draw(canvas,circles);
			}	


		}
		
		private void drawMenu(Canvas canvas) {		 
			if((selected == null)) {
				int distance = 0;
				for(Bitmap b : menu){
					canvas.drawBitmap(b, distance, 0, null);
					distance = distance + b.getWidth();
				}				
			}else{
				canvas.drawBitmap(remove, ((metrics.widthPixels)/2) - remove.getWidth()/2  , 0, null);
				
				
			}

		}
		
		public void select(Gate g) {
			if(selected != null)
				selected.flipSelected();
			selected = g;
			selected.flipSelected();
		}
		
		public boolean onTouchEvent(MotionEvent event) {
		    int eventaction = event.getAction();

		    switch (eventaction) {
		        case MotionEvent.ACTION_DOWN: 
		        	
		        	//menu touch?
		        	int distance = 0;
		        	int count = -1;
		        	Gate newGate = null;
					for(Bitmap bitmap : menu){
						if(event.getX() < ( bitmap.getWidth() + distance) && event.getX() > distance && event.getY() < bitmap.getHeight() ){
							switch(count) {
							case -1:
								newGate = new Input(Input.Type.ZERO,bitmap,event.getX(),event.getY());
								break;
							case 0:
								newGate = new BinaryGate(BinaryGate.Type.AND,bitmap,event.getX(),event.getY());
								break;
							case 1:
								newGate = new BinaryGate(BinaryGate.Type.NAND,bitmap,event.getX(),event.getY());
								break;
							case 2:
								newGate = new BinaryGate(BinaryGate.Type.NOR,bitmap,event.getX(),event.getY());
								break;
							case 3:
								newGate = new UnaryGate(UnaryGate.Type.NOT,bitmap,event.getX(),event.getY());
								break;
							case 4:
								newGate = new BinaryGate(BinaryGate.Type.OR,bitmap,event.getX(),event.getY());
								break;
							case 5:
								newGate = new BinaryGate(BinaryGate.Type.XOR,bitmap,event.getX(),event.getY());
								break;
							}
							gates.add(newGate);
							select(newGate);
							break;
						}
						distance = distance + bitmap.getWidth();
						count++;
					}
					
					for(Gate g : gates){
						if(g.inputFlip(event)){
							this.invalidate();
							break;
						}
						if(g.outputTouched(event)){
							g.setWiring(true); 
							modifyingOutputGate = g;
							 
							 
							 beginX = g.getOutputX();
							 beginY = g.getOutputY();
							 
							 wireX = event.getX();
							 wireY = event.getY();
							 this.invalidate();
							 break;
						}
					}	
					
					// touch an exisiting one?
					
					for(Gate g : gates){
						if(g.inGate(event)){
							select(g);
						}
					}					
					
					this.invalidate();

		            break;

		        case MotionEvent.ACTION_MOVE:
		        	if(selected != null)
		        	{
		        		if(event.getY() < menu.get(0).getHeight()){
		        			selected.setDeleting(true);
		        		} else {
		        			selected.setDeleting(false);
		        		}
		        		
		        		selected.setX(event.getX());
		        		selected.setY(event.getY()); 
		        	}else if(modifyingOutputGate != null) {
		        		wireX = event.getX();
		        		wireY = event.getY();
		        	}
		        	this.invalidate();
		            // finger moves on the screen
		            break;

		        case MotionEvent.ACTION_UP: 
		        	if(selected != null){
		        		if(selected.isDeleting()){
		        			gates.remove(selected);
		        		} else if(selected.isWiring()) {
		        			for(Gate g : gates) {
		        				if(g.snapWire(event, selected)) {
		        					//selected.setWiring(false);
		        					invalidate();
		        					// TODO Snap to input nodes
		        					break;
		        				}
		        			}
		        		}
		        		
		        		selected.flipSelected();
		        		selected = null;
		        	}
		        	this.invalidate();
		        	
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
		            break;
		    }

		    // tell the system that we handled the event and no further processing is required
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
			
            input = BitmapFactory.decodeResource(getResources(),
                    R.drawable.inputgate);
			
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
            remove = BitmapFactory.decodeResource(getResources(),
                    R.drawable.remove);
            
            menu = new ArrayList<Bitmap>();
            menu.add(input);
            menu.add(and);
            menu.add(nand);
            menu.add(nor);
            menu.add(not);
            menu.add(or);
            menu.add(xor);
            
            gates = new ArrayList<Gate>();
            
            //InputOutput bitmaps
            circles = new ArrayList<Bitmap>();
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.inputnode));
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.inputnode0));
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.inputnode1));
            //index 3
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.outputnode0));
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.outputnode1));
            
            
		}
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gateway, menu);
        return true;
    }
}
