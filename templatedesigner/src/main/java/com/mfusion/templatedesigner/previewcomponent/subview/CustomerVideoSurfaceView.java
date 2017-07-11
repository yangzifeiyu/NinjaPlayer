package com.mfusion.templatedesigner.previewcomponent.subview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.mfusion.commons.tools.LogOperator;

import java.util.Timer;
import java.util.TimerTask;


public class CustomerVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private MediaPlayer mediaPlayer;

	private String MediaPath;

	private Boolean IsMute=false;

	private Paint paint;
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

		this.paint=new TextPaint();
		this.paint.setAntiAlias(true);
		this.paint.setStyle(Paint.Style.FILL);
		this.paint.setColor(Color.WHITE);
		this.paint.setTextSize(80);
	}

	private void release(boolean cleartargetstate) {
		System.out.println("release");
		if (mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public void setMute(boolean mute){

		this.IsMute=mute;
		if(this.mediaPlayer==null)
			return;
		float volume=this.IsMute?0:0.5f;
		this.mediaPlayer.setVolume(volume, volume);
	}
	//Date time;
	public boolean setVideo(String path,boolean mute) {

		this.MediaPath=path;
		this.IsMute=mute;
		return this.playVideo();

	}
	private boolean playVideo() {

		boolean result=false;
		try
		{

			System.out.println("Media Player Start Play 1");
			//this.width=this.getLayoutParams().width;
			//this.height=this.getLayoutParams().height;

			if(this.mediaPlayer!=null&&this.MediaPath!=null&&!this.MediaPath.isEmpty())
			{
				System.out.println("Media Player Start Play 2");
				this.mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						//LoggerHelper.WriteLogfortxt("Prepared");
						//Date playtime=MainActivity.Instance.Clock.Now;
						//int delay=DateTimeHelper.GetDuration(playtime, time);
						mediaPlayer.start();
						System.out.println("Media Player Prepared " +mediaPlayer.getDuration()+" "+mediaPlayer.getCurrentPosition());

					}
				});
				this.mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {

						System.out.println("onBufferingUpdate");
					}
				});
				this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						System.out.println("Media Player Complete");
					}
				});
				this.mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
					@Override
					public boolean onInfo(MediaPlayer mp, int what, int extra) {
						System.out.println("Media Player Info "+what +" "+extra);
						return false;
					}
				});
				this.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						System.out.println("Media Player Error");
						return false;
					}
				});
				if(this.IsMute)
				{
					this.mediaPlayer.setVolume(0, 0);
				}

				this.mediaPlayer.reset();
				this.mediaPlayer.setDataSource(this.MediaPath);
				this.mediaPlayer.prepare();

				//this.mediaPlayer.start();



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
			System.out.println("surfaceCreated");

			/*ViewGroup.LayoutParams layoutParams=this.getLayoutParams();
			layoutParams.width=400;
			layoutParams.height=400;
			this.setLayoutParams(layoutParams);*/
			/*Canvas canvas = holder.lockCanvas();//��ȡ����
			if(canvas!=null)
			{
				canvas.drawColor(Color.RED);
				canvas.drawText("Video Player Testing",50,100,paint);
			}

			holder.unlockCanvasAndPost(canvas);// �������ύ���õ�ͼ��*/
			this.mediaPlayer=new MediaPlayer();
			this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.mediaPlayer.setDisplay(this.getHolder());

			this.playVideo();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LogOperator.WriteLogfortxt("CustomerVideoSurfaceView surfaceCreated==>"+ex.getMessage());
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
		getHolder().setFormat(PixelFormat.TRANSPARENT);
	}


}
