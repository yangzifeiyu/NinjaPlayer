/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-20
 *
 *AudioComponent
 */
package com.mfusion.player.common.Entity.Components;

import java.util.Random;
import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.MediaFile;

import com.mfusion.player.common.Entity.Control.AudioControl;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Enum.PlayMode;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.AudioComponentSetting;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;



import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class AudioComponent extends BasicComponent implements MyCallInterface{

	public AudioComponentSetting setting;
	private int AudioIndex=-1;
	private AudioControl AudioPlayer;
	//private MediaFile currentMedia;

	private HandleTimer mTimer; 
	private int interval=1000;


	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try 
			{
				//PlayAudio();

			} catch (Exception ex) {
				// TODO Auto-generated catch block
				LoggerHelper.WriteLogfortxt("MainActivity handleMessage==>"+ex.getMessage());
			}

		}


	};

	public AudioComponent(Context context) {
		super(context);
		try
		{

			// TODO Auto-generated constructor stub
			AudioPlayer=(AudioControl) Helper.ControlManager.GetControl(ControlType.Audio);
			this.setCmpcontext(AudioPlayer.Element);

			mTimer = new HandleTimer() {
				@Override
				protected void onTime() {
					m_controller_Elapsed();
				}
			};

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("AudioComponent==>"+ex.getMessage());
		}
	}

	protected void m_controller_Elapsed() {
		// TODO Auto-generated method stub
		this.mTimer.stop();
		try {
			if (setting.AudioList==null||setting.AudioList.size() == 0)
				return;
			MediaFile media = this.GetAudioFile();
			if(media==null)//���Ϊnull�ټ�����
				this.mTimer.start(interval);
			else
			{
				this.PlayAudio(media);
			}			
		} 
		catch (Exception ex)
		{
			// TODO Auto-generated ncatch block
			LoggerHelper.WriteLogfortxt("AudioComponent m_controller_Elapsed==>"+ex.getMessage());
		}	
	}

	@Override
	public void End() {
		try
		{
			// TODO Auto-generated method stub
			this.mTimer.stop();
			this.AudioPlayer.SetCallback(null);
			this.AudioPlayer.Stop();
			Helper.ControlManager.ReturnControl(this.AudioPlayer);

			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			this.container.removeView(this.AudioPlayer.Element);

			this.AudioIndex=-1;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("AudioComponent End==>"+ex.getMessage());
		}

	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub

		this.AddView(this.AudioPlayer.Element);
		MainActivity.Instance.PBUDispatcher.template.addView(container);
		try
		{
			if (this.setting.AudioList.size()>0)
			{

				Caller caller=new Caller();
				caller.setI(this);
				this.AudioPlayer.SetCallback(caller);

				this.mTimer.start(0);

			}
		}
		catch (Exception ex)
		{
			LoggerHelper.WriteLogfortxt("Audio Playback raised an exception:" + ex.getMessage());
		}
	}

	@Override
	public void Init(Object o) {
		try
		{
			// TODO Auto-generated method stub
			if(o==null)
				return;
			setting=(AudioComponentSetting)o;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("AudioComponent Init==>"+ex.getMessage());
		}

	}


	/*
	 * ��ȡaudio
	 */
	private MediaFile GetAudioFile()
	{
		MediaFile media = null;
		try
		{
			if(this.setting.AudioList==null||this.setting.AudioList.size()==0)
				return null;
			else
			{
				if (this.setting.PlayMode.equals(PlayMode.sequence))
				{
					this.AudioIndex = this.AudioIndex + 1;
					if (this.AudioIndex >= this.setting.AudioList.size())
						this.AudioIndex = 0;

				}
				else
				{
					Random random = new Random();
					this.AudioIndex = random.nextInt(this.setting.AudioList.size());
				}
			}
			media = this.setting.AudioList.get(this.AudioIndex);
			String mediaSourcePath = media.FilePath.replace("\\", "/");
			if(!media.MediaSource.equalsIgnoreCase("local")){
				mediaSourcePath=PlayerStoragePath.AudioStorage+mediaSourcePath;
			}
			if (FileHelper.IsExists(mediaSourcePath))
				return media;
			else return null;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("AudioComponent GetAudioFile==>"+ex.getMessage());
			return null;
		}

	}

	@Override
	public Object fuc(Object paras) {
		// TODO Auto-generated method stub
		this.mTimer.start(0);
		return null;
	}

	/*
	 * ���ŵ�ǰaudio
	 */
	private void PlayAudio(MediaFile currentMedia) {
		// TODO Auto-generated method stub
		try
		{
			String mediaSourcePath = currentMedia.FilePath.replace("\\", "/");
			if(!currentMedia.MediaSource.equalsIgnoreCase("local")){
				mediaSourcePath=PlayerStoragePath.AudioStorage+mediaSourcePath;
			}
			this.AudioPlayer.PlayFile(mediaSourcePath);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("AudioComponent PlayAudio==>"+ex.getMessage());
		}
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}

}
