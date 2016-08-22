package com.mfusion.player.common.Entity.Control;

import java.io.IOException;

import com.mfusion.player.common.Entity.View.MyVideoView;

import com.mfusion.player.library.Helper.LoggerHelper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;


import android.view.View;
import android.widget.MediaController;

public class VideoControl extends AControl {
	//private String path="";
	
	public VideoControl(Context context)
	{
		this.CreateControl(context);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		this.Stop();
		this.Element=null;
	}
	
	@Override
	public void CreateControl(Context context) {
		try
		{
			// TODO Auto-generated method stub
			MediaController mController= new MediaController(context);
			mController.setVisibility(View.INVISIBLE);// ȥ�������
			MyVideoView m_Video = new MyVideoView(context);
			m_Video.requestFocus();
			m_Video.setMediaController(mController);
			mController.setMediaPlayer(m_Video);
			m_Video.setVisibility(View.INVISIBLE);
			this.Element=m_Video;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("VideoControl CreateController==>"+ex.getMessage());	
		}
	}
	public void LoadVideo(String videopath,boolean mute)
	{
		try
		{

			//this.path=videopath;
			
			if(mute)
			{
				((MyVideoView)this.Element).setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.setVolume(0.0f, 0.0f);
					}
				});
			}
			Uri uri = Uri.parse(videopath); 
			((MyVideoView)this.Element).setVideoURI(uri);

			((MyVideoView)this.Element).start();
		}
		catch(Exception ex)
		{

			LoggerHelper.WriteLogfortxt("VideoControl LoadVideo==>"+ex.getMessage());
		}
	}

	public void Stop()
	{
		if(this.Element!=null)
		{

			((MyVideoView)this.Element).pause();
			((MyVideoView)this.Element).stopPlayback();
			

		}
	}

	/*
	 * Mute
	 */
	public void Mute(boolean mute) {
		// TODO Auto-generated method stub
		if(mute)
		{
			/*((MyVideoView)this.Element).setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.setVolume(0f,0f);
				}
			});*/
		}
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}
}
