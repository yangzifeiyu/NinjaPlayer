package com.mfusion.player.common.Entity.Control;

import com.mfusion.player.library.Helper.LoggerHelper;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class GifControl extends AControl{

	private GifDrawable gifFromPath;
	public GifControl(Context context)
	{

		this.CreateControl(context);
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
		this.Element = null;
	}

	@Override
	public void CreateControl(Context context) {
		// TODO Auto-generated method stub
		try
		{
			GifImageView gif=new GifImageView(context);
			 // ���ü��ط�ʽ���ȼ��غ���ʾ���߼��ر���ʾ��ֻ��ʾ��һ֡����ʾ  
			gif.setScaleType(ImageView.ScaleType.FIT_XY);
			gif.setVisibility(View.INVISIBLE);
			this.Element=gif;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("GifControl Create==>"+ex.getMessage());
		}
	}

	public void LoadGif(String path)
	{
		try
		{
			gifFromPath = new GifDrawable(path);
			if(gifFromPath!=null)
			{
				((GifImageView)this.Element).setBackground(gifFromPath);
				((GifImageView)this.Element).animate();

			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("GifControl LoadGif==>"+ex.getMessage());
		}
	}
	
	
	public void Stop()
	{
		if(gifFromPath!=null)
		{
			gifFromPath.pause();
			//gifFromPath.recycle();
			//gifFromPath=null;
			gifFromPath.stop();
			gifFromPath=null;
			System.gc();
		}
	}
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}

}
