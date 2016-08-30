package com.mfusion.player.common.Entity.Control;



import com.mfusion.player.common.Entity.View.RecycleBitmapDrawable;
import com.mfusion.player.common.Entity.View.RecycleImageView;
import com.mfusion.player.common.Task.ImageAsyncTask;

import com.mfusion.player.library.Helper.ImageHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;


public class ImageControl extends AControl{

	
	private Context context=null;

	public ImageControl(Context context)
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

			this.context=context;
			RecycleImageView image=new RecycleImageView(context);
			image.setScaleType(ImageView.ScaleType.FIT_XY);

			image.setVisibility(View.INVISIBLE);

			this.Element=image;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ImageControl Create==>"+ex.getMessage());
		}
	}

	String imageUrl="";
	ImageAsyncTask imageTask;
	public void LoadImage(String url,int width,int height)
	{

		if(url.compareToIgnoreCase(this.imageUrl)==0)
			return;
		this.imageUrl=url;
		if(imageTask!=null){
			imageTask.cancelTask();
			imageTask=null;
		}
		imageTask=new ImageAsyncTask(context, (RecycleImageView)(this.Element),url, width, height);
		imageTask.execute("");
		/*try
		{		
			Bitmap bm=ImageHelper.readBitmapAutoSize(url, width, height);
			if(bm!=null)
			{
				//((ImageView)(this.Element)).setImageBitmap(bm);
				((RecycleImageView)(this.Element)).setImageDrawable(new RecycleBitmapDrawable(context.getResources(), bm));
			}
		} 
		catch (Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ImageControl LoadImage==>"+ex.getMessage());
		}*/
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}






}
