package com.mfusion.player.common.Entity.Components;

import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Template;
import com.mfusion.player.common.Entity.Control.WeatherControl;
import com.mfusion.player.common.Entity.Weather.LayoutTemplate;
import com.mfusion.player.common.Entity.Weather.WeatherData;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Helper.WeatherDataHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.WeatherSetting;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class WeatherComponent extends BasicComponent {

	private WeatherSetting m_setting;
	
	private WeatherDataHelper m_weather_helper; 
	
	private WeatherControl m_weatherControl;
	
	private WeatherData m_content;
	
	private LayoutTemplate m_template;
	
	private HashSet<String> m_loadingImages;
	
	private Date m_last_update_template;
	
	private Date m_last_update_content;
	
	private int m_refresh_interval=60;
	
	private Thread m_refreshThread;
	
	private Thread m_image_downloader;
	
	private Boolean isloadingBoolean=false;
	
	private Boolean isLoadImageBoolean=false;
	
	public WeatherComponent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		m_weatherControl=(WeatherControl) Helper.ControlManager.GetControl(ControlType.Weather);
		this.setCmpcontext(m_weatherControl.Element);
	}

	@Override
	public void End() {
		// TODO Auto-generated method stub
		try {
			this.isloadingBoolean=false;
			
			this.isLoadImageBoolean=false;
			
			Helper.ControlManager.ReturnControl(this.m_weatherControl);

			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			
			this.container.removeView(this.m_weatherControl.Element);
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("WeatherComponent End==>"+e.getMessage());
		}
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			this.m_template=this.m_weather_helper.getLastLayoutTemplate(this.m_setting.LayoutXMLPath);
			
			if(this.m_template!=null)
				this.m_content=this.m_weather_helper.getLastWeatherData(this.m_setting.City, this.m_setting.Language,this.m_template.DateNum);
			
			this.m_weatherControl.Element.setVisibility(View.VISIBLE);
			this.AddView(this.m_weatherControl.Element);
			MainActivity.Instance.PBUDispatcher.template.addView(container);
			
			this.m_refreshThread=new Thread(downloadRunnable);

			this.isloadingBoolean=true;
			
			this.m_refreshThread.start();
			
			if(this.m_setting!=null){
				this.m_weatherControl.SetWeatherTemplateType(this.m_setting.Type);
				this.m_weatherControl.ShowWeather(this.m_content,this.m_template);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("WeatherComponent Render==>"+ex.getMessage());
		}
	}

	Integer m_download_error_times=0;
	Runnable downloadRunnable=new Runnable() {
		@Override
		public void run() {
			m_loadingImages=new HashSet<String>();
			m_last_update_template=m_last_update_content=null;
			// TODO Auto-generated method stub
			while (isloadingBoolean) {
				try {
					//downlod template
					Boolean isContentChanged=false;
					Boolean isTemplateChanged=false;
					Date nowDate=MainActivity.Instance.Clock.Now;
					
					if(m_last_update_template==null||DateTimeHelper.GetAddedDateFromMinute(m_last_update_template, m_refresh_interval).compareTo(nowDate)<0){
						m_template=m_weather_helper.DownloadWeatherTemplate(m_setting.LayoutXMLPath);
						if(m_template!=null){
							m_last_update_template=nowDate;
							isTemplateChanged=true;
							if (m_template.BackImage.equalsIgnoreCase("") == false)
		                    {
								m_weather_helper.DownloadWeatherBackImage(m_setting.LayoutXMLPath,m_template.BackImage);
		                    }	
						}else {
							m_download_error_times++;
						}
					}
					
					//download datas
					if(m_last_update_content==null||DateTimeHelper.GetAddedDateFromMinute(m_last_update_content, m_refresh_interval).compareTo(nowDate)<0){
						isLoadImageBoolean=false;
						Integer dateNum=1;
						if(m_template!=null)
							dateNum=m_template.DateNum;
						m_content=m_weather_helper.DownloadWeatherContent(m_setting.City, m_setting.Language, dateNum, m_loadingImages);
						if(m_content.ErrorMessage==null||m_content.ErrorMessage.isEmpty()){
							m_last_update_content=nowDate;
							isContentChanged=true;
						}else {
							m_download_error_times++;
						}
					}
					
					if(isTemplateChanged||isContentChanged){
						m_download_error_times=0;
						
						m_handler.sendEmptyMessage(0);
						
						if(isContentChanged){
							m_image_downloader=new Thread(downloadImageRunnable);
							
							m_image_downloader.start();
						}
					}
					
					if(m_download_error_times>=6){
						m_download_error_times=0;
						m_last_update_template=m_last_update_content=DateTimeHelper.GetAddedDateFromMinute(nowDate, 3-m_refresh_interval);
					}
				
				} catch (Exception e) {
					LoggerHelper.WriteLogfortxt("WeatherComponent downloadRunnable==>"+e.getMessage());
					// TODO: handle exception
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	Runnable downloadImageRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			isLoadImageBoolean=true;
			
			while(isLoadImageBoolean){
				try {
					Integer loadedNum=0;
					for (String imagePath : m_loadingImages)
		            {
						if(m_weather_helper.DownloadWeatherIcons(imagePath, m_content.IconSizes, m_content.ImageRootPath))
							loadedNum++;
		            }
					if(loadedNum>0){
						m_handler.sendEmptyMessage(0);
						if(loadedNum==m_loadingImages.size())
							break;
					}
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception ex) {
					LoggerHelper.WriteLogfortxt("WeatherComponent downloadImageRunnable==>"+ex.getMessage());
					// TODO: handle exception
				}
			}
		}
	};
	
	Handler m_handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			m_weatherControl.ShowWeather(m_content,m_template);
		}
	};
	
	@Override
	public void Init(Object arg) {
		// TODO Auto-generated method stub
		if(arg==null)
			return;
		this.m_setting=(WeatherSetting)arg;
		
		this.m_weather_helper=new WeatherDataHelper();
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}

}
