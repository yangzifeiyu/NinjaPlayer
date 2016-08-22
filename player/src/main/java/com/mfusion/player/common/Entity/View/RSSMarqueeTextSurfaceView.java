package com.mfusion.player.common.Entity.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Helper.RSS.RSSUnit;
import com.mfusion.commons.tools.rss.RssFeed;
import com.mfusion.commons.tools.rss.RssItem;
import com.mfusion.player.common.Setting.Component.RSSSetting;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;

import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RSSMarqueeTextSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


	private SurfaceHolder mHolder;
	private MyThread myThread;
	private float xOffset = 0;
	private boolean isSurfaceValid = false;
	private RSSSetting setting;
	public Caller caller;
	private Paint subPaint;
	private Paint bodyPaint;
	private TextPropertySetting mBodyText;
	private TextPropertySetting mSubText;
	private float baseline,baseline1;
	private int width;
	private List<RSSUnit> units=new ArrayList<RSSUnit>();
	private int backcolor=Color.BLUE;


	public RSSSetting getSetting() {
		return setting;
	}


	public void setSetting(RSSSetting setting)
	{
		this.setting = setting;
		this.backcolor=setting.BackColor;
		this.setmBodyText(setting.BodyTextProperty);
		this.setmSubText(setting.SubTextProperty);
		this.width =this.getLayoutParams().width;

	}



	public TextPropertySetting getmBodyText()
	{
		return mBodyText;
	}


	public void setmBodyText(TextPropertySetting mBodyText)
	{
		this.mBodyText = mBodyText;
		bodyPaint = new Paint();
		bodyPaint.setColor(mBodyText.FontColor);
		bodyPaint.setTextSize(mBodyText.FontSize);
		bodyPaint.setTypeface(mBodyText.FontStyle);
		bodyPaint.setAntiAlias(true);   
		FontMetricsInt fontMetrics = bodyPaint.getFontMetricsInt();  
		baseline1 =(this.getLayoutParams().height- fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  
	}


	public TextPropertySetting getmSubText() 
	{
		return mSubText;
	}


	public void setmSubText(TextPropertySetting mSubText)
	{
		this.mSubText = mSubText;
		subPaint = new Paint();
		subPaint.setColor(mSubText.FontColor);
		subPaint.setTextSize(mSubText.FontSize);
		subPaint.setTypeface(mSubText.FontStyle);
		subPaint.setAntiAlias(true);   
		FontMetricsInt fontMetrics = subPaint.getFontMetricsInt();  
		baseline =(this.getLayoutParams().height- fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  
	}



	public RSSMarqueeTextSurfaceView(Context context)
	{
		super(context);
		init();
	}

	public RSSMarqueeTextSurfaceView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}

	public RSSMarqueeTextSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		init();
	}

	public void init()
	{
		try
		{

			setZOrderOnTop(true);
			mHolder = this.getHolder();
			mHolder.addCallback(this);
			myThread = new MyThread(mHolder);
			mHolder.setFormat(PixelFormat.TRANSPARENT);

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("RSSMarqueeTextSurfaceView init==>"+ex.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

		//holder.setFixedSize(width, height);
	}


	private ReentrantLock lock=new ReentrantLock();
	public void setText(RssFeed feed)
	{
		try
		{
			lock.lock();
			units.clear();
			ArrayList<RssItem> rssitems=feed.getRssItems();
			for(int i=0;i<rssitems.size();i++)
			{
				RssItem content=rssitems.get(i);
				String titile=content.getTitle();
				float titlewidth=this.subPaint.measureText(titile);
				RSSUnit titleunit=new RSSUnit(titile, subPaint, titlewidth,baseline);
				String body=content.getDescription();
				float bodywidth=this.bodyPaint.measureText(body);
				RSSUnit bodyunit=new RSSUnit(body, bodyPaint, bodywidth,baseline1);
				units.add(titleunit);
				units.add(bodyunit);

			}
			lock.unlock();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("RSSMargueeTextView setText==>"+ex.getMessage());
		}
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{

		isSurfaceValid = true;

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
			while (canRun ) 
			{
				if(isSurfaceValid)
				{
					try 
					{
						c = holder.lockCanvas();//������һ�������Ϳ���ͨ���䷵�صĻ�������Canvas���������滭ͼ�Ȳ����ˡ�
						handlerDrawInternal(c);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					} 
					finally 
					{
						if (c != null) 
						{
							holder.unlockCanvasAndPost(c);//������ͼ�����ύ�ı䡣
						}

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
		/*if (!isSurfaceValid) {
			return;
		}*/
		myThread.canRun = true;
		if (!myThread.isAlive()) {
			xOffset = width;
			myThread = new MyThread(mHolder);
			myThread.start();
		}
	}

	public void stopScroll() {
		myThread.canRun = false;
		xOffset =width;
	}

	float currentwidth=0;
	float currentstart=0;
	float currenttotal=0;
	boolean flag=false;

	private void handlerDrawInternal(Canvas canvas)
	{
		try
		{
			if (canvas == null) {
				return;
			}

			canvas.drawColor(backcolor,Mode.CLEAR);
			float pos=0;
			float size=0;
			if(xOffset<=-units.get(0).width)
			{
				units.remove(0);
				xOffset=0;

			}
			if(units.size()<=0&&xOffset<=-size)
			{
				if(caller!=null)
				{
					caller.call(null);
				}
				xOffset=this.width;
			}



			pos=xOffset;

			int i=0;
			while(pos<=this.width)
			{

				if(this.units.size()>i)
				{
					RSSUnit unit=this.units.get(i);
					canvas.drawText(unit.text, xOffset+size, unit.baseline,unit.paint);
					size+=unit.width;
					pos+=unit.width;
					i++;

				}
				else
					break;

			}                                                                                                      
			currentwidth=size;
			currentstart=currentwidth-this.width;


		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("RSSMargueeTextView handlerDrawInternal==>"+ex.getMessage());
		}
	}



}