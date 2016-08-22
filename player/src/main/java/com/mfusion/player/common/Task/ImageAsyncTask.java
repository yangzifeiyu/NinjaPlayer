package com.mfusion.player.common.Task;

import com.mfusion.player.library.Helper.ImageHelper;
import com.mfusion.player.common.Entity.View.RecycleBitmapDrawable;
import com.mfusion.player.common.Entity.View.RecycleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ImageAsyncTask extends AsyncTask<String, Integer, String> {
	
	Context context;
	
	RecycleImageView imageView;
	
	String imagePath;
	
	Integer imageW,imageH;
	
	Bitmap imageBitmap;
	
	public ImageAsyncTask(Context context,RecycleImageView view,String url,int width,int height){
		this.context=context;
		this.imageView=view;
		this.imagePath=url;
		this.imageW=width;
		this.imageH=height;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		try {
			
			imageBitmap=ImageHelper.readBitmapAutoSize(this.imagePath, this.imageW, this.imageH);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override  
    protected void onPostExecute(String result) {  
        try {
			
        	this.imageView.setImageDrawable(new RecycleBitmapDrawable(context.getResources(), this.imageBitmap));
        	
        	super.cancel(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
	
	public void cancelTask(){
		try {

			super.cancel(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
