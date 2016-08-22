/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-08
 *
 *player �����߼�
 */
package com.mfusion.player.common.Service;
import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

import com.mfusion.player.R;
import com.mfusion.player.library.Controller.HandleTimer;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.ImageHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.Components.BasicComponent;
import com.mfusion.player.common.Entity.View.MFConnectionView;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Helper.PBUCompareHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import com.mfusion.player.common.Task.TemplateBackImage.TemplateImageTask;
import com.mfusion.player.common.Trigger.VirtualTrigger;

public class PBUDispatcherService implements BasicServiceInterface {
	public  Stack<PBU> m_pbu_stack;         //pbu ����
	public  List<String> m_pbu_idlist;           //pbu id list���ڻ�ȡ��Ҫ���ص��ļ�
	public RelativeLayout template;              //template view

	public PBU m_playing_pbu = null;        //��ǰ���ڲ��ŵ�pbu
	private HandleTimer mTimer;
	private static final int interval = 1000;                  // 0.5s
	private boolean m_pbu_deep_compare = false;   //�ж��Ƿ���Ҫ��ȱȽ�
	public boolean IsComparePBU = false;          //����ultimate����schedule assignģʽ��true��ʾ����Ҫ�Ƚ������л�
	private RelativeLayout.LayoutParams layoutParams;
	public boolean m_pbu_based_timeline = true;//����pbu���л��ǻ���timeline����Sequence
	private boolean m_force_stop_trigger = false;
	private Date EndTime;
	public PBU m_current_trigger_pbu;
	private MFConnectionView connectionview;
	private ImageView downloadview;
	private RelativeLayout.LayoutParams downloadParams;

	private TextView downloadspeedview;
	private RelativeLayout.LayoutParams downloadspeedParams;

	private ReentrantLock m_restart_locker = new ReentrantLock();

	private ReentrantLock m_screen_locker = new ReentrantLock();


	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				switch(msg.what)
				{
				case 0:
					RenderBackImage();
					break;
				case 1:
					Bundle b = msg.getData();
					ServerConnectStatus connection=ServerConnectStatus.fromString(b.getString("connection"));
					ChangeMFConnectionState(connection);
					break;
				case 2:
					Bundle b1 = msg.getData();
					boolean download=b1.getBoolean("download");
					ChangeDownloadState(download);
					break;
				case 3:
					Bundle b2 = msg.getData();
					ShowDownloadSpeed(b2.getString("downloadspeed"));
					break;
				}
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
				// TODO Auto-generated catch block
				LoggerHelper.WriteLogfortxt("PBUDispatcherService handleMessage==>"+ex.getMessage());
			}
			finally
			{
				msg=null;
			}

		}


	};


	private void ChangeMFConnectionState(ServerConnectStatus connection) {
		// TODO Auto-generated method stub

		switch(connection)
		{
		case Unconnection:
			if(this.connectionview.getVisibility()!=View.VISIBLE)
			{
				connectionview.setVisibility(View.VISIBLE);
				connectionview.ChangeColor(connection);
			}
			break;
		case Connection:

			if(this.connectionview.getVisibility()==View.VISIBLE)
			{
				connectionview.setVisibility(View.INVISIBLE);
			}
			break;
		case OverLimit:
			if(this.connectionview.getVisibility()!=View.VISIBLE)
			{
				connectionview.setVisibility(View.VISIBLE);
				connectionview.ChangeColor(connection);

			}
			break;
		default:
			break;
		}
	}
	//���캯��
	public PBUDispatcherService() {
		try
		{

			InitTemplate();
			InitConnectStatus();
			InitDownloadStatus();

			mTimer = new HandleTimer() 
			{
				@Override
				protected void onTime() {
					m_controller_Elapsed();
				}
			};
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PBUDispatcherService==>"+ex.getMessage());
		}
	}




	private void InitDownloadStatus() {
		// TODO Auto-generated method stub


		this.downloadspeedview=new TextView(MainActivity.Instance);
		this.downloadspeedview.setTextColor(-16728064);
		downloadspeedParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		downloadspeedParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.downloadspeedview.setLayoutParams(downloadspeedParams);

		this.downloadview=new ImageView(MainActivity.Instance);

		downloadParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		this.downloadview.setLayoutParams(downloadParams);
		downloadview.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
		Drawable trans=MainActivity.Instance.getResources().getDrawable(R.drawable.download);
		this.downloadview.setImageDrawable(trans);
		this.downloadview.setVisibility(View.INVISIBLE);

	}
	private void InitConnectStatus() {
		// TODO Auto-generated method stub
		this.connectionview=new MFConnectionView(MainActivity.Instance);
		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams1.leftMargin=0;
		layoutParams1.topMargin=0;
		layoutParams1.width=10;
		layoutParams1.height=10;
		this.connectionview.setLayoutParams(layoutParams1);
		this.connectionview.setVisibility(View.INVISIBLE);
	}

	private void InitTemplate() {
		// TODO Auto-generated method stub
		this.layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		this.layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		this.template = new RelativeLayout(MainActivity.Instance);
		this.template.setLayoutParams(layoutParams);
	}

	//����Ƿ���Ҫ�л�pbu
	private void m_controller_Elapsed() {
		// TODO Auto-generated method stub
		this.mTimer.stop();

		try 
		{

			MainActivity.Instance.Clock.CheckIsDateChanged();
			/*	try
			{
				if (this.m_force_stop_trigger == true || (this.EndTime != MainActivity.Instance.Clock.Now && DateTimeHelper.CompareTime(this.EndTime,MainActivity.Instance.Clock.Now)<=0))
				{
					this.m_force_stop_trigger = false;

					this.EndTime = MainActivity.Instance.Clock.Now;

					//ֹͣtrigger

					if (this.m_current_trigger_pbu != null)
					{
						if (this.m_current_trigger_pbu.pbuTriggerAction.equals(TriggerActionType.App))
						{
							if (this.m_current_trigger_pbu != null && this.m_current_trigger_pbu.internalTriggers!=null)
								for (VirtualTrigger vt :this.m_current_trigger_pbu.internalTriggers)
								{
									vt.StopTrigger();
								}

						}
						else if (this.m_current_trigger_pbu.pbuTriggerAction .equals (TriggerActionType.PBU))
						{
							this.m_playing_pbu.pbuTriggerAction = TriggerActionType.App;
							this.m_playing_pbu.isTriggered = false;
							if (this.m_current_trigger_pbu != null && this.m_current_trigger_pbu.internalTriggers!=null)
								for (VirtualTrigger vt :this.m_current_trigger_pbu.internalTriggers)
								{
									vt.DisposeTrigger();
								}
						}
						this.m_current_trigger_pbu = null;
					}
				}
				else if (DateTimeHelper.CompareTime(this.EndTime, MainActivity.Instance.Clock.Now) !=0  && DateTimeHelper.CompareTime(this.EndTime,MainActivity.Instance.Clock.Now)>0)
				{
					return;
				}
			}
			catch(Exception ex) 
			{
				ex.printStackTrace();
			}*/

			this.m_restart_locker.lock();

			if (this.m_pbu_stack==null||this.m_pbu_stack.size() == 0)
				return;

			PBU pbu = this.m_pbu_stack.peek();
			if (this.m_pbu_based_timeline)
			{
				//����pbu
				if (DateTimeHelper.CompareTime(MainActivity.Instance.Clock.Now,pbu.StartTime) >= 0
						&& DateTimeHelper.CompareTime(MainActivity.Instance.Clock.Now,pbu.EndTime) <= 0) 
				{
					//LoggerHelper.WriteLogfortxt("now:"+DateTimeHelper.ConvertToString(MainActivity.Instance.Clock.Now,"hh:mm:ss"));
					this.BeforeChangePBU(pbu);
				} 
				else
				{
					this.m_pbu_stack.remove(pbu);
				}
			}
			else
			{
				if (pbu.isResetTime == false)
				{
					pbu.isResetTime = true;
					pbu.StartTime = MainActivity.Instance.Clock.Now;
					pbu.EndTime = DateTimeHelper.GetAddedDate(MainActivity.Instance.Clock.Now,pbu.OriginalDuration,MainActivity.Instance.PlayerSetting.Timezone);
					this.BeforeChangePBU(pbu);
				}
				else
				{
					if (DateTimeHelper.CompareTime(MainActivity.Instance.Clock.Now, pbu.EndTime)>=0)
						this.m_pbu_stack.remove(pbu);
				}
			}
			this.m_restart_locker.unlock();
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher m_controller_Elapsed==>"+e.getMessage());
		}
		finally
		{
			this.mTimer.start(interval);

		}
	}



	//�л�pbuǰ
	private void BeforeChangePBU(PBU pbu) {
		// TODO Auto-generated method stub
		try {
			// �������PBU��ͬ������,����ͬ�Ļ��������¼���
			boolean result=this.IsPBUEqual(pbu);
			if (!result)
			{

				/*if (pbu.isTriggered == false && this.m_playing_pbu != null &&this.m_playing_pbu.internalTriggers!=null)
				{
					for (VirtualTrigger vt :this.m_playing_pbu.internalTriggers)
					{
						vt.DisposeTrigger();
					}
				}*/
				this.AfterChangePBU(m_playing_pbu, pbu);

			}
			this.m_playing_pbu=pbu;

		} catch (Exception e) {
			LoggerHelper.WriteLogfortxt("PBUDispatcher BeforeChangePBU==>"+e.getMessage());
		}
	}

	//�л�pbu��
	private void AfterChangePBU(PBU closing_pbu, PBU playing_pbu) {
		this.m_screen_locker.lock();

		// TODO Auto-generated method stub
		try
		{

			//LoggerHelper.WriteLogfortxt("End Componenets");
			this.EndComponents(closing_pbu);

			//LoggerHelper.WriteLogfortxt("Clean screen");
			this.ClearScreen();

			this.m_playing_pbu=playing_pbu;
			// ��ȾPBU


			//LoggerHelper.WriteLogfortxt("Render pbu");
			this.RenderPBU(playing_pbu);



			//trigger
			/*if (playing_pbu.isTriggered == false)
				{
					List<VirtualTrigger> internalTriggers = playing_pbu.internalTriggers;

					if (internalTriggers!=null)
					{
						for (VirtualTrigger vt : internalTriggers)
						{
							// �����PBU�ϵ�ÿ��Trigger��init Trigger
							vt.InitTrigger();
						}
					}
					MainActivity.Instance.TcpListener.SetTriggerObject(playing_pbu);

				}*/

		}
		catch(Exception e)
		{
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher AfterChangePBU==>"+e.getMessage());
		}
		finally
		{
			this.m_screen_locker.unlock();

		}

	}

	/*
	 * ��Ⱦpbu
	 */
	private void RenderPBU(PBU pbu) {
		// TODO Auto-generated method stub
		try
		{
			/*	synchronized(this.m_screen_locker)
			{*/
			//LoggerHelper.WriteLogfortxt("Begin Render PBU");
			MainActivity.Instance.Render();
			//LoggerHelper.WriteLogfortxt("Begin Render Template");
			this.RenderTemplate(pbu);
			//LoggerHelper.WriteLogfortxt("Begin Render Components");
			this.RenderComponents(pbu);
			//LoggerHelper.WriteLogfortxt("Begin Render Connection");
			this.RenderMFConnection();
			//LoggerHelper.WriteLogfortxt("Begin Render Download");
			this.RenderDownloadState();
			//}

		}
		catch(Exception e)
		{
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderPBU==>"+e.getMessage());

		}

	}

	private void RenderDownloadState() {
		// TODO Auto-generated method stub
		try
		{


			this.template.addView(this.downloadview);
			this.template.bringChildToFront(this.downloadview);
			this.template.addView(this.downloadspeedview);
			this.template.bringChildToFront(this.downloadspeedview);
			this.ChangeDownloadState(MainActivity.Instance.FileManager.DownloadState);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderDownloadState==>"+ex.getMessage());
		}
	}
	private void ChangeDownloadState(boolean state) 
	{

		if(MainActivity.Instance.PlayerSetting.ShowNetwork==false)
			return;

		// TODO Auto-generated method stub
		if(this.downloadview.getVisibility()!=View.VISIBLE&&state&&MainActivity.Instance.PlayerSetting.ShowNetwork)
		{
			this.downloadview.setVisibility(View.VISIBLE);
			this.downloadspeedview.setVisibility(View.VISIBLE);
		}
		else if((this.downloadview.getVisibility()==View.VISIBLE&&!state)||MainActivity.Instance.PlayerSetting.ShowNetwork==false)
		{
			this.downloadview.setVisibility(View.INVISIBLE);
			this.downloadspeedview.setVisibility(View.INVISIBLE);
		}
	}

	private void ShowDownloadSpeed(String speed)
	{

		if(MainActivity.Instance.PlayerSetting.ShowNetwork){
			this.downloadspeedview.setText(speed);
			this.downloadspeedview.setVisibility(View.VISIBLE);
		}else {
			this.downloadspeedview.setText("");
			this.downloadspeedview.setVisibility(View.INVISIBLE);
		}
	}

	private void RenderMFConnection() {
		// TODO Auto-generated method stub
		try
		{
			template.addView(this.connectionview);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderMFConnection==>"+ex.getMessage());
		}
	}
	/*
	 * ��ȾComponents
	 */
	private void RenderComponents(PBU pbu) {
		// TODO Auto-generated method stub
		try
		{
			for (BasicComponent component : pbu.Components) {
				//component.SetTop();
				component.Render();

			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderComponents==>"+e.getMessage());
		}
	}

	/*
	 * ��Ⱦ����ͼƬ
	 */
	@SuppressWarnings("deprecation")
	public void RenderBackImage()
	{
		try
		{
			MediaFile backfile=this.m_playing_pbu.Template.BackMediaFile;
			if(backfile==null)
				return ;
			String path=backfile.FilePath.replace("\\", "/");
			if(backfile.MediaSource.equalsIgnoreCase("local")){
				path=PlayerStoragePath.ImageStorage+path;
			}
			//Bitmap bmp=BitmapFactory.decodeFile(path);
			Bitmap bmp=ImageHelper.readBitmapAutoSize(path,this.template.getLayoutParams().width, this.template.getLayoutParams().height);
			if(bmp!=null)
			{
				Drawable drawable =new BitmapDrawable(bmp);
				this.template.setBackground(drawable);
				/*if(!bmp.isRecycled())
				{
					bmp.recycle();
					bmp=null;
				}*/
				System.gc(); 
			}
		}
		catch(Exception e)
		{
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderBackImage==>"+e.getMessage());
		}

	}

	/*
	 * ��Ⱦtemplate
	 */
	@SuppressWarnings("deprecation")
	private void RenderTemplate(PBU pbu) {
		// TODO Auto-generated method stub
		try {
			int tmeplate_width = pbu.Template.Width;
			int template_height = pbu.Template.Height;
			int template_color=pbu.Template.BackColor;
			MediaFile backfile=pbu.Template.BackMediaFile;


			layoutParams.height = template_height;
			layoutParams.width = tmeplate_width;
			layoutParams.topMargin = 0;
			layoutParams.leftMargin = 0;


			/*downloadspeedParams.leftMargin=tmeplate_width-70;
			downloadspeedParams.topMargin=template_height-25;*/

			downloadspeedParams.rightMargin=30;
			//downloadspeedParams.leftMargin=tmeplate_width-100;
			downloadspeedParams.topMargin=20;


			downloadParams.leftMargin=tmeplate_width-35;
			downloadParams.topMargin=0;

			if(backfile!=null)
			{
				String path=backfile.FilePath.replace("\\", "/");
				if(!backfile.MediaSource.equalsIgnoreCase("local")){
					path=PlayerStoragePath.ImageStorage+path;
				}
				
				File file = new File(path);
				if (file.exists()) 
				{
					Bitmap bmp=ImageHelper.readBitmapAutoSize(path, tmeplate_width, template_height);
					if(bmp!=null)
					{
						Drawable drawable =new BitmapDrawable(bmp);
						template.setBackground(drawable);
						/*if(!bmp.isRecycled())
						{
							bmp.recycle();
							bmp=null;
						}*/
						System.gc(); 
					}

				}
				else
				{

					//�Ժ� 
					Hashtable<String, Object> templateInfo = new Hashtable<String, Object>();
					templateInfo.put("BackPhotoPath", path);
					TemplateImageTask templateImage = new TemplateImageTask(templateInfo);
					templateImage.start();

					template.setBackgroundColor(template_color);
				}
			}
			else
				template.setBackgroundColor(template_color);
			//MainActivity.Instance.Template = template;
			MainActivity.Instance.Screen.addView(template);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("PBUDispatcher RenderTemplate==>"+e.getMessage());
		}
	}

	private void EndComponents(PBU closing_pbu)
	{
		try
		{
			/*synchronized(this.m_screen_locker)
			{*/
			if (closing_pbu != null) 
			{

				for (BasicComponent component : closing_pbu.Components)
				{

					component.End();
				}
			}
			//}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("PBUDispatcher EndComponents==>"+e.getMessage());
		}
	}

	//�����Ļ
	private void ClearScreen() {
		// TODO Auto-generated method stub
		try{
			/*synchronized(this.m_screen_locker)
			{*/
			if(template!=null)
			{
				template.removeAllViews();
				MainActivity.Instance.Screen.removeView(template);
			}
			//}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("PBUDispatcher ClearScreen==>"+e.getMessage());
		}
	}

	/*
	 * �Ƚ�����pbu�Ƿ���ͬ
	 */
	private boolean IsPBUEqual(PBU pbu) {
		// TODO Auto-generated method stub
		boolean result = false;
		// ���ǰ��PBUΪ�գ�ֱ���϶�����ͬ
		if (this.m_playing_pbu == null || pbu == null)
			return false;
		else 
		{
			// ���PBU ID��һ��ֱ���϶�����ͬ
			if (!this.m_playing_pbu.ID.equalsIgnoreCase(pbu.ID))
			{
				return false;
			}
			else if (this.m_playing_pbu == pbu)
				return true;
			else if (this.m_pbu_deep_compare)
			{
				this.m_pbu_deep_compare = false;

				if (IsComparePBU == false)
					return false;

				result= PBUCompareHelper.IsPBUCompare(this.m_playing_pbu, pbu);

				return result;
			}
		}
		return result;
	}

	//timer��ʼ
	@Override
	public void Restart() {
		this.m_restart_locker.lock();
		try
		{


			LoggerHelper.WriteLogfortxt("PBUDispatcher Restart");
			this.m_pbu_deep_compare = true;
			this.m_pbu_stack=MainActivity.Instance.ScheduleLoader.pbus;
			this.m_pbu_idlist=MainActivity.Instance.ScheduleLoader.pbuid_list;
			this.mTimer.start(0,interval);
			/*PBU pbu = this.m_pbu_stack.peek();
			LoggerHelper.WriteLogfortxt("First pbu==>Start:"+pbu.StartTime);*/


		}
		catch(Exception e)
		{
			LoggerHelper.WriteLogfortxt("PBUDispatcher Restart==>"+e.getMessage());
		}
		finally
		{
			m_restart_locker.unlock();
		}

	}
	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		//this.mTimer.stop();
	}

	//trigger app
	public boolean triggerOtherApp(int interval, boolean mute, VirtualTrigger trigger)
	{
		try
		{
			if (this.m_playing_pbu.isTriggered == false)
			{
				if (this.m_playing_pbu == null || this.m_playing_pbu.internalTriggers == null || this.m_playing_pbu.internalTriggers.contains(trigger) == false)
				{
					LoggerHelper.WriteLogfortxt("PBUDispatcher triggerOtherApp==> old trigger not dispose");
					return false;
				}

			}
			else
			{
				if (this.m_current_trigger_pbu != null && this.m_current_trigger_pbu.internalTriggers != null && this.m_current_trigger_pbu.internalTriggers.contains(trigger) == false)
				{
					LoggerHelper.WriteLogfortxt("PBUDispatcher triggerOtherApp==> old trigger not dispose");
					return false;
				}

			}

			if (this.m_pbu_based_timeline == false)
			{
				this.m_playing_pbu.isResetTime = false;
			}


			if (this.m_playing_pbu.isTriggered == false)
				this.m_current_trigger_pbu = this.m_playing_pbu;



			this.EndTime =DateTimeHelper.GetAddedDate(MainActivity.Instance.Clock.Now,interval,MainActivity.Instance.PlayerSetting.Timezone);
		}
		catch (Exception ex)
		{ 
			LoggerHelper.WriteLogfortxt("PBUDispatcher triggerOtherApp ==> " + ex.getMessage());
		}
		return true;
	}

	public void triggerPBU(PBU pbu, int interval, VirtualTrigger trigger)
	{
		try
		{
			if (this.m_playing_pbu == null)
				return;

			this.m_playing_pbu.pbuTriggerAction = TriggerActionType.PBU;

			if (this.triggerOtherApp(interval, false, trigger) == false)
				return;

			if (this.m_playing_pbu.ID.equalsIgnoreCase(pbu.ID))
			{
				if (this.m_playing_pbu.isTriggered == false)
				{
					this.m_playing_pbu.isTriggered = true;
				}
			}
			else
			{
				LoggerHelper.WriteLogfortxt("PBUDispatcher Trigger PBU ==> " + pbu.ID);

				pbu.isTriggered = true;

				this.BeforeChangePBU(pbu);
			}
		}
		catch (Exception ex)
		{ 
			LoggerHelper.WriteLogfortxt("PBUDispatcher triggerPBU ==> " + ex.getMessage());
		}
	}

}


