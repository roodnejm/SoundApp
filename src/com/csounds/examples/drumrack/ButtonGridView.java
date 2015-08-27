package com.csounds.examples.drumrack;

import com.csounds.examples.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ButtonGridView extends View implements DrumMachine.Listener {

	// measurements
	
	private static final int BUTTON_GRID_SIZE = 3;
	
	private static final float BUTTON_PADDING = 0.01f;
	
	private float buttonCellSize;

	private float scale;
	
	// drawing tools
	
	private Bitmap buttonIdleBitmap;
	
	private Bitmap buttonPressedBitmap;
	
	// model
	
	private DrumMachine model;

	public ButtonGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ButtonGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ButtonGridView(Context context) {
		super(context);
		init();
	}

	private void init() {
		initDrawingInstruments();
	}
	
	private void initDrawingInstruments() {
		Resources resources = getContext().getResources();
		
		buttonIdleBitmap = BitmapFactory.decodeResource(resources, R.drawable.button_idle_purple);
		buttonPressedBitmap = BitmapFactory.decodeResource(resources, R.drawable.button_pressed_purple);
	}	
	
	public void setDrumMachineModel(DrumMachine model) {
		if (model != null) {
			model.removeListener(this);
		}
		this.model = model;
		if (model != null) {
			model.addListener(this);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		scale = canvas.getClipBounds().width();
			
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		
		drawButtons(canvas);
		
		canvas.restore();
	}
	
	private void drawButtons(Canvas canvas) {
		for (int row = 0; row < BUTTON_GRID_SIZE; ++row) {
			for (int col = 0; col < BUTTON_GRID_SIZE; ++col) {
				drawButton(canvas, row, col);
			}
		}
	}
	
	private void drawButton(Canvas canvas, int row, int col) {
		buttonCellSize = 1.0f / BUTTON_GRID_SIZE;
		
		float buttonCellTop = row * buttonCellSize;
		float buttonCellLeft = col * buttonCellSize;
		
		float buttonTop = buttonCellTop + BUTTON_PADDING;
		float buttonLeft = buttonCellLeft + BUTTON_PADDING;
		
		float buttonSize = (buttonCellSize - BUTTON_PADDING * 2); 
	
		
		Bitmap bitmap = getBitmapForButton(row, col);
		float pixelSize = canvas.getClipBounds().width();
		System.out.println(pixelSize + " " + bitmap + " "+ buttonSize);
		float bitmapScaleX = (pixelSize / bitmap.getWidth()) * buttonSize;
		float bitmapScaleY = (pixelSize / bitmap.getHeight()) * buttonSize;
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(bitmapScaleX, bitmapScaleY);
		canvas.drawBitmap(bitmap, buttonLeft / bitmapScaleX, buttonTop / bitmapScaleY, null);
		canvas.restore();
	}

	private Bitmap getBitmapForButton(int row, int col) {
		
		return model.isButtonPressed(getButtonIndex(row, col)) ?
				buttonPressedBitmap : buttonIdleBitmap;
	}

	private int getButtonIndex(int row, int col) {
		return row * BUTTON_GRID_SIZE + col;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	private int getPreferredSize() {
		return 300;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int buttonIndex = -1;
		 int pointerIndex = event.getActionIndex();
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			buttonIndex = getButtonByCoords(event.getX(), event.getY());
			if (buttonIndex != -1) {
				model.pressButton(buttonIndex);
			}
			return true;
		case MotionEvent.ACTION_UP:
			buttonIndex = getButtonByCoords(event.getX(), event.getY());
			if (buttonIndex != -1) {
				model.releaseButton(buttonIndex);
			}
			model.releaseAllButtons();
			return true;
		case MotionEvent.ACTION_POINTER_DOWN:
			buttonIndex = getButtonByCoords(event.getX(pointerIndex), event.getY(pointerIndex));
			if (buttonIndex != -1) {
				
				model.pressButton(buttonIndex);
			}
			return true;
		case MotionEvent.ACTION_POINTER_UP:
			buttonIndex = getButtonByCoords(event.getX(pointerIndex), event.getY(pointerIndex));
			if (buttonIndex != -1) {
				model.releaseButton(buttonIndex);
			}
			return true;
		}
		return false;
	}

	private int getButtonByCoords(float x, float y) {
		float scaledX = x / scale;
		float scaledY = y / scale;
		
		float buttonCellX = FloatMath.floor(scaledX / buttonCellSize); 
		float buttonCellY = FloatMath.floor(scaledY / buttonCellSize);
		
		return getButtonIndex((int) buttonCellY, (int) buttonCellX);
	}

	@Override
	public void buttonStateChanged(int index) {
		invalidate();
	}

	@Override
	public void multipleButtonStateChanged() {
		invalidate();
	}
}
