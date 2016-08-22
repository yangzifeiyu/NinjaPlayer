package com.mfusion.templatedesigner.previewcomponent.subview;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




public class CustomerVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback { 

	private MediaPlayer mediaPlayer; 
	protected SurfaceHolder surfaceHolder; 

	private String mediaPath;
	
	private Boolean isMute=false;
	
	public CustomerVideoSurfaceView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
		this.initVideoView(); 
	} 
	public CustomerVideoSurfaceView(Context context) { 
		super(context); 
		this.initVideoView(); 
	} 

	private void initVideoView() {
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
			mediaPath=path;
			isMute=mute;
			
			if(this.mediaPlayer==null)
				return false;
			
			this.mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
				}
			});
			if(mute)
			{
				this.mediaPlayer.setVolume(0, 0);
			}
			
			this.mediaPlayer.reset();
			this.mediaPlayer.setDataSource(path);
			this.mediaPlayer.prepare();
			this.mediaPlayer.start();
			
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

	} 
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{  
		try
		{
			this.mediaPlayer=new MediaPlayer();
			this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.mediaPlayer.setDisplay(surfaceHolder);
			
			if(mediaPath!=null&&!mediaPath.isEmpty())
				setVideo(mediaPath,isMute);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
