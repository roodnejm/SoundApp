package com.csounds.examples.drumrack;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.csounds.examples.drumrack.LcdScreen.Mode;

import android.os.Handler;

public class DrumMachineLcdUpdater {
	private LcdScreen screen;
	
	private static Timer timer = new Timer();
	
	private Handler handler;
	
	private int mode = -1;
	
	private static Random random = new Random();
	
	public DrumMachineLcdUpdater(LcdScreen screen) {
		this.screen = screen;
		this.handler = new Handler();
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						updateScreen();						
					}
				});
			}
		}, 0, 10000L);
	}
	
	private void updateScreen() {
		int mode = this.mode;
//		while (mode == this.mode) {
//			mode = random.nextInt(3);
//		}
		mode = 2;
		this.mode = mode;
		
		switch (mode) {
		case 0:
			screen.setMode(Mode.FLASHING);
			screen.setTargetString("-Drum Pad-");
			break;
		case 1:
			screen.setMode(Mode.TICKER);
			screen.setTargetString("Synth");
			break;
		case 2:
			screen.setMode(Mode.CENTER);
			screen.setTargetString("Synth01");
			break;
		}
	}
}
