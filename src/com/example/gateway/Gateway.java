package com.example.gateway;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class Gateway extends Activity  {
	

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
        
        private Paint paint = new Paint();
        
		public PlayAreaView(Context context) {       	
            super(context);
            loadImages();
        }
		
		protected void onDraw(Canvas canvas) {
			drawMenu(canvas);
			
			if(modifyingOutputGate != null){
				paint.setColor(Color.GREEN);
				canvas.drawLine( beginX, beginY, wireX, wireY, paint);
			}
			
			//DrawWires
		    for(Gate g : gates){
		    	g.drawWires(canvas);
			}
			
		    
		    for(Gate g : gates){
		    	g.draw(canvas,circles);
			}

		}
		
		private void drawMenu(Canvas canvas) {		 
			paint.setColor(Color.BLACK);
			//canvas.drawRect(left, top, right, bottom, paint)
			canvas.drawRect(0, 0, metrics.widthPixels+10, menu.get(1).getHeight(), paint);
			
			if((selected == null)) {
				
				Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
				p.setFilterBitmap(true);
				p.setColorFilter(new LightingColorFilter(65280,0xFFFFFF));    //GREEN
				
				int distance = 0;
				for(Bitmap b : menu){
					canvas.drawBitmap(b, distance, 0, p);
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
					
					// touch an input or output
					for(Gate g : gates){
						//This needs to be cleaned up someday.  
						Gate testingGate = g.disconnectWire(event);
						if(testingGate != null) {
							 modifyingOutputGate = testingGate;
							 modifyingOutputGate.flipWiring();
							 g.clearInput(modifyingOutputGate);
							 
							 beginX = modifyingOutputGate.getOutputX();
							 beginY = modifyingOutputGate.getOutputY();

							 wireX = event.getX();
							 wireY = event.getY();
							 
							 this.invalidate();
							 return true;
							 
						}
						
						else if(g.inputFlip(event)){
							
							this.invalidate();
							break;
						} 
						
						if(g.outputTouched(event)){
							g.flipWiring();
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
		        		
		        		selected.setX(event.getX() - selected.bitmap.getWidth()/2);
		        		selected.setY(event.getY() - selected.bitmap.getHeight()/2); 
		        		
		        		if(modifyingOutputGate != null) {
		        			beginX = modifyingOutputGate.getOutputX();
		        			beginY = modifyingOutputGate.getOutputY();
		        		}
		        	}else if(modifyingOutputGate != null) {
		        		wireX = event.getX();
		        		wireY = event.getY();
		        	}
		        	this.invalidate();
		            // finger moves on the screen
		            break;

		        case MotionEvent.ACTION_UP:
		        	
	        		if(modifyingOutputGate != null) {
	        			for(Gate g : gates) {
	        				p("Testing gate: "+g);
	        				if(g.snapWire(event, modifyingOutputGate)) {	        					
	        					break;
	        				}
	        			}
    					modifyingOutputGate.flipWiring();
    					modifyingOutputGate = null;
    					invalidate();
	        		}
	        		
		        	if(selected != null){
		        		if(selected.isDeleting()){
		        			gates.remove(selected);
		        			selected.setDeleted(true);
		        			modifyingOutputGate = null;
		        		}
		        		
		        		selected.flipSelected();
		        		selected = null;
		        	}
	        		
		        	this.invalidate();

		            break;
		    }

		    // tell the system that we handled the event and no further processing is required
			return true;
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
            circles.add(BitmapFactory.decodeResource(getResources(),
                    R.drawable.cantwire1));
            
            
		}
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gateway, menu);
        return true;
    }
    
    public static void p(Object o) {
    	//System.out.println(o);
    }
}
