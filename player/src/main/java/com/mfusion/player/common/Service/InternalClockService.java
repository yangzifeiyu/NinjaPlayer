/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-09
 *
 *�ڲ�ʱ��
 */
package com.mfusion.player.common.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.mozilla.intl.chardet.nsBIG5Verifier;

import android.content.ContentResolver;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Player.MainActivity;


public class InternalClockService implements BasicServiceInterface{

	public Date BeforeTime;//֮ǰ��ʱ��
	public Date Now;//���ڵ�ʱ��
	//private HandleTimer  timer;
	//private Thread thread;

	private static final int Interval=1000;//1s
	private int duration;
	private Object timelock=new Object();
	private ClockServiceThread clockservice;

	private final int ON = 1;
    private final int OFF = 0;
	/*
	 * ���캯��
	 */
	public InternalClockService()
	{
		/*int nAutoTimeStatus=0;
		try {
			 nAutoTimeStatus = Settings.System.getInt(MainActivity.Instance.getContentResolver(), Settings.Global.AUTO_TIME, OFF);

			 ContentResolver resolver= MainActivity.Instance.getContentResolver();
			 Uri uri = android.provider.Settings.System.getUriFor(Settings.Global.AUTO_TIME);  
			 android.provider.Settings.System.putInt(resolver, Settings.Global.AUTO_TIME,  nAutoTimeStatus == OFF ? ON: OFF);  
			 resolver.notifyChange(uri, null);  
			 
			 nAutoTimeStatus = Settings.System.getInt(MainActivity.Instance.getContentResolver(), Settings.Global.AUTO_TIME, OFF);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		//Now=Calendar.getInstance().getTime();
		/*timer = new HandleTimer() {
			@Override
			protected void onTime() {
				try
				{
					Date local=Calendar.getInstance().getTime();
					Now=DateTimeHelper.GetAddedDate(local, duration,MainActivity.Instance.PlayerSetting.Timezone);

					if(MainActivity.Instance.PlayerSetting.ShowNetwork)
						listerNetworkSpeed(getTotalRxBytes(1));
					else if(MainActivity.Instance.PlayerSetting.isModifyFromServer)
					{
						MainActivity.Instance.PlayerSetting.isModifyFromServer=false;
						listerNetworkSpeed("-1");
					}
				}
				catch(Exception ex)
				{
					LoggerHelper.WriteLogfortxt("InternalClock onTime==>"+ex.getMessage());
				}
			}
		};*/



	}


	/*
	 * �ж������Ƿ�ı�
	 */
	@SuppressWarnings("deprecation")
	public void CheckIsDateChanged() {
		try
		{
			if (DateTimeHelper.CompareTime(this.BeforeTime, Now)!=0 )
			{
				if(BeforeTime!=null&&Now!=null)
				{
					int startday=BeforeTime.getDay();
					int day=Now.getDay();

					if(day!=startday)
					{
						LoggerHelper.WriteLogfortxt("Date Changed==>");
						MainActivity.Instance.mHandler.sendEmptyMessage(0);
					}
				}
				this.BeforeTime=Now;
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("InternalClock CheckIsDateChanged==>"+ex.getMessage());
		}
	}


	/*
	 * �����ڲ�ʱ��
	 */
	@Override
	public void Restart()
	{
		Now=Calendar.getInstance().getTime();
	}
	
	public int Restart(ServerConnectStatus connect,String date,String timezone)
	{
		int result=-1;//-1 failed,0 internet,1 server
		try
		{

			this.Stop();

			synchronized(this.timelock)
			{
				if(date==null||date.isEmpty()){
					Now=Calendar.getInstance().getTime();
					clockservice=new ClockServiceThread();
					clockservice.start();
					return 1;
				}
					
				try
				{

					Date local=Calendar.getInstance().getTime();
					if(connect.equals(ServerConnectStatus.Connection))
					{

						MainActivity.Instance.PlayerSetting.setTimezonestr(timezone);
						Date serverdate=DateTimeHelper.ConvertToDateTime(date, 0,MainActivity.Instance.PlayerSetting.Timezone);
						BeforeTime=serverdate;
						Now=serverdate;
						result=1;
						MainActivity.Instance.connectState=true;
					}
					else//��ȡ����ʱ��
					{
						/*URL url = null;
				try
				{
					url = new URL("http://www.bjtime.cn");
				} 
				catch (MalformedURLException e)
				{
					result=-1;
					PlayerStartDateTime=Calendar.getInstance().getTime();//���player������ʱ��
					Now=Calendar.getInstance().getTime();
					MainActivity.Instance.connectState=false;
					LoggerHelper.WriteLogfortxt("Cannot connect to network");
					this.timer.start(0,Interval);
					return result;
				}
				URLConnection uc = null;
				try {
					uc = url.openConnection();
					uc.connect();
				} catch (IOException e) {
					result=-1;
					PlayerStartDateTime=Calendar.getInstance().getTime();//���player������ʱ��
					Now=Calendar.getInstance().getTime();
					MainActivity.Instance.connectState=false;
					LoggerHelper.WriteLogfortxt("Cannot connect to network");
					this.timer.start(0,Interval);
					return result;
				}
				result=0;
				long ld = uc.getDate();

				Date now = new Date(ld);

				PlayerStartDateTime=now;
				Now=now;*/
						try
						{

							SntpClient client = new SntpClient();  
							if (client.requestTime("asia.pool.ntp.org", 30000)) 
							{  
								result=0;
								long ld = client.getNtpTime() + System.nanoTime() / 1000  
										- client.getNtpTimeReference();
								Date now = new Date(ld);

								BeforeTime=now;
								Now=now;
								MainActivity.Instance.connectState=true;
							}
							else
							{
								result=-1;
								BeforeTime=Calendar.getInstance().getTime();//���player������ʱ��
								Now=Calendar.getInstance().getTime();
								MainActivity.Instance.connectState=false;
								LoggerHelper.WriteLogfortxt("Cannot connect to network");
								//this.timer.start(0,Interval);
								clockservice=new ClockServiceThread();
								clockservice.start();
								return result;
							}
						}
						catch(Exception ex)//��ȡ�ڲ�ʱ��
						{
							result=-1;
							BeforeTime=Calendar.getInstance().getTime();//���player������ʱ��
							Now=Calendar.getInstance().getTime();
							MainActivity.Instance.connectState=false;
							LoggerHelper.WriteLogfortxt("Cannot connect to network");
							//this.timer.start(0,Interval);
							clockservice=new ClockServiceThread();
							clockservice.start();
							return result;
						}
					}
					duration=DateTimeHelper.GetDuration(Now, local);

				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					LoggerHelper.WriteLogfortxt("InternalClock Restart==>"+ex.getMessage());
				}
			}

			clockservice=new ClockServiceThread();
			clockservice.start();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}


	@Override
	public void Stop() 
	{
		// TODO Auto-generated method stub
		//this.timer.stop();
		if(clockservice!=null)
			this.clockservice.Stop();
		clockservice=null;


	}



	double lastTotalBytes=0;
	Integer dataUsedTimeInteger=0;
	private String getTotalRxBytes(long time){  
		try {
			dataUsedTimeInteger=dataUsedTimeInteger%60;
			//��ȡ�ܵĽ����ֽ����Mobile��WiFi��  
			double cuttentTotalBytes=TrafficStats.getTotalRxBytes()==TrafficStats.UNSUPPORTED?0:(TrafficStats.getTotalRxBytes()/1024.0); 
			if(cuttentTotalBytes==0)
				return "";

			if(dataUsedTimeInteger==0){
				lastTotalBytes=cuttentTotalBytes;
				return "";
			}
			double bytesPreSecond=(cuttentTotalBytes-lastTotalBytes)*8/dataUsedTimeInteger;
			BigDecimal bd = new BigDecimal(bytesPreSecond);
			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);  
			return bd.doubleValue()+"";  

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			dataUsedTimeInteger++;
		}
		return "";
	}

	private void listerNetworkSpeed(String speed) {
		// TODO Auto-generated method stub
		try {
			if(MainActivity.Instance.PBUDispatcher==null||MainActivity.Instance.PBUDispatcher.mHandler==null)
				return;

			if(speed.equals("-1"))
				speed="";
			else if(speed.equals("")==false)
				speed=speed+"kbps";
			else{
				return;
			}
			Message msg=new Message();
			msg.what=3;
			Bundle bundle=new Bundle();
			bundle.putString("downloadspeed",speed);
			msg.setData(bundle);
			MainActivity.Instance.PBUDispatcher.mHandler.sendMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private class ClockServiceThread extends Thread
	{
		private boolean Running=true;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(Running)
			{
				// TODO Auto-generated method stub
				try
				{

					Date local=Calendar.getInstance().getTime();
					Now=DateTimeHelper.GetAddedDate(local, duration,MainActivity.Instance.PlayerSetting.Timezone);

					if(MainActivity.Instance.PlayerSetting.ShowNetwork)
						listerNetworkSpeed(getTotalRxBytes(1));
					else if(MainActivity.Instance.PlayerSetting.isModifyFromServer)
					{
						MainActivity.Instance.PlayerSetting.isModifyFromServer=false;
						listerNetworkSpeed("-1");
					}
					Thread.sleep(Interval);
				}
				catch(Exception ex)
				{
					LoggerHelper.WriteLogfortxt("ClockRunnable onTime==>"+ex.getMessage());
				}
			}
		}
		public void Stop()
		{
			Running=false;
		}



	}


}
