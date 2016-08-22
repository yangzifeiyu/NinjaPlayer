package com.mfusion.player.common.Entity.View;

import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;

public class CustomerImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback{  

	private SurfaceHolder holder;  
	private Bitmap bitmap;  
	private Paint paint;  
	private Matrix matrix;
	private boolean surfaceValid=false;
	private int imagewidth,imageheight;
	private int width,height;
	
	
	public CustomerImageSurfaceView(Context context) {  
		super(context);  

		this.Init();

	}  

	private void Init() {
		// TODO Auto-generated method stub

		holder = this.getHolder();//��ȡholder    
		holder.addCallback(this); 

		this.paint=new Paint();


	}
	@Override   
	public void startAnimation(Animation animation) {   
		super.startAnimation(animation);   
	}   
	
	
	@Override  
	public void surfaceCreated(SurfaceHolder holder) {    
		this.surfaceValid=true;
		this.width=this.getWidth();
		this.height=this.getHeight();

	}  

	@Override  
	public void surfaceChanged(SurfaceHolder holder, int format, int width,  
			int height) {  
	}  


	@Override  
	public void surfaceDestroyed(SurfaceHolder holder) {  
		/*if(this.bitmap!=null&&!this.bitmap.isRecycled()) 
			this.bitmap.recycle();
		this.bitmap=null;*/
		this.surfaceValid=false;
	}  





	public void setImageBitmap(Bitmap bitmap)
	{
		try
		{
			this.bitmap=bitmap;
			//this.bitmap=ImageHelper.bitmapZoomBySize(bitmap, this.width, this.height);
			this.imagewidth=bitmap.getWidth();
			this.imageheight=bitmap.getHeight();
			float scaleWidth = ((float)width) / imagewidth;   
			float scaleHeight = ((float) height) / imageheight;  
			this.matrix=new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);  
			if(this.surfaceValid)
			{
				Canvas canvas = holder.lockCanvas();//��ȡ����   
				if(canvas!=null)
				{
					if(this.bitmap!=null)
					{
						canvas.drawBitmap(bitmap, matrix, paint);
					}
				}

				holder.unlockCanvasAndPost(canvas);// �������ύ���õ�ͼ��    
			}
			if(this.bitmap!=null&&!this.bitmap.isRecycled()) 
				this.bitmap.recycle();
			this.bitmap=null;
			

		}

		catch(Exception ex)

		{
			LoggerHelper.WriteLogfortxt("CustomerImageView setImageBitmap==>"+ex.getMessage());
		}
	}


}  