/* 

 MultiTouchXYActivity.java:

 Copyright (C) 2011 Victor Lazzarini, Steven Yi

 This file is part of Csound Android Examples.

 The Csound Android Examples is free software; you can redistribute it
 and/or modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.   

 Csound is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with Csound; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 02111-1307 USA

 */

package com.csounds.examples.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import android.R.array;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.csounds.bindings.ui.CsoundUI;
import com.csounds.examples.BaseCsoundActivity;
import com.csounds.examples.R;
import com.csounds.examples.drumrack.DrumMachineLcdUpdater;
import com.csounds.examples.drumrack.LcdScreenView;
import com.csounds.examples.drumrack.LoadPresetFragment;
import com.csounds.examples.knobbutton.MainActivity;
import com.csounds.examples.knobbutton.RoundKnobButton;
import com.csounds.examples.knobbutton.Singleton;
import com.csounds.examples.knobbutton.RoundKnobButton.RoundKnobButtonListener;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;
import csnd6.*;



public class SynthActivity extends BaseCsoundActivity implements
CsoundObjListener, CsoundBinding, ILoaderPreset, IKeyboardUI {

	Singleton m_Inst = Singleton.getInstance();
	private static final String newLine = System.getProperty("line.separator");
	public View multiTouchView;
	public RelativeLayout keyboardLayout;
	public Button octPlusButton;
	public Button octMinusButton;
	private ToggleButton diode4;
	private ToggleButton diode3;
	SeekBar slider = null;
	SeekBar slider2 = null;
	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	CsoundMYFLTArray touchXPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray touchYPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray channelPtr[] = new CsoundMYFLTArray[2]; 
	float percentage = 1;
	int presetNbr = 1;
	CsoundMYFLTArray parameters[] = new CsoundMYFLTArray[10];;
	File txtfile;
	int octNbr = 0;
	//instanciate UI
	private KeyboardUI keyboardui = new KeyboardUI();
	boolean isOff = false;
	protected int getTouchIdAssignment() {
		for (int i = 0; i < touchIds.length; i++) {
			if (touchIds[i] == -1) {
				return i;
			}
		}
		return -1;
	}

	protected int getTouchId(int touchId) {
		for (int i = 0; i < touchIds.length; i++) {
			if (touchIds[i] == touchId) {
				return i;
			}
		}
		return -1;
	}


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		for (int i = 0; i < touchIds.length; i++) {
			touchIds[i] = -1;
			touchX[i] = -1;
			touchY[i] = -1;
		}
		multiTouchView = new View(this);
		
		setContentView(R.layout.synth2);
		multiTouchView = (RelativeLayout)findViewById(R.id.synth);
		octPlusButton = (Button)findViewById(R.id.plusoctavebutton);
		octMinusButton = (Button)findViewById(R.id.minusoctavebutton);
		diode4 = (ToggleButton)findViewById(R.id.diode4);
		diode3 = (ToggleButton)findViewById(R.id.diode3);
		LcdScreenView lcd = (LcdScreenView)findViewById(R.id.lcd_screen);
		 new DrumMachineLcdUpdater(lcd.getModel());
		 this.createLoadListener((View)lcd);
		 keyboardLayout = (RelativeLayout)findViewById(R.id.keyboard);
		keyboardLayout.setOnTouchListener(new OnTouchListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
			 */
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction() & MotionEvent.ACTION_MASK;
				float[] touchArrayDown = new float[2];
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:

					for (int i = 0; i < event.getPointerCount(); i++) {
						int pointerId = event.getPointerId(i);
						int id = getTouchId(pointerId);

						if (id == -1) {

							id = getTouchIdAssignment();

							if (id != -1) {
								touchIds[id] = pointerId;
								touchX[id] = event.getX(i)
										/ keyboardLayout.getWidth();
								touchY[id] = 1 - (event.getY(i) / keyboardLayout
										.getHeight());
								//TODO calculte interval Y
							 

								if (touchXPtr[id] != null) {
									//TODO swtich touchid
//									Log.d("touchXtouchY","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id] + " multitouchview width " + multiTouchView.getWidth() + " multitouchview width " + multiTouchView.getHeight());
//									Log.d("touchXtouchY","keyboardLayout width " + keyboardLayout.getWidth() + " keyboardLayout width " + keyboardLayout.getHeight());
									// get the key from the touch coordinates
									float[] touchArray = new float[2];
									touchArray = evaluateTouchKey(touchX[id],touchY[id]);
									touchArrayDown[0] = touchX[id];
									touchArrayDown[1] = touchY[id];									
									touchX[id] = touchArray[0];
									touchY[id] = touchArray[1];
									
//									Log.d("touchXtouchY","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id]);
									touchXPtr[id].SetValue(0, touchX[id]);
									touchYPtr[id].SetValue(0, touchY[id]);
									csoundObj.sendScore(String.format(
											"i1.%d 0 -2 %d", id, id));									
								}
						
							}
						}

					}


					break;
				case MotionEvent.ACTION_MOVE:

					for (int i = 0; i < event.getPointerCount(); i++) {
						int pointerId = event.getPointerId(i);
						int id = getTouchId(pointerId);

					

							if (id != -1) {
								touchIds[id] = pointerId;
								touchX[id] = event.getX(i)
										/ keyboardLayout.getWidth();
								touchY[id] = 1 - (event.getY(i) / keyboardLayout
										.getHeight());
								//TODO calculte interval Y
							 

								if (touchXPtr[id] != null) {
									//TODO swtich touchid
//									Log.d("aaa","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id] + " multitouchview width " + multiTouchView.getWidth() + " multitouchview width " + multiTouchView.getHeight());
//									Log.d("aaa","keyboardLayout width " + keyboardLayout.getWidth() + " keyboardLayout width " + keyboardLayout.getHeight());
									// get the key from the touch coordinates
									float[] touchArray = new float[2];
									
									touchArray = evaluateTouchKey(touchX[id],touchY[id]);
							
									
									touchX[id] = touchArray[0];
									touchY[id] = touchArray[1];
									
//									Log.d("touchXtouchY","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id]);
									touchXPtr[id].SetValue(0, touchX[id]);
									touchYPtr[id].SetValue(0, touchY[id]);
									csoundObj.sendScore(String.format(
											"i1.%d 0 -2 %d", id, id));									
								}
						
							}
						

					}
					break;
					
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_UP: {
					int activePointerIndex = event.getActionIndex();
					int pointerId = event.getPointerId(activePointerIndex);

					int id = getTouchId(pointerId);
					if (id != -1) {
						touchIds[id] = -1;
						csoundObj.sendScore(String.format("i-1.%d 0 0 %d", id,
								id));
					}
					//write file
					/*			System.out.println(String.format(
							"i1.%d 0 -2 %d", id, id));
					writeToFile(String.format(
							"i1.%d 0 -2 %d", id, id));
					 */
				}
				break;
				}


				return true;
			}

		});
		
		
		OnTouchListener octBtnOk = new OnTouchListener() {

		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		     	switch (event.getAction()){
		        case MotionEvent.ACTION_DOWN:
		        	if(octNbr<12)
		    	octNbr +=12;
		    	break;
		    	}
		     return false;
		    }
		   };
		octPlusButton.setOnTouchListener(octBtnOk);

		OnTouchListener octBtnMinus = new OnTouchListener() {

		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	switch (event.getAction()){
		        case MotionEvent.ACTION_DOWN:
		        	if(octNbr>-36)
		    	octNbr -=12;
		    	
		    	break;
		    	}
		     return false;
		    }
		   };
		   
		octMinusButton.setOnTouchListener(octBtnMinus);
		
		
		String csd = getResourceFileAsString(R.raw.multitouch_xy_kx);
		File f = createTempFile(csd);

		csoundObj.addBinding(this);

		csoundObj.startCsound(f);
	//	initknobs();
	
		initSeekBar(diode3,diode4);
		
	}
	
	public void initknobs() {
		
		  m_Inst.InitGUIFrame(this);
	        
	        RelativeLayout panel4 = (RelativeLayout) findViewById(R.id.potard4);
	        RelativeLayout panel3 = (RelativeLayout) findViewById(R.id.potard3);
	        RelativeLayout panel2 = (RelativeLayout) findViewById(R.id.potard2);
	        RelativeLayout panel = (RelativeLayout) findViewById(R.id.potard1);
	     
	      
	        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
	        RoundKnobButton rv = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff, 
	        		m_Inst.Scale(64), m_Inst.Scale(64));

	        lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 		
	        RoundKnobButton rv2 = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff, 
	        		m_Inst.Scale(64), m_Inst.Scale(64));
	        lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 		
	        RoundKnobButton rv3 = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff, 
	        		m_Inst.Scale(64), m_Inst.Scale(64));
	        lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 		
	        RoundKnobButton rv4 = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff, 
	        		m_Inst.Scale(64), m_Inst.Scale(64));
	        lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			panel.addView(rv, lp);
			panel2.addView(rv2, lp2);
			panel3.addView(rv3, lp3);
			panel4.addView(rv4, lp4);
	        
	        rv.setRotorPercentage(100);
	        rv.SetListener(new RoundKnobButtonListener() {
				public void onStateChange(boolean newstate) {
					Toast.makeText(SynthActivity.this,  "New state:"+newstate,  Toast.LENGTH_SHORT).show();
				}
				
				public void onRotate(final int percentage) {
					SynthActivity.this.percentage = percentage;
				
					
					
	//		Csound csound = csoundObj.getCsound();
	//		csound.SetChannel("freq", percentage);
					


				}
			});
	        
	      
			
	}

	public void initSeekBar(ToggleButton diode3, ToggleButton diode4) {
		
		SeekBar slider3 = (SeekBar) findViewById(R.id.slider3);
		SeekBar slider4 = (SeekBar) findViewById(R.id.slider4);
		
	//	setSeekBarValue(slider, 1, 4, 1.5);

		this.slider = (SeekBar) findViewById(R.id.slider);
		this.slider2 = (SeekBar) findViewById(R.id.slider2);
		

		setSeekBarValue(this.slider, 0, 1, 0.1);
		setSeekBarValue(this.slider2, 0, 1, 0.1);

		diode4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							String csd = getResourceFileAsString(R.raw.multitouch_xy_kfreq);
							File f = createTempFile(csd);
							Log.d("aaa", "raw multifreq diode 4 loaded");
							CsoundUI csoundUI = new CsoundUI(csoundObj);
							
							csoundUI.addSlider(SynthActivity.this.slider, "freq", 0.,
									1.);
//							csoundObj.stop();
							csoundObj.getCsound().Reset();
							csoundObj.startCsound(f);
							
						} else {
							channelPtr[0].SetValue(0, 0);
							//csoundObj.stop();
						}

					}
				});
		diode3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					String csd = getResourceFileAsString(R.raw.multitouch_xy_kfreq);
					File f = createTempFile(csd);
					Log.d("aaa", "raw multifreq diode 3 loaded");
					CsoundUI csoundUI = new CsoundUI(csoundObj);
					
					csoundUI.addSlider(SynthActivity.this.slider2, "hertz", 0.,
							1.);
//					csoundObj.stop();
					csoundObj.getCsound().Reset();
					csoundObj.startCsound(f);
					
				} else {
					channelPtr[1].SetValue(0, 0);
					//csoundObj.stop();
				}

			}
		});
	}
	@Override
	public void pitchshifterGesture() {
		keyboardui.pitchshifterGesture();
		
	}

	@Override
	public void volumeGesture() {
		keyboardui.volumeGesture();
		
	}

	@Override
	public void panGesture() {
		keyboardui.panGesture();
		
	}

	@Override
	public void keyboardGesture() {
		keyboardui.keyboardGesture();
		
	}

	@Override
	public void buttonGesture() {
		keyboardui.buttonGesture();
		
	}

	@Override
	public void potentioGesture() {
		keyboardui.potentioGesture();
		
	}
		
	public void csoundObjStarted(CsoundObj csoundObj) {}

	public void csoundObjCompleted(CsoundObj csoundObj) {
		handler.post(new Runnable() {
			public void run() {
				diode4.setChecked(false);
			}
		});
	}

	// VALUE CACHEABLE

	public void setup(CsoundObj csoundObj) {
		for (int i = 0; i < touchIds.length; i++) {
			touchXPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.x", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
			touchYPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.y", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
		}
		  channelPtr[0] = csoundObj.getInputChannelPtr(
		            String.format("freq", this.slider),
		            controlChannelType.CSOUND_CONTROL_CHANNEL
		        );
		  channelPtr[1] = csoundObj.getInputChannelPtr(
		            String.format("hertz", this.slider2),
		            controlChannelType.CSOUND_CONTROL_CHANNEL
		        );
	
	}

	public void updateValuesToCsound() {
		for (int i = 0; i < touchX.length; i++) {
			touchXPtr[i].SetValue(0, touchX[i]);
			touchYPtr[i].SetValue(0, touchY[i]);
		}
		channelPtr[0].SetValue(0, this.slider.getProgress());
		 channelPtr[1].SetValue(0, this.slider2.getProgress());
	
	}

	public void updateValuesFromCsound() {

	}

	public void cleanup() {
		for (int i = 0; i < touchIds.length; i++) {
			touchXPtr[i].Clear();
			touchXPtr[i] = null;
			touchYPtr[i].Clear();
			touchYPtr[i] = null;
		}
	}

	/* find the right key of keyboard */ 
	public float[] evaluateTouchKey(float touchX, float touchY) {
		float[] touchArray = new float[2];
		

//ZONE 2 KEYBOARD DOWN PART DOWN
		if((touchY<0.23&&touchY>0)) {
			if(touchX>0.00 && touchX < 0.11) {
				touchX = frequence[39+octNbr];
			}
			if(touchX>0.11 && touchX < 0.20) {
				touchX = frequence[41+octNbr];
			}
			if(touchX>0.20 && touchX < 0.31) {
				touchX = frequence[43+octNbr];
			}
			if(touchX>0.31 && touchX < 0.41) {
				touchX = frequence[44+octNbr];
			}
			if(touchX>0.41 && touchX < 0.52) {
				touchX = frequence[46+octNbr];
			}
			if(touchX>0.52 && touchX < 0.62) {
				touchX = frequence[48+octNbr];
			}
			if(touchX>0.62 && touchX < 0.73) {
				touchX = frequence[50+octNbr];
			}
			if(touchX>0.73 && touchX < 0.82) {
				touchX = frequence[51+octNbr];
			}
			if(touchX>0.82 && touchX < 0.92) {
				touchX = frequence[53+octNbr];
			}
			if(touchX>0.92 && touchX < 0.1) {
				touchX = frequence[55+octNbr];
			}
			
		}
		//ZONE UP KEYBOARD WHITEBLACKKEY
		if((touchY<1&&touchY>0.23)) {
			
			if(touchX>0 && touchX < 0.05882352941) {
				touchX = frequence[39+octNbr];
			}
			if(touchX>0.05882352941 && touchX < 0.11764705882) {
				touchX = frequence[40+octNbr];
			}
			if(touchX>0.11764705882 && touchX < 0.17647058823) {
				touchX = frequence[41+octNbr];
			}
			if(touchX>0.17647058823 && touchX < 0.23529411764) {
				touchX = frequence[42+octNbr];
			}
			if(touchX>0.23529411764 && touchX < 0.29411764705) {
				touchX = frequence[43+octNbr];
			}
			if(touchX>0.29411764705 && touchX < 0.35294117646) {
				touchX = frequence[44+octNbr];
			}
			if(touchX>0.35294117646 && touchX < 0.41176470587) {
				touchX = frequence[45+octNbr];
			}
			if(touchX>0.41176470587 && touchX < 0.47058823528) {
				touchX = frequence[46+octNbr];
			}
			if(touchX>0.47058823528 && touchX < 0.52941176469) {
				touchX = frequence[47+octNbr];
			}
			if(touchX>0.52941176469 && touchX < 0.5882352941) {
				touchX = frequence[48+octNbr];
			}
			if(touchX>0.5882352941 && touchX < 0.64705882351) {
				touchX = frequence[49+octNbr];
			}
			if(touchX>0.64705882351 && touchX < 0.70588235292) {
				touchX = frequence[50+octNbr];
			}
			if(touchX>0.70588235292 && touchX < 0.76470588233) {
				touchX = frequence[51+octNbr];
			}
			if(touchX>0.76470588233 && touchX < 0.82352941174) {
				touchX = frequence[52+octNbr];
			}
			if(touchX>0.82352941174 && touchX < 0.88235294115) {
				touchX = frequence[53+octNbr];
			}
			if(touchX>0.88235294115 && touchX < 0.94117647056) {
				touchX = frequence[54+octNbr];
			}
			if(touchX>0.94117647056 && touchX < 1) {
				touchX = frequence[55+octNbr];
			}
			
			
		}
		touchY = 1;
		touchArray[0] = touchX;
		touchArray[1] = touchY;
		return touchArray;
	}
	
	 float[] frequence = 
		 {27.5f,29.1353f,30.8677f,32.7032f,34.6479f,
			 36.7081f,38.8909f,41.2035f,43.6536f,46.2493f,
			 48.9995f,51.9130f,55f,58.2705f,61.7354f,65.4064f,
			 69.2957f,73.4162f,77.7817f,82.4069f,87.3071f,
			 92.4986f,97.9989f,103.826f,110f,116.541f,123.471f,
			 130.813f,138.591f,146.832f,155.563f,164.814f,
			 174.614f,184.997f,195.998f,207.652f,220f,
			 233.083f,246.942f,261.626f,277.183f,293.655f,
			 311.127f,329.628f,349.228f,369.994f,391.995f,
			 415.305f,440f,466.164f,493.883f,523.251f,
			 554.365f,587.33f,622.254f,659.255f,698.456f,
			 739.989f,783.991f,830.609f,880f,932.328f,
			 987.767f,1046.5f,1108.73f,1174.66f,1244.51f,
			 1318.51f,1396.91f,1479.98f,1567.98f,1661.22f,
			 1760f,1864.66f,1975.53f,2093f,2217.46f,
			 2349.32f,2489.02f,2637.02f,2793.83f,2959.96f,
			 3135.96f,3322.44f,3520f,3729.31f,3951.07f};
	 
	 public void createLoadListener(View loadPresetButton) {
	        loadPresetButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	Intent intent = new Intent(SynthActivity.this, ILoaderPresetActivity.class);
	            	startActivityForResult(intent, 0);

	        }
	        });
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  System.out.println(" RrequestC resultcode data " +requestCode + " " + resultCode + " " + data);
	if(requestCode == 0 && resultCode == RESULT_OK && data != null) {
		System.out.println("datagetIntExtrapresetNbr " +data.getIntExtra("presetNbr", 0));
		int presetNbr = data.getIntExtra("presetNbr", 0);
		this.setPreset(presetNbr);
		this.presetNbr = presetNbr;
	}
	}
	
	/* setPreset() 
     * @parameters int presetNbr
     */
    public void setPreset(int presetNbr) {
    	
    	String csd = getResourceFileAsString(R.raw.synth_sawtooth);
    	File f = createTempFile(csd);

   	 switch (presetNbr) {
   
		 case 0: presetNbr = 0;
		 

			csd = getResourceFileAsString(R.raw.multitouch_xy_kfreq);
			f = createTempFile(csd);
        break;
        case 1:  presetNbr = 1;
		 csd = getResourceFileAsString(R.raw.synth1);
		f = createTempFile(csd);
        break;
        case 2:  presetNbr = 2;
		 csd = getResourceFileAsString(R.raw.synth_sawtooth);
	
		f = createTempFile(csd);

        break;
        case 3:  presetNbr = 3;
		 csd = getResourceFileAsString(R.raw.synth_pulse);
	
		f = createTempFile(csd);

        break;
        case 4:  presetNbr = 4;
   		 csd = getResourceFileAsString(R.raw.synth_triangle);
   	
   		f = createTempFile(csd);

           break;
        case 5:  presetNbr = 5;
   		 csd = getResourceFileAsString(R.raw.synth_5);
   	
   		f = createTempFile(csd);

           break;
        default:break;
	
    }
 	csoundObj.stop();
	csoundObj.startCsound(f);
	Csound csound = csoundObj.getCsound();
	 csound.SetChannel("freq", 1);
	 csound.SetChannel("herz", 2);
	 System.out.println("freq   "  + csoundObj.getCsound().GetChannel("freq"));
//	csoundObj.sendScore("f1 0 16384 10 1 1   1   1    0.7 0.5   0.3  0.1");
    }

	

}
	
	
