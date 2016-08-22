package com.mfusion.player.common.Entity.View;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class CustomerVideoView extends VideoView{

	private MediaController mController;
	private Context context;
	private String path;
	public CustomerVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;

	}

	public boolean setVideo(String videopath,boolean mute) 
	{
		boolean result=false;
		try
		{
			this.path=videopath;
			this.mController= new MediaController(context);
			this.mController.setVisibility(View.INVISIBLE);// ȥ�������
			this.setMediaController(this.mController);
			this.mController.setMediaPlayer(this);
			this.mController.setKeepScreenOn(true);
			this.mController.setClickable(false);

			if(mute)
			{
				this.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.setVolume(0f,0f);
					}
				});
			}
			/*	this.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				try {
					mp.reset();
					mp.setDataSource(path);
					mp.prepare();
					mp.start();

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});*/

			this.setVideoPath(videopath);
			this.start();
			result=true;
		}
		catch(Exception ex)
		{

		}
		return result;
	}

	public void stop() {

		this.stopPlayback();

		if(this.mController!=null)
		{

			this.mController.removeView(this);

			this.mController=null;

		}


	}

}
