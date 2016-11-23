/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *ScheduleMediaComponent
 */
package com.mfusion.player.common.Entity.Components;

import java.util.Random;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Control.ErrorControl;
import com.mfusion.player.common.Entity.Control.GifControl;
import com.mfusion.player.common.Entity.Control.ImageControl;
import com.mfusion.player.common.Entity.Control.VideoControl1;
import com.mfusion.player.common.Entity.Control.WebControl;
import com.mfusion.player.common.Entity.Effect.TransitionEffectHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Enum.PlayMode;
import com.mfusion.player.common.Enum.TransitionEffectType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.ScheduleMediaSetting;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class ScheduleMediaComponent extends BasicComponent {

	private HandleTimer timer;                        //����playlist�л�
	private int minterval=1000;

	private ImageControl ImageControl1;
	private ImageControl ImageControl2;
	private GifControl   GifControl1;
	private GifControl  GifControl2;

	private VideoControl1 VideoControl1;
	private VideoControl1 VideoControl2;

	private ErrorControl ErrorControl1;
	private ErrorControl ErrorControl2;

	private WebControl WebControl1;
	private WebControl WebControl2;

	private int CurrentFileIndex = 0;
	private MediaFile mediafile;
	public ScheduleMediaSetting setting;
	private TransitionEffectHelper transition;
	private View m_CurView;
	private View m_PreView;


	@SuppressLint("SetJavaScriptEnabled")
	public ScheduleMediaComponent(Context context) {
		super(context);
		try
		{
			transition=new TransitionEffectHelper();
			VideoControl1=(VideoControl1) Helper.ControlManager.GetControl(ControlType.Video);
			VideoControl2=(VideoControl1) Helper.ControlManager.GetControl(ControlType.Video);
			ImageControl1=(ImageControl) Helper.ControlManager.GetControl(ControlType.Image);
			ImageControl2=(ImageControl) Helper.ControlManager.GetControl(ControlType.Image);
			GifControl1=(GifControl) Helper.ControlManager.GetControl(ControlType.Image,"Gif");
			GifControl2=(GifControl) Helper.ControlManager.GetControl(ControlType.Image,"Gif");

			ErrorControl1=(ErrorControl) Helper.ControlManager.GetControl(ControlType.Error);
			ErrorControl2=(ErrorControl) Helper.ControlManager.GetControl(ControlType.Error);

			WebControl1=(WebControl) Helper.ControlManager.GetControl(ControlType.WebPage);
			WebControl2=(WebControl) Helper.ControlManager.GetControl(ControlType.WebPage);


			this.setCmpcontext(VideoControl1.Element);
			this.setCmpcontext(VideoControl2.Element);
			this.setCmpcontext(ErrorControl1.Element);
			this.setCmpcontext(ErrorControl2.Element);
			this.setCmpcontext(ImageControl1.Element);
			this.setCmpcontext(ImageControl2.Element);
			this.setCmpcontext(GifControl1.Element);
			this.setCmpcontext(GifControl2.Element);
			this.setCmpcontext(WebControl1.Element);
			this.setCmpcontext(WebControl2.Element);

			timer = new HandleTimer() {
				@Override
				protected void onTime() 
				{
					playTimer_Tick();
				}
			};
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent==>"+ex.getMessage());
		}

	}

	/*
	 * playlist file�л�
	 */
	protected void playTimer_Tick() {
		// TODO Auto-generated method stub
		//LoggerHelper.WriteLogfortxt("ScheduleMediaComponent timer begin");
		timer.stop();
		
		int interval = 0;
		try
		{

			if (this.setting.Idleplaylist!=null&&this.setting.Idleplaylist.Medias.size() != 0) {

				
				mediafile =this.setting.Idleplaylist.Medias.get(this.CurrentFileIndex);

				// ����ļ��Ƿ����
				String mediaSourcePath = mediafile.FilePath;

				FileType fileType = mediafile.Type;


				VideoControl2.Stop();

				VideoControl1.Stop();

				GifControl1.Stop();

				GifControl2.Stop();
				
				WebControl1.Stop();
				
				WebControl2.Stop();
				

				if(!fileType.equals(FileType.Html)&&!fileType.equals(FileType.Streaming))
				{
					mediaSourcePath = mediaSourcePath.replace("\\", "/");
					if(!mediafile.MediaSource.equalsIgnoreCase("local")){
						
						String storage=PlayerStoragePath.ImageStorage;
						if(fileType.equals(FileType.Image))
						{
							storage=PlayerStoragePath.ImageStorage;
						}
						else if(fileType.equals(FileType.Video))
						{
							storage=PlayerStoragePath.VideoStorage;
						}

						mediaSourcePath=storage+mediaSourcePath;
					}
					
					//������
					if (FileHelper.IsExists(mediaSourcePath)) 
					{

						interval = mediafile.Duration * 1000;
						this.m_PreView=this.m_CurView;
						if (fileType.equals(FileType.Image)) 
						{
							if(mediafile.ExtName.equalsIgnoreCase(".gif"))
							{
								if(GifControl1.Element.getVisibility()!=View.VISIBLE)
								{
									this.container.bringChildToFront(GifControl1.Element);
									GifControl1.Element.setVisibility(View.VISIBLE);
									GifControl1.LoadGif(mediaSourcePath);	
									m_CurView=GifControl1.Element;

								}
								else
								{
									this.container.bringChildToFront(GifControl2.Element);
									GifControl2.Element.setVisibility(View.VISIBLE);
									GifControl2.LoadGif(mediaSourcePath);	
									m_CurView=GifControl2.Element;

								}
							}
							else
							{
								if(ImageControl1.Element.getVisibility()!=View.VISIBLE)
								{
									this.container.bringChildToFront(ImageControl1.Element);
									ImageControl1.Element.setVisibility(View.VISIBLE);
									//ImageControl1.LoadImage(mediaSourcePath,this.getCmpwidth(),this.getCmpheight());
									m_CurView=ImageControl1.Element;

								}
								else
								{
									this.container.bringChildToFront(ImageControl2.Element);
									ImageControl2.Element.setVisibility(View.VISIBLE);
									//ImageControl2.LoadImage(mediaSourcePath,this.getCmpwidth(),this.getCmpheight());
									m_CurView=ImageControl2.Element;

								}
							}
							//LoggerHelper.WriteLogfortxt("ScheduleMediaComponent play Image");

						} 
						else if (fileType.equals(FileType.Video))
						{
							boolean result=false;
							if(VideoControl1.Element.getVisibility()!=View.VISIBLE)
							{
								this.container.bringChildToFront(VideoControl1.Element);
								VideoControl1.Element.setVisibility(View.VISIBLE);
								result=VideoControl1.LoadVideo(mediaSourcePath,setting.Mute);
								if(result)
								{
									m_CurView=VideoControl1.Element;
								}


							}
							else
							{
								this.container.bringChildToFront(VideoControl2.Element);
								VideoControl2.Element.setVisibility(View.VISIBLE);
								result=VideoControl2.LoadVideo(mediaSourcePath,setting.Mute);
								if(result)
								{

									m_CurView=VideoControl2.Element;
								}

							}
							if(!result)
							{
								
								if(ErrorControl1.Element.getVisibility()!=View.VISIBLE)
								{

									this.container.bringChildToFront(ErrorControl1.Element);
									((ErrorControl)ErrorControl1).SetErrorMessage(mediafile.FilePath);
									ErrorControl1.Element.setVisibility(View.VISIBLE);
									m_CurView=ErrorControl1.Element;

								}
								else
								{

									this.container.bringChildToFront(ErrorControl2.Element);
									((ErrorControl)ErrorControl2).SetErrorMessage(mediafile.FilePath);
									ErrorControl2.Element.setVisibility(View.VISIBLE);
									m_CurView=ErrorControl2.Element;


								}
							}

							//LoggerHelper.WriteLogfortxt("ScheduleMediaComponent play Video");

						}
						if(m_PreView!=null)
						{
							TransitionEffectType type=TransitionEffectType.fromString(mediafile.Effect);
							transition.SetAnimation(type,m_CurView,this.m_PreView);
						}
						

					}
					else
					{
						interval=View.VISIBLE;
					}
				}
				else if(fileType.equals(FileType.Html))
				{
					this.m_PreView=this.m_CurView;
					interval = mediafile.Duration * 1000;
					if(WebControl1.Element.getVisibility()!=View.VISIBLE)
					{
						this.container.bringChildToFront(WebControl1.Element);
						this.WebControl1.LoadUrl(mediaSourcePath); 
						this.WebControl1.Element.setVisibility(View.VISIBLE);
						m_CurView=WebControl1.Element;
					}
					else
					{
						this.container.bringChildToFront(WebControl2.Element);
						this.WebControl2.LoadUrl(mediaSourcePath); 
						this.WebControl2.Element.setVisibility(View.VISIBLE);
						m_CurView=WebControl2.Element;
					}
					if(m_PreView!=null)
					{
						TransitionEffectType type=TransitionEffectType.fromString(mediafile.Effect);
						transition.SetAnimation(type,m_CurView,this.m_PreView);
					}
				}
				else if(fileType.equals(FileType.Streaming))
				{
					this.m_PreView=this.m_CurView;
					interval = mediafile.Duration * 1000;
					boolean result=false;
					if(VideoControl1.Element.getVisibility()!=View.VISIBLE)
					{
						this.container.bringChildToFront(VideoControl1.Element);
						VideoControl1.Element.setVisibility(View.VISIBLE);
						result=VideoControl1.LoadVideo(mediaSourcePath,setting.Mute);
						if(result)
						{
							m_CurView=VideoControl1.Element;
						}


					}
					else
					{
						this.container.bringChildToFront(VideoControl2.Element);
						VideoControl2.Element.setVisibility(View.VISIBLE);
						result=VideoControl2.LoadVideo(mediaSourcePath,setting.Mute);
						if(result)
						{

							m_CurView=VideoControl2.Element;
						}

					}
					if(!result)
					{
						
						if(ErrorControl1.Element.getVisibility()!=View.VISIBLE)
						{

							this.container.bringChildToFront(ErrorControl1.Element);
							((ErrorControl)ErrorControl1).SetErrorMessage(mediafile.FilePath);
							ErrorControl1.Element.setVisibility(View.VISIBLE);
							m_CurView=ErrorControl1.Element;

						}
						else
						{
							this.container.bringChildToFront(ErrorControl2.Element);
							((ErrorControl)ErrorControl2).SetErrorMessage(mediafile.FilePath);
							ErrorControl2.Element.setVisibility(View.VISIBLE);
							m_CurView=ErrorControl2.Element;


						}
					}
					if(m_PreView!=null)
					{
						TransitionEffectType type=TransitionEffectType.fromString(mediafile.Effect);
						transition.SetAnimation(type,m_CurView,this.m_PreView);
					}
				}

				if(this.setting.Idleplaylist.Medias.size()==1)
					return;

				this.GetCurrentFileIndex();
				
				this.preloadImage(this.CurrentFileIndex,true);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent playTimer_Tick==>"+ex.getMessage());
		}

		this.timer.start(interval,interval);
		//LoggerHelper.WriteLogfortxt("ScheduleMediaComponent timer will start==>"+interval);

	}

	private void preloadImage(int mediaIndex,Boolean isAsync){
		try {
			MediaFile mediafile =this.setting.Idleplaylist.Medias.get(mediaIndex);
			if(mediafile.Type!=FileType.Image||mediafile.ExtName.equalsIgnoreCase(".gif"))
				return;
			
			String imagePath=mediafile.FilePath.replace("\\", "/");
			if(!mediafile.MediaSource.equalsIgnoreCase("local"))
				imagePath=PlayerStoragePath.ImageStorage+imagePath;
			
			if(!FileHelper.IsExists(imagePath))
				return;
			
			if(ImageControl1.Element.getVisibility()!=View.VISIBLE)
			{
				ImageControl1.LoadImage(imagePath,this.getCmpwidth(),this.getCmpheight(),isAsync);
			}
			else
			{
				ImageControl2.LoadImage(imagePath,this.getCmpwidth(),this.getCmpheight(),isAsync);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void GetCurrentFileIndex() {
		// TODO Auto-generated method stub
		try
		{

			if (this.setting.Idleplaylist.Mode.equals(PlayMode.random)) {
				Random random = new Random();
				this.CurrentFileIndex = random.nextInt(this.setting.Idleplaylist.Medias.size());
			} else {
				this.CurrentFileIndex += 1;
				if (this.CurrentFileIndex >= this.setting.Idleplaylist.Medias.size())
					this.CurrentFileIndex = 0;
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent GetCurrentFileIndex==>"+ex.getMessage());
		}
	}



	@Override
	public void End() {
		// TODO Auto-generated method stub
		try
		{
			//LoggerHelper.WriteLogfortxt("SchduleMedia End");
			//this.mTimer.stop();
			this.timer.stop();
			this.VideoControl1.Stop();
			this.VideoControl2.Stop();
			this.GifControl1.Stop();
			this.GifControl2.Stop();

			Helper.ControlManager.ReturnControl(this.VideoControl1);
			Helper.ControlManager.ReturnControl(this.VideoControl2);
			Helper.ControlManager.ReturnControl(this.ImageControl1);
			Helper.ControlManager.ReturnControl(this.ImageControl2);
			Helper.ControlManager.ReturnControl(this.GifControl1);
			Helper.ControlManager.ReturnControl(this.GifControl2);
			Helper.ControlManager.ReturnControl(this.WebControl1);
			Helper.ControlManager.ReturnControl(this.WebControl2);
			Helper.ControlManager.ReturnControl(this.ErrorControl1);
			Helper.ControlManager.ReturnControl(this.ErrorControl2);

			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			this.container.removeView(this.ImageControl1.Element);
			this.container.removeView(this.ImageControl2.Element);
			this.container.removeView(this.GifControl1.Element);
			this.container.removeView(this.GifControl2.Element);
			this.container.removeView(this.VideoControl1.Element);
			this.container.removeView(this.VideoControl2.Element);
			this.container.removeView(this.WebControl1.Element);
			this.container.removeView(this.WebControl2.Element);
			this.container.removeView(this.ErrorControl1.Element);
			this.container.removeView(this.ErrorControl2.Element);
			this.CurrentFileIndex=0;
			this.m_CurView=null;
			this.m_PreView=null;
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent End==>"+ex.getMessage());
		}
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			
			
			this.AddView(this.ImageControl1.Element);
			this.AddView(this.ImageControl2.Element);
			this.AddView(this.GifControl1.Element);
			this.AddView(this.GifControl2.Element);
			this.AddView(this.VideoControl1.Element);
			this.AddView(this.VideoControl2.Element);
			this.AddView(this.WebControl1.Element);
			this.AddView(this.WebControl2.Element);
			this.AddView(this.ErrorControl1.Element);
			this.AddView(this.ErrorControl2.Element);
			MainActivity.Instance.PBUDispatcher.template.addView(container);

			this.preloadImage(0,false);

			this.timer.start(0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent Render==>"+ex.getMessage());
		}
	}


	@Override
	public void Init(Object o) 
	{
		
		// TODO Auto-generated method stub
		if(o==null)
			return;
		try
		{
			this.setting=(ScheduleMediaSetting)o;

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ScheduleMediaComponent Init==>"+ex.getMessage());
		}

	}

	
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		this.VideoControl1.SetTop();
		this.VideoControl2.SetTop();
	}

}
