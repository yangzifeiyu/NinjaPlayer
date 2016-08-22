/* 
this.startScroll(); * @version 2015-04-20
 *
 *�Ż�ticktext
 */
package com.mfusion.player.common.Entity.View;

import com.mfusion.player.common.Setting.Component.TickerTextSetting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MarqueeTextSurfaceView extends SurfaceView  implements SurfaceHolder.Callback {


	private SurfaceHolder mHolder;
	private MyThread myThread;
	private String mText = "";
	private float xOffset = 0,baseline;
	private Paint paint;
	private boolean isSurfaceValid = false;
	private TickerTextSetting setting;
	private int width,height;
	private float textWidth;
	private int color=Color.BLUE;


	public TickerTextSetting getSetting() {
		return setting;
	}

	public void setSetting(TickerTextSetting setting) {
		this.setting = setting;
		color=setting.BackColor;
		height = this.getLayoutParams().height;
		width = this.getLayoutParams().width;
		xOffset =width;

		this.mText=setting.Context;
		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG); //��������
		paint.setTextSize(setting.TextProperty.FontSize);
		paint.setColor(setting.TextProperty.FontColor);
		paint.setTypeface(setting.TextProperty.FontStyle);
		Rect textBound = new Rect();
		paint.getTextBounds(mText.toCharArray(), 0, mText.length(), textBound);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();  
		baseline= (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top; 
		this.textWidth=paint.measureText(this.mText); 
	}

	public MarqueeTextSurfaceView(Context context) {
		super(context);
		init();
	}



	public MarqueeTextSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressLint("InlinedApi")
	public void init() {
		//this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setZOrderOnTop(true);
		//setZOrderMediaOverlay(true);
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.TRANSPARENT);
		myThread = new MyThread(mHolder);
		
	



	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		holder.setFixedSize(width, height);
	}



	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		isSurfaceValid = true;
		this.startScroll();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		isSurfaceValid = false;
	}

	class MyThread extends Thread {
		private SurfaceHolder holder;
		public boolean canRun;

		public MyThread(SurfaceHolder holder) {
			this.canRun = true;
			this.holder = holder;
		}

		@Override
		public void run() {
			Canvas c = null;
			while (canRun && isSurfaceValid) {
				try {
					c = holder.lockCanvas();
					handlerDrawInternal(c);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null) {
						holder.unlockCanvasAndPost(c);
					}
					if (xOffset <=-textWidth) {
						xOffset =width;
					} 
					else 
					{
						xOffset-= setting.Speed/100;
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}


	public void startScroll() {
		if (!isSurfaceValid) {
			return;
		}
		myThread.canRun = true;
		if (!myThread.isAlive()) {
			xOffset = mHolder.getSurfaceFrame().width();
			myThread = new MyThread(mHolder);
			myThread.start();
		}
	}

	public void stopScroll() {
		myThread.canRun = false;
		xOffset =mHolder.getSurfaceFrame().width();
	}



	private void handlerDrawInternal(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		canvas.drawColor(color,Mode.CLEAR);
		canvas.drawText(mText, xOffset, baseline, paint);



	}

	public void setTop() {
		// TODO Auto-generated method stub
		setZOrderOnTop(false);
		setZOrderMediaOverlay(false);
		mHolder.setFormat(PixelFormat.TRANSPARENT);
	}





}