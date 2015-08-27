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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.csounds.examples.BaseCsoundActivity;
import com.csounds.examples.R;
import com.csounds.examples.drumrack.LoadPresetFragment;

import android.widget.Button;
import android.widget.RelativeLayout;
import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;


public class MultiTouchXYActivity extends BaseCsoundActivity implements
CsoundObjListener, CsoundBinding, ILoaderPreset {

	private static final String newLine = System.getProperty("line.separator");
	public View multiTouchView;
	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	CsoundMYFLTArray touchXPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray touchYPtr[] = new CsoundMYFLTArray[10];
	File txtfile;


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
		/*		File outputFile = new File(DIR_NAME);
		File outputDir = this.getCacheDir(); // context being the Activity pointer
		try {
			outputFile = File.createTempFile("temp", "txt", outputDir);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ContextWrapper cw = new ContextWrapper(this);
		File directory = cw.getExternalFilesDir(null);
		txtfile = new File(directory,"temp.txt");
		 */
		multiTouchView = new View(this);
		setContentView(R.layout.multitouchxy);
		multiTouchView = (RelativeLayout)findViewById(R.id.multitouchxy);
		 Button loadPresetButton = (Button)findViewById(R.id.load_button);
		this.createLoadListener(loadPresetButton);
		multiTouchView.setOnTouchListener(new OnTouchListener() {
		
			/* (non-Javadoc)
			 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
			 */
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction() & MotionEvent.ACTION_MASK;
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
										/ multiTouchView.getWidth();
								touchY[id] = 1 - (event.getY(i) / multiTouchView
										.getHeight());
								//TODO calculte interval Y
								int a = Math.round(id/440);

								System.out.println("y " + a);

								if (touchXPtr[id] != null) {
									//TODO swtich touchid
									Log.d("touchXtouchY","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id] + " multitouchview width " + multiTouchView.getWidth() + " multitouchview width " + multiTouchView.getHeight());

									// get the key from the touch coordinates
									float[] touchArray = new float[2];
									touchArray = evaluateTouchKey(touchX[id],touchY[id]);
									touchX[id] = touchArray[0];
									touchY[id] = touchArray[1];
									
									Log.d("touchXtouchY","touchX[id]) " + touchX[id] + " touchY[id]) " + touchY[id]);
									touchXPtr[id].SetValue(0, touchX[id]);
									touchYPtr[id].SetValue(0, touchY[id]);
									csoundObj.sendScore(String.format(
											"i1.%d 0 -2 %d", id, id));									
								}
								//write file
								/*				System.out.println(String.format(
										"i1.%d 0 -2 %d", id, a));
							writeToFile(String.format(
										"i1.%d 0 -2 %d", id, a));
								 */
							}
						}

					}


					break;
				case MotionEvent.ACTION_MOVE:

					for (int i = 0; i < event.getPointerCount(); i++) {
						int pointerId = event.getPointerId(i);
						int id = getTouchId(pointerId);

						if (id != -1) {
							touchX[id] = event.getX(i)
									/ multiTouchView.getWidth();
							touchY[id] = 1 - (event.getY(i) / multiTouchView
									.getHeight());

						}

						//write file
						/*			System.out.println(String.format(
								"i1.%d 0 -2 %d", id, id));
						writeToFile(String.format(
								"i1.%d 0 -2 %d", id, id));
						 */
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

		setContentView(multiTouchView);

		String csd = getResourceFileAsString(R.raw.multitouch_xy_kx);
		File f = createTempFile(csd);

		csoundObj.addBinding(this);

		csoundObj.startCsound(f);
	}


	public void csoundObjStarted(CsoundObj csoundObj) {}

	public void csoundObjCompleted(CsoundObj csoundObj) {}

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
	}

	public void updateValuesToCsound() {
		for (int i = 0; i < touchX.length; i++) {
			touchXPtr[i].SetValue(0, touchX[i]);
			touchYPtr[i].SetValue(0, touchY[i]);
		}

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

	private byte[] buffer = null;
	private String DIR_NAME = "raw/temp.txt";
	/*
	 * transfert /raw/recordX.wav file to intern storage
	 */
	public void storeRAWFilesToInternalStorage() throws IOException
	{


		InputStream in = getResources().openRawResource(R.raw.record01);
		if (Environment.MEDIA_MOUNTED
				.equals(Environment.getExternalStorageState())) {
			File file = new File(this.getFilesDir(), "csoundtracks");
			ContextWrapper cw = new ContextWrapper(this);
			File directory = cw.getExternalFilesDir(null);
			File wavfile = new File(directory,"record01.wav");
			if (!file.mkdirs()) {
				Log.e("logtag", "Directory not created");
			}
			System.out.println(wavfile.getAbsolutePath());
			System.out.println("wavfile" + wavfile + " directory " + directory);
			if (!wavfile.exists()) {
				Log.e("logtag", "wave file not created");
				try {

					wavfile.createNewFile();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!wavfile.exists()) {
				Log.e("logtag", "wave2 file not created");

			}

			FileOutputStream out = new FileOutputStream(wavfile);

			byte[] buff = new byte[1024];
			int read = 0;

			try {
				while ((read = in.read(buff)) > 0) {
					out.write(buff, 0, read);
				}
			} finally {
				in.close();
				out.close();
			}

		}
	}

	/* writer from csound data into txt file */
	private void writeToFile(String data) {
		try {
			if(!txtfile.exists()) {
				Log.d("txtfile","txtfile doesn't exist");
				ContextWrapper cw = new ContextWrapper(this);
				File directory = cw.getExternalFilesDir(null);
				txtfile = new File(directory,"temp.txt");
			} 
			System.out.println("zz" +txtfile);
			FileOutputStream outStream = new FileOutputStream(txtfile, true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outStream);

			String output = String.format(data);
			//        BufferedWriter oFile = new BufferedWriter(new OutputStreamWriter(
			//            new FileOutputStream("test.txt"), "UTF-16"));
			outputStreamWriter.append("\r\n");
			outputStreamWriter.append(output);
			outputStreamWriter.close();

		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		} 
	}

	public void getTouchedKey() {

	}
	
	/* find the right key of keyboard */ 
	public float[] evaluateTouchKey(float touchX, float touchY) {
		float[] touchArray = new float[2];
		
		// ZONE 1 VOID DOWN
		if(touchY>0 && touchY < 0.193989907103825) {
			touchX = 0f;
		}
//ZONE 2 KEYBOARD DOWN PART DOWN
		if(touchY>0.193989907103825 && touchY < 0.30327868852) {
			if(touchX>0 && touchX < 0.14285) {
				touchX = 261.63f;
			}
			if(touchX>0.14285 && touchX < 0.28571) {
				touchX = 293.67f;
			}
			if(touchX>0.28571 && touchX < 0.42857142857) {
				touchX = 329.63f;
			}
			if(touchX>0.42857142857 && touchX < 0.57142857142) {
				touchX = 349.23f;
			}
			if(touchX>0.57142857142 && touchX < 0.71428571428) {
				touchX = 392.00f;
			}
			if(touchX>0.71428571428 && touchX < 0.85714285714) {
				touchX = 440.00f;
			}
			if(touchX>0.85714285714 && touchX < 1) {
				touchX = 493.88f;
			}
			touchY = 1;
		}
//KEYBOARD DOWN PART UP WHITE/BLACK KEY								
		if(touchY>0.30327868852 && touchY < 0.44808743169399) {
			if(touchX>0 && touchX < 0.10666) {
				touchX = 261.63f;
			}
			if(touchX>0.10666 && touchX < 0.18) {
				touchX = 277.18f;
			}
			if(touchX>0.18 && touchX < 0.24666) {
				touchX = 293.67f;
			}
			if(touchX>0.24666 && touchX < 0.32) {
				touchX = 311.13f;
			}
			if(touchX>0.32 && touchX < 0.42666) {
				touchX = 329.63f;
			}
			if(touchX>0.42666 && touchX < 0.53333) {
				touchX = 349.23f;
			}
			if(touchX>0.53333 && touchX < 0.60666) {
				touchX = 369.99f;
			}
			if(touchX>0.60666 && touchX < 0.67333) {
				touchX = 392.00f;
			}
			if(touchX>0.67333 && touchX < 0.74666) {
				touchX = 415.30f;
			}
			if(touchX>0.74666 && touchX < 0.82) {
				touchX = 440.00f;
			}
			if(touchX>0.82 && touchX < 0.89333) {
				touchX = 466.16f;
			}
			if(touchX>0.89333 && touchX < 1) {
				touchX = 493.88f;
			}
			// set touchY to 1:1 ratio to calculate the frequency in csound script
			touchY = 1;
		}
//ZONE VOID  MID
		if(touchY>0.44808743169399 && touchY < 0.60245901639344) {
			touchX = 0f;
		}
//ZONE KEYBOARD 2 UP DOWN
		if(touchY>0.60245901639344 && touchY < 0.71174863388) {
			
			if(touchX>0 && touchX < 0.14285) {
				touchX = 523.25f;
			}
			if(touchX>0.14285 && touchX < 0.28571) {
				touchX = 587.33f;
			}
			if(touchX>0.28571 && touchX < 0.42857142857) {
				touchX = 659.26f;
			}
			if(touchX>0.42857142857 && touchX < 0.57142857142) {
				touchX = 698.46f;
			}
			if(touchX>0.57142857142 && touchX < 0.71428571428) {
				touchX = 783.99f;
			}
			if(touchX>0.71428571428 && touchX < 0.85714285714) {
				touchX = 880.00f;
			}
			if(touchX>0.85714285714 && touchX < 1) {
				touchX = 987.77f;
			}
			touchY = 1;

		}
//ZONE KEYBOARD 2 UP UP 									
		if(touchY>0.71174863388 && touchY < 0.85655737704918) {
			if(touchX>0 && touchX < 0.10666) {
				touchX = 523.25f;
			}
			if(touchX>0.10666 && touchX < 0.18) {
				touchX = 554.37f;
			}
			if(touchX>0.18 && touchX < 0.24666) {
				touchX = 587.33f;
			}
			if(touchX>0.24666 && touchX < 0.32) {
				touchX = 622.25f;
			}
			if(touchX>0.32 && touchX < 0.42666) {
				touchX = 659.26f;
			}
			if(touchX>0.42666 && touchX < 0.53333) {
				touchX = 698.46f;
			}
			if(touchX>0.53333 && touchX < 0.60666) {
				touchX = 739.99f;
			}
			if(touchX>0.60666 && touchX < 0.67333) {
				touchX = 783.99f;
			}
			if(touchX>0.67333 && touchX < 0.74666) {
				touchX = 830.61f;
			}
			if(touchX>0.74666 && touchX < 0.82) {
				touchX = 880.00f;
			}
			if(touchX>0.82 && touchX < 0.89333) {
				touchX = 932.33f;
			}
			if(touchX>0.89333 && touchX < 1) {
				touchX = 987.77f;
			}
			// set touchY to 1:1 ratio to calculate the frequency in csound script
			touchY = 1;
		}
//ZONE VOID  UP
		if(touchY>0.85655737704918 && touchY < 0.99) {
			touchX = 0f;
		}
		touchArray[0] = touchX;
		touchArray[1] = touchY;
		return touchArray;
	}

    /* setPreset() 
     * @parameters int presetNbr
     */
    public void setPreset(int presetNbr) {
    	
    	String csd = getResourceFileAsString(R.raw.synth_sawtooth);
    	File f = createTempFile(csd);
		System.out.println(csoundObj);
		
   	 switch (presetNbr) {
   
		 case 0: presetNbr = 0;
			csd = getResourceFileAsString(R.raw.multitouch_xy_kx);
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
        default:break;
	
    }
 	csoundObj.stop();
	csoundObj.startCsound(f);
//	csoundObj.sendScore("f1 0 16384 10 1 1   1   1    0.7 0.5   0.3  0.1");
    }
    /* create the preset button listener 
     * (non-Javadoc)
     */

public void createLoadListener(Button loadPresetButton) {
	        loadPresetButton.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	Intent intent = new Intent(MultiTouchXYActivity.this, ILoaderPresetActivity.class);
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
	}
	}



}
