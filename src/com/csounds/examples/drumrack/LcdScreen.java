package com.csounds.examples.drumrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public final class LcdScreen {

	public interface Listener {
		void lcdWidthChanged(int newWidth);

		void lcdCharsUpdated();
	}
	
	public enum Mode {
		CENTER,
		FLASHING,
		TICKER
	}
	
	private List<Listener> listeners = new ArrayList<Listener>();
	
	private int width = 0;
	
	private char[] chars = new char[0];
	
	private String targetString = "";

	private Mode mode = Mode.TICKER;
	
	private boolean flashingFlag = true; 
	
	private int tickerPosition = 0;
	
	private int tickerDirection = 1;
	
	private static Timer timer = new Timer();
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					updateByTimer();
				}
			});
		}
	};
	
	private Handler handler;
	
	
	public LcdScreen() {
		handler = new Handler();
		
		timer.scheduleAtFixedRate(updateTask, 0, 500L);
	}
	
	void updateWidth(int newWidth) {
		char[] newChars = new char[newWidth];
		width = newWidth;		
		chars = newChars;
		clear();
		
		// reset the ticker
		tickerPosition = 0;
		tickerDirection = 1;
		
		for (Listener listener : listeners) {
			listener.lcdWidthChanged(newWidth);
		}
		
		iterate();
	}
	
	char[] getChars() {
		return chars;
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void updateByTimer() {
		iterate();
	}
	
	private void iterate() {
		if (width == 0) {
			return; // not initialized
		}
		
		if (mode == Mode.CENTER) {
			renderCenteredString();
		} else if (mode == Mode.TICKER) {
			renderTicker();
		} else if (mode == Mode.FLASHING) {
			renderFlashing();
		}
		for (Listener listener : listeners) {
			listener.lcdCharsUpdated();
		}
	}

	private void renderFlashing() {
		if (flashingFlag) {
			renderCenteredString();
		} else {
			clear();
		}
					
		flashingFlag = ! flashingFlag;
	}
	
	private void renderTicker() {
		if (targetString.length() <= width) {
			renderCenteredString();
			return;
		}
		
		tickerPosition += tickerDirection;

		int rightPosition = tickerPosition + width;
		if (tickerDirection == 1) {
			if (rightPosition == targetString.length()) {
				tickerDirection = - tickerDirection;
			}
		} else {
			if (tickerPosition == 0) {
				tickerDirection = - tickerDirection;
			}
		}
		
		for (int i = 0; i < width; ++i) {
			chars[i] = targetString.charAt(tickerPosition + i);
		}
	}
	
	private void clear() {
		for (int i = 0; i < width; ++i) {
			chars[i] = ' ';
		}
	}

	private void renderCenteredString() {
		String s = targetString;
		if (s.length() > width) {
			s = s.substring(0, width - 1);
		}
		
		int offset = (width - s.length()) / 2;
		for (int i = 0; i < width; ++i) {
			if (i < offset || (i >= offset + s.length())) {
				chars[i] = ' ';
			} else {
				chars[i] = s.charAt(i - offset);
			}
		}
	}
	
	public void setTargetString(String s) {
		targetString = s;
		tickerPosition = 0; // FIXME: c/p
		tickerDirection = 1;
		
		iterate();
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
		iterate();
	}
}
