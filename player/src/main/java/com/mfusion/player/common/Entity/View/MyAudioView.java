package com.mfusion.player.common.Entity.View;

import java.io.IOException;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;

public class MyAudioView extends View{

	public MediaPlayer  mediaPlayer;  
	public Caller caller;

	public MyAudioView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mediaPlayer=new MediaPlayer(); 
		mediaPlayer.setOnCompletionListener(new OnCompletionListener(){  
			@Override  
			public void onCompletion(MediaPlayer mp) {  
				//mp.release();//�ͷ���Ƶ��Դ  

				if(caller!=null)  
					caller.call("");
			}  
		});  
	}

	public void PlayFile(String path)
	{
		try {
			if(mediaPlayer!=null)
			{
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();  
			}
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

	public void Stop()
	{
		try
		{
			if(mediaPlayer!=null)
			{
				mediaPlayer.pause();
				mediaPlayer.stop();
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MyAudioView Stop==>"+ex.getMessage());
		}
	}

}
