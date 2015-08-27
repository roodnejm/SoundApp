package com.csounds.examples.drumrack;

import com.csounds.examples.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

public class LcdScreenView extends View implements LcdScreen.Listener {

	// these are tied to the 9patch png 
	
	private static final int HORIZONTAL_MARGIN = 40;

	private static final float VERTICAL_MARGIN = 50.0f;

	private static final float LEFT_MARGIN = 30.0f;

	
	private NinePatchDrawable backgroundPatch;
	
	private static Typeface lcdTypeface;
	
	private Paint textPaint;
	
	private LcdScreen model; 
	
	
	public LcdScreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LcdScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LcdScreenView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setModel(new LcdScreen());
		
		Resources resources = getContext().getResources();
		
		backgroundPatch = (NinePatchDrawable) resources.getDrawable(R.drawable.lcd);
		
		if (lcdTypeface == null) {
			lcdTypeface = Typeface.createFromAsset(getContext().getAssets(), "DigitalDream.ttf");
		}
		
		textPaint = new Paint();
		textPaint.setColor(0xff000000);
		textPaint.setTypeface(lcdTypeface);
		textPaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = 100;
		}
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = 100;
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		float textHeight = h - VERTICAL_MARGIN; 
		textPaint.setTextSize(textHeight);
		int width = (int) Math.floor((w - HORIZONTAL_MARGIN) / textPaint.measureText(" "));
		
		model.updateWidth(width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Rect clip = canvas.getClipBounds();
		int width = clip.width();
		int height = clip.height();
		
		backgroundPatch.setBounds(0, 0, width - 1, height - 1);
		backgroundPatch.draw(canvas);
		
		float ascent = textPaint.ascent();
		canvas.drawText(model.getChars(), 0, model.getChars().length, 
						LEFT_MARGIN, (height - textPaint.getTextSize()) / 2 - ascent, textPaint);
	}

	public void setModel(LcdScreen model) {
		if (this.model != null) {
			this.model.removeListener(this);
		}
		this.model = model;
		if (model != null) {
			model.addListener(this);
		}
	}
	
	public LcdScreen getModel() {
		return model;
	}

	@Override
	public void lcdWidthChanged(int newWidth) {
	}

	@Override
	public void lcdCharsUpdated() {
		invalidate();
	}
}
