package com.mfusion.player.common.Entity.Control;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.View.RSSMarqueeTextSurfaceView;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.commons.tools.rss.RssFeed;
import com.mfusion.player.common.Setting.Component.RSSSetting;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class RSSControl extends AControl implements MyCallInterface{

	private RSSSetting setting;
	private String m_playing_rss_url = "";
	private boolean scrolled=false;
	
	private Thread waitContent=new Thread(){  
		public void run(){  
			while (true)
			{
				if(Helper.RSS.RSSContent(setting.RSSURL)==null)
				{
					try
					{
						Thread.sleep(2000);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
					break;
			}


			mHandler.sendEmptyMessage(0);


		}  
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {

				fuc(null);

			} catch (Exception ex) {
				// TODO Auto-generated catch block
				LoggerHelper.WriteLogfortxt("RSSComponent handleMessage==>"+ex.getMessage());
			}

		}
	};
	public RSSControl (Context context)
	{
		this.CreateControl(context);
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		((RSSMarqueeTextSurfaceView)this.Element).stopScroll();
		this.Element=null;
	}

	@Override
	public void CreateControl(Context context) {
		// TODO Auto-generated method stub
		RSSMarqueeTextSurfaceView rss=new RSSMarqueeTextSurfaceView(context);

		Caller caller=new Caller();
		caller.setI(this);
		rss.caller=caller;
		this.Element=rss;
	}

	public void setSetting(RSSSetting setting) {
		// TODO Auto-generated method stub
		this.setting=setting;
		((RSSMarqueeTextSurfaceView)this.Element).setSetting(setting);
	}


	@Override
	public Object fuc(Object paras) {
		// TODO Auto-generated method stub
		try
		{
			RssFeed rssContent=Helper.RSS.RSSContent(setting.RSSURL);
			((RSSMarqueeTextSurfaceView)this.Element).setText(rssContent);
			((RSSMarqueeTextSurfaceView)this.Element).startScroll();
			this.scrolled=true;//��ʼ����
		}
		catch(Exception ex){}
		return null;
	}

	public void Play() {
		// TODO Auto-generated method stub
		if (!this.setting.RSSURL.trim().equals(this.m_playing_rss_url))
		{
			if (!this.scrolled)//���ڹ���
			{
				this.waitContent.start();
				this.m_playing_rss_url = this.setting.RSSURL.trim();
			}

		}
		else
		{
			if(this.scrolled)
			{
				((RSSMarqueeTextSurfaceView)this.Element).setText(Helper.RSS.RSSContent(setting.RSSURL));
			}
		}
	}
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}


}
