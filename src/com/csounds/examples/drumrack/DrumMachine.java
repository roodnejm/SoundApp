package com.csounds.examples.drumrack;

import java.util.ArrayList;
import java.util.List;

import com.csounds.examples.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public final class DrumMachine {

	public static final int TOTAL_BUTTONS = 3 * 3;
	
	public interface Listener {
		void buttonStateChanged(int index);

		void multipleButtonStateChanged();
	}
	
	private boolean[] buttonPressMap = new boolean[TOTAL_BUTTONS];
	
	private List<Listener> listeners = new ArrayList<Listener>();
	
	private SoundPool soundPool;
	
	private int[] soundIds = new int[TOTAL_BUTTONS];
	
	private int[] streamIds = new int[TOTAL_BUTTONS];
	
	public DrumMachine(Context context) {
		for (int i = 0; i < TOTAL_BUTTONS; ++i) {
			buttonPressMap[i] = false;
		}
		soundPool = new SoundPool(TOTAL_BUTTONS, AudioManager.STREAM_MUSIC, 0);
		int drumrack = 2;
		if(drumrack==1){
			initDrumRackPreset(context);
		} 
		if(drumrack==2){
			initDubStepPreset(context);
		}
	}
	public void initDubStepPreset(Context context) {
		soundIds[0] = soundPool.load(context, R.raw.dsbass01, 1);
		soundIds[1] = soundPool.load(context, R.raw.dsbass02, 1);
		soundIds[2] = soundPool.load(context, R.raw.dsbeat01, 1);
		soundIds[3] = soundPool.load(context, R.raw.dsbeat02, 1);
		soundIds[4] = soundPool.load(context, R.raw.dsbeat03, 1);
		soundIds[5] = soundPool.load(context, R.raw.dsfx01, 1);
		soundIds[6] = soundPool.load(context, R.raw.dsfx02, 1);
		soundIds[7] = soundPool.load(context, R.raw.dsorgan04, 1);
		soundIds[8] = soundPool.load(context, R.raw.dsorgan01, 1);
	}
	
	public void initDrumRackPreset(Context context) {
		soundIds[0] = soundPool.load(context, R.raw.kick2, 1);
		soundIds[1] = soundPool.load(context, R.raw.snare, 1);
		soundIds[2] = soundPool.load(context, R.raw.snare, 1);
		soundIds[3] = soundPool.load(context, R.raw.yppahintro, 1);
		soundIds[7] = soundPool.load(context, R.raw.cme1, 1);
		soundIds[8] = soundPool.load(context, R.raw.cme2, 1);
	}
	
	/* Setting the preset on the DrumMachine 
	 * 
	 */
	public void initPreset(Context context,int drumrackNbr) {
		System.out.println(drumrackNbr);
		
		 switch (drumrackNbr) {
		 case 0: drumrackNbr = 0;
		 initDrumRackPreset(context);
         break;
         case 1:  drumrackNbr = 1;
         initDubStepPreset(context);
         break;


         default:break;
		 }
	}

	public void pressButton(int index) {
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == false) {
				buttonPressMap[index] = true;
				
				int soundId = soundIds[index];
				if (soundId != 0) {
					streamIds[index] = soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
				}
				
				for (Listener listener : listeners) {
					listener.buttonStateChanged(index);
				}
			}
		}
	}
	
	public void releaseButton(int index) {
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == true) {
				buttonPressMap[index] = false;
	
				int streamId = streamIds[index];
				if (streamId != 0) {
					soundPool.stop(streamId);
					streamIds[index] = 0;
				}

				for (Listener listener : listeners) {
					listener.buttonStateChanged(index);
				}
			}
		}
	}
	
	public boolean isButtonPressed(int index) {
		if (index < 0 || index > TOTAL_BUTTONS) {
			return false;
		} else {
			return buttonPressMap[index];
		}
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void releaseAllButtons() {
		for (int i = 0; i < buttonPressMap.length; ++i) {
			buttonPressMap[i] = false;
		}
		for (Listener listener : listeners) {
			listener.multipleButtonStateChanged();
		}
	}
	
	public void dispose() {
		
	}
}
