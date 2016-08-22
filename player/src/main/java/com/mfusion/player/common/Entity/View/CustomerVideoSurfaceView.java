package com.mfusion.player.common.Entity.View;



import java.util.Date;

import com.mfusion.player.common.Player.MainActivity;

import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




public class CustomerVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback { 

	private MediaPlayer mediaPlayer; 
	//private int width; 
	//private int height; 
	protected SurfaceHolder surfaceHolder; 


	public CustomerVideoSurfaceView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		this.initVideoView(); 
	} 
	public CustomerVideoSurfaceView(Context context) { 
		super(context); 
		this.initVideoView(); 
	} 




	private void initVideoView() {
		//setZOrderOnTop(true);
		//setZOrderMediaOverlay(true);
		//this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);


	}

	private void release(boolean cleartargetstate) { 
		if (mediaPlayer != null) 
		{
			mediaPlayer.stop();
			mediaPlayer.reset(); 
			mediaPlayer.release(); 
			mediaPlayer = null; 
		} 
	} 

	//Date time;
	public boolean setVideo(String path,boolean mute) { 


		boolean result=false;
		try 
		{

			//this.width=this.getLayoutParams().width;
			//this.height=this.getLayoutParams().height;
			if(this.mediaPlayer!=null)
			{
				this.mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						//LoggerHelper.WriteLogfortxt("Prepared");
						//Date playtime=MainActivity.Instance.Clock.Now;
						//int delay=DateTimeHelper.GetDuration(playtime, time);
						
						
					}
				});
				if(mute)
				{
					this.mediaPlayer.setVolume(0, 0);
				}
				
				this.mediaPlayer.reset();
				this.mediaPlayer.setDataSource(path);
				this.mediaPlayer.prepare();
				//time=MainActivity.Instance.Clock.Now;
				//this.mediaPlayer.start();
				//this.mediaPlayer.seekTo(2000);
				this.mediaPlayer.start();
				//mp.seekTo(delay*1000);
				
			   
			   
			
			}
			result=true;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
		}
		return result;

	} 



	public void stop()  
	{  
		release(true); 
	}  



	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, 
			int h) {
		//this.surfaceHolder.setFixedSize(w, h);

	} 
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{  
		try
		{
			this.mediaPlayer=new MediaPlayer();
			this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.mediaPlayer.setDisplay(surfaceHolder);
			

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("CustomerVideoSurfaceView surfaceCreated==>"+ex.getMessage());
		}
	} 
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { 

		release(true); 
	} 

	public void setTop()
	{
		setZOrderOnTop(true);
		setZOrderMediaOverlay(true);
	}

}