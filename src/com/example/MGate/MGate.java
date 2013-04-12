package com.example.MGate;

import java.util.ArrayList;

import com.example.MGate.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MGate extends Activity  {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gateway);
		FrameLayout frame = (FrameLayout) findViewById(R.id.graphics_holder);
		PlayAreaView image = new PlayAreaView(this);

		frame.addView(image);
	}

	class PlayAreaView extends View {


		private Bitmap and,or,nand,nor,not,xor,nxor,buff,remove,input;
		private ArrayList<Gate> gates;
		private ArrayList<Bitmap> menu; 
		private ArrayList<Bitmap> circles; 
		private Gate glowing =  null;
		private long time;
		DisplayMetrics metrics;

		boolean cuttingMode = false;
		float cutX,cutY;

		Gate selected;
		Gate modifyingOutputGate;

		float wireX,wireY;
		float beginX,beginY;

		//Scrollbar stuff
		int mstart = 0;
		float scrollX = 0;
		float sx = -1;
		boolean scrolling = false;

		//Zoom stuff
		float zoom = (float)(1);
		boolean zooming = false;
		float   t0x=0,
				t1x=0,
				t0y=0,
				t1y=0;
		float dx=0;
		float dy=0;
		float cx=0;
		float cy=0;
		
		//Show All stuff
		boolean showAll = false;
		boolean tap = false;
		float tz = 0;    // Target zoom
		float tdx = 0;   // Target dx
		float tdy = 0;   // Target dy
		int steps = 0;
		float ddx =0;
		float ddy = 0;
		float dz = 0;
		Thread showAllThd;
		boolean canvas_ready = false;
		
		int touchType = -1;
		
		int menuItem = -2;

		private Paint paint = new Paint();

		public PlayAreaView(Context context) {       	
			super(context);
			loadImages();
			Gate newGate = null;
			newGate = new Input(Input.Type.ZERO, BitmapFactory.decodeResource(getResources(),
					R.drawable.inputgate), 200, 300);
			gates.add(newGate);
			newGate = new BinaryGate(BinaryGate.Type.AND, BitmapFactory.decodeResource(getResources(),
					R.drawable.and), 500, 200);
			gates.add(newGate);
			newGate = new BinaryGate(BinaryGate.Type.AND, BitmapFactory.decodeResource(getResources(),
					R.drawable.and), 500, 400);
			gates.add(newGate);
			gates.get(1).setInput(gates.get(0));
			gates.get(2).setInput(gates.get(0));
			this.invalidate();
			
			/* The idea here was to have a thread that we could stop and start.
			 * Apparently you can't stop threads though, so it just runs all the time.
			 */
			showAllThd = new Thread(new Runnable() {
				public void run() {
					while(true){
						if(showAll && canvas_ready) {
							if(steps != 0) {
								dx += ddx;
								dy += ddy;
								zoom += dz;
								steps--;
							} else {
								showAll = false;
							}
							canvas_ready = false;
							postInvalidate();
						}
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			showAllThd.start();
		}

		protected void onDraw(Canvas canvas) {
			
			canvas.translate(-dx,-dy);
			canvas.scale(zoom,zoom);

			paint.setColor(Color.BLACK);


			if(modifyingOutputGate != null){
				paint.setColor(Color.GREEN);
				paint.setStrokeWidth(4);
				canvas.drawLine(beginX, beginY, wireX, wireY, paint);
			}

			//DrawWires
			for(Gate g : gates){
				g.drawWires(canvas);
			}


			Gate d = null;
			for(Gate g : gates){
				if(g.isDeleting())
					d=g;
				g.draw(canvas,circles);
			}

			canvas.scale(1/zoom,1/zoom);
			canvas.translate(dx,dy);
			drawMenu(canvas);

			//Draw deleting gates on top of menu
			if(d!=null) {
				canvas.translate(-dx,-dy);
				canvas.scale(zoom,zoom);

				d.drawWires(canvas);
				d.draw(canvas,circles);
			}
			
			canvas_ready = true;
		}

		private void drawMenu(Canvas canvas) {		 
			paint.setColor(0xffe5e5e5); //grey

			Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
			p.setStrokeWidth(2);

			canvas.drawRect(0, 0, metrics.widthPixels+10, menu.get(1).getHeight()+14, paint);

			canvas.drawLine(0, menu.get(1).getHeight()+10,metrics.widthPixels+5, menu.get(1).getHeight()+10, p);

			//Draw scrollbar
			canvas.drawLine(0, menu.get(1).getHeight()+14,metrics.widthPixels+5, menu.get(1).getHeight()+14, p);

			p.setFilterBitmap(true);
			//p.setColorFilter(new LightingColorFilter(65280,0xFFFFFF));    //white


			int distance = mstart;
			float m = 0;
			int count = -1;
			for(Bitmap b : menu){
				if(menuItem == count){
					p.setColorFilter(new LightingColorFilter(65280,65280)); // green
					canvas.drawBitmap(b, distance, 0, p);
					p.setColorFilter(new LightingColorFilter(65280,0x0000000));  
				}else {
					canvas.drawBitmap(b, distance, 0, p);
				}
				m+=b.getWidth();
				distance = distance + b.getWidth();
				count++;
			}		

			//Draw scrollbar
			float w = metrics.widthPixels;
			if(m<w) m=w;
			float ln = ((w/(float)m)*w);
			canvas.drawRect(new RectF((scrollX/w)*(w-ln), menu.get(1).getHeight()+11, w-((w-scrollX)/w)*(w-ln), menu.get(1).getHeight()+13),p);


			if((selected != null)) {
				canvas.drawRect(0, 0, metrics.widthPixels+10, menu.get(1).getHeight()+10, paint);

				if(selected.isDeleting()){
					p.setFilterBitmap(true);
					p.setColorFilter(new LightingColorFilter(65280,0xFF0000)); // RED
					canvas.drawBitmap(remove, ((metrics.widthPixels)) - remove.getWidth()  , 0, p);
				}
				canvas.drawBitmap(remove, ((metrics.widthPixels)) - remove.getWidth()  , 0, null);
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

			if(event.getPointerCount()>1) {
				zooming = true;
				if(selected != null) {
					selected.flipSelected();
					selected.setDeleting(false);
				}
				selected = null;
				menuItem = -2;
				cuttingMode = false;
			} else {
				zooming = false;
				t0x = 0;
				t1x = 0;
				t0y = 0;
				t1y = 0;
			}

			switch (eventaction) {
			case MotionEvent.ACTION_DOWN:
				
				touchType = -1;
				
				//menu touch?
				int distance = mstart;
				int count = -1;
				//Gate newGate = null;
				for(Bitmap bitmap : menu){
					if(event.getX() < ( bitmap.getWidth() + distance) && event.getX() > distance && event.getY() < bitmap.getHeight() ){
						menuItem = count;
						touchType = 1;
						break;
					}
					distance = distance + bitmap.getWidth();
					count++;
				}

				//Scroll touch?
				if((touchType == -1 || touchType == 1)&& /* (event.getY() > menu.get(1).getHeight()+12) &&*/ (event.getY() < menu.get(1).getHeight()+30)) {
					scrolling = true;
					sx = event.getX();
					touchType = 2;
				}

				if(touchType == -1) {
					// touch an input or output
					for(Gate g : gates){
						//This needs to be cleaned up someday.  
						Gate testingGate = g.disconnectWire((event.getX()+dx)/zoom, (event.getY()+dy)/zoom);
						if(testingGate != null) {
							modifyingOutputGate = testingGate;
							modifyingOutputGate.flipWiring();
							g.clearInput(modifyingOutputGate);

							beginX = modifyingOutputGate.getOutputX();
							beginY = modifyingOutputGate.getOutputY();

							wireX = (event.getX()+dx)/zoom;
							wireY = (event.getY()+dy)/zoom;
							
							touchType = 3;
							
							this.invalidate();
							return true;
						}

						else if(g.inputFlip((event.getX()+dx)/zoom, (event.getY()+dy)/zoom)){
							
							touchType = 4;
							
							this.invalidate();
							break;
						}

						else if(g.outputTouched((event.getX()+dx)/zoom, (event.getY()+dy)/zoom)){
							
							touchType = 5;
							
							g.flipWiring();
							modifyingOutputGate = g;


							beginX = g.getOutputX();
							beginY = g.getOutputY();

							wireX = (event.getX()+dx)/zoom;
							wireY = (event.getY()+dy)/zoom;
							this.invalidate();
							break;
						}
					}
				}
				
				if(touchType == -1) {
					// touch an exisiting one?
					for(Gate g : gates){
						if(g.inGate((event.getX()+dx)/zoom, (event.getY()+dy)/zoom)){
							select(g);
							touchType = 6;
						}
					}
				}

				this.invalidate();
				
				//Show All code
				if(touchType == -1) {
					if(tap && (System.nanoTime() < time + 300000000.0) && gates.size() > 0) {
						//Double Tap, zoom out to show everything
						
						touchType = 7;
						
						showAll = true;
						float   maxX = gates.get(0).getX(),
								minX = gates.get(0).getX(),
								maxY = gates.get(0).getY(),
								minY = gates.get(0).getY();
						
						tdx = 0;
						tdy = 0;
						tz = 0;
						
						// Calculate the collective midpoint of all gates
						for(Gate g : gates) {
							float x = g.getX()+(g.getBitmap().getWidth()/1);
							float y = g.getY()+(g.getBitmap().getHeight()/2);
							
							if(x<minX)
								minX = x;
							if(x>maxX)
								maxX = x;
							if(y<minY)
								minY = y;
							if(y>maxY)
								maxY = y;
						}
						
						minX -= menu.get(0).getWidth();
						maxX += menu.get(0).getWidth();
						minY -= menu.get(0).getHeight();
						maxY += menu.get(0).getHeight();
						
						tdx = (maxX+minX)/2;
						tdy = (maxY+minY)/2;
						
						// Transform the coordinates in the canvas to the coordinates on the screen
						maxX = ((maxX+dx)*zoom);
						maxY = ((maxY+dy)*zoom);
						minX = ((minX+dx)*zoom);
						minY = ((minY+dy)*zoom);
						
						//Based on the current size of the display, calculate the zoom needed to fit everything
						float tzx = (metrics.widthPixels/(maxX-minX))*zoom;
						float tzy = ((metrics.heightPixels-200)/(maxY-minY))*zoom;
						
						tz = Math.min(tzx,tzy);
						
						// Calculate a new dx and new dy to move the screen 
						tdx = (tdx*tz)-(metrics.widthPixels/2);
						tdy = (tdy*tz)-((metrics.heightPixels/2)+30);
						
						// ddx, ddy, and dz are the "step sizes" as the screen zooms and moves
						ddx = (tdx - dx)/10;
						ddy = (tdy - dy)/10;
						dz = (tz - zoom)/10;
						
						showAll = true;
						tap = false;
						steps = 10;
					} else {
						tap = false;
					}
				}
				
				if(touchType == -1){
					cuttingMode = true;
					cutX = event.getX();
					cutY = event.getY();

					//Show All code
					tap = true;
					time = System.nanoTime();
					
					touchType = 8;
				}
				
				break;

			case MotionEvent.ACTION_MOVE:

				if(zooming) {
					//Oh boy.
					//Get new coordinates of pointers
					float nt0x = event.getX(0);
					float nt0y = event.getY(0);
					float nt1x = event.getX(1);
					float nt1y = event.getY(1);

					float distOld = (float)Math.sqrt(((t0x-t1x)*(t0x-t1x))+((t0y-t1y)*(t0y-t1y)));
					float distNew = (float)Math.sqrt(((nt0x-nt1x)*(nt0x-nt1x))+((nt0y-nt1y)*(nt0y-nt1y)));
					if(distOld != 0) {
						if(Math.abs(distNew-distOld)>6) {
							//Zooming
							double zr = 1; //1.01 - 1.09
							float z2 = zoom;
							if(distNew>distOld) {
								//This was not a pinch, so zoom in
								z2 = (float)(zoom*(zr+(.09*((distNew-distOld)/15))));
							} else if(distNew<distOld) {
								z2= (float)(zoom/(zr+(.09*((distOld-distNew)/15))));
							}

							//We now have to translate the canvas to center on the zoomed out-point

							cx = (nt0x+nt1x)/2;
							cy = (nt1y+nt0y)/2;

							dx = (((cx*(z2-zoom))+(dx*z2))/zoom);
							dy = (((cy*(z2-zoom))+(dy*z2))/zoom);;
							zoom = z2;
						} else {
							//Moving
							cx = (nt0x+nt1x)/2;
							cy = (nt1y+nt0y)/2;
							
							float cxOld = (t0x+t1x)/2;
							float cyOld = (t0y+t1y)/2;

							dx -= cx - cxOld;
							dy -= cy - cyOld;
						}
					}

					t0x = nt0x;
					t0y = nt0y;
					t1x = nt1x;
					t1y = nt1y;
				} else {

				if(cuttingMode) {
					for(Gate g : gates){ 
						g.deleteWires((cutX+dx)/zoom, (cutY+dy)/zoom, (event.getX()+dx)/zoom, (event.getY()+dy)/zoom);
					}
				}

				//Scrolling
				float d = 0;
				for(Bitmap bitmap : menu){
					d = d + bitmap.getWidth();
				}
				if(scrolling) {
					float w = metrics.widthPixels;
					if(d<w) d=w;
					float ln = ((w/(float)d)*w);

					scrollX+=w*(event.getX()-sx)/(w-ln);
					if(scrollX < 0) scrollX=0;
					else if(scrollX > w) scrollX = w;
					else sx = event.getX();

					mstart = (int)((scrollX/metrics.widthPixels)*(metrics.widthPixels-d));
					if(mstart > 0) mstart = 0;
				}


				if(menuItem != -2 && (event.getY() > (menu.get(menuItem+1).getHeight()+menu.get(menuItem+1).getHeight()/2))){
					scrolling = false;
					Gate newGate = null;
					switch(menuItem) {
					case -1:
						newGate = new Input(Input.Type.ZERO,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 0:
						newGate = new BinaryGate(BinaryGate.Type.AND,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 1:
						newGate = new BinaryGate(BinaryGate.Type.OR,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 2:
						newGate = new UnaryGate(UnaryGate.Type.NOT,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 3:
						newGate = new BinaryGate(BinaryGate.Type.XOR,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 4:
						newGate = new BinaryGate(BinaryGate.Type.NAND,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 5:
						newGate = new BinaryGate(BinaryGate.Type.NOR,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 6:
						newGate = new BinaryGate(BinaryGate.Type.EQUIV,menu.get(menuItem+1),event.getX(),event.getY());
						break;
					case 7:
						newGate = new UnaryGate(UnaryGate.Type.BUFF,menu.get(menuItem+1),event.getX(),event.getY());
						break;	
					}
					gates.add(newGate);
					select(newGate);
					cuttingMode = false;

					menuItem = -2;


				}

				if(selected != null)
				{
					//Delete that crap
					if(event.getY() < (menu.get(1).getHeight()+30 + selected.bitmap.getHeight()/2)){
						selected.setDeleting(true);
					} else {
						selected.setDeleting(false);
					}

					selected.setX((event.getX()+dx)/zoom - selected.bitmap.getWidth()/2);
					selected.setY((event.getY()+dy)/zoom - selected.bitmap.getHeight()/2); 


					if(modifyingOutputGate != null) {
						beginX = modifyingOutputGate.getOutputX();
						beginY = modifyingOutputGate.getOutputY();
					}


					if(glowing != null) {
						if(!selected.isConnecting(glowing) ) {
							if(System.currentTimeMillis() > (time + 375)) {
								glowing.setGlowing(false);	
								selected.addInput(glowing);		        					
							}
							else {
								glowing.setGlowing(false);
								glowing = null;
							}
						}
					} else {
						for(Gate g : gates){ 
							if(selected.isConnecting(g)) {
								g.setGlowing(true);
								glowing = g;
								time = System.currentTimeMillis();
								break;
							}
						}
					}

				}else if(modifyingOutputGate != null) {
					wireX = (event.getX()+dx)/zoom;
					wireY = (event.getY()+dy)/zoom;
				}
				}
				this.invalidate();
				// finger moves on the screen
				break;
				//
			case MotionEvent.ACTION_UP:
				cuttingMode = false;
				scrolling = false;
				zooming = false;

				if(glowing != null) {
					if(System.currentTimeMillis() > (time + 500)) {
						selected.addInput(glowing);		
					}
					glowing.setGlowing(false);
					glowing = null;
				}


				if(menuItem != -2){
					menuItem = -2;
				}

				if(modifyingOutputGate != null) {
					for(Gate g : gates) {
						p("Testing gate: "+g);
						if(g.snapWire((event.getX()+dx)/zoom, (event.getY()+dy)/zoom, modifyingOutputGate)) {	        					
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
			nxor = BitmapFactory.decodeResource(getResources(),
					R.drawable.nxor);
			buff = BitmapFactory.decodeResource(getResources(),
					R.drawable.buff); 
			remove = BitmapFactory.decodeResource(getResources(),
					R.drawable.trashcanremove);

			menu = new ArrayList<Bitmap>();
			menu.add(input);
			menu.add(and);
			menu.add(or);
			menu.add(not);
			menu.add(xor);
			menu.add(nand);
			menu.add(nor);
			menu.add(nxor);
			menu.add(buff);

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
			circles.add(BitmapFactory.decodeResource(getResources(),
					R.drawable.halo));


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
