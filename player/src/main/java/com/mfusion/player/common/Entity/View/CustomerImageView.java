package com.mfusion.player.common.Entity.View;

import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;

public class CustomerImageView extends ImageView{

	private Bitmap bitmap;
	private Paint paint;
	private Matrix matrix;
	private int imagewidth,imageheight;

	public CustomerImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.paint=new Paint();

	}

	@Override 
	protected void onDraw(Canvas canvas) {  

		if(canvas!=null)
		{
			if(this.bitmap!=null)
			{
				paint.reset(); 

				canvas.drawBitmap(bitmap, matrix, paint);

				if(!this.bitmap.isRecycled()) 
					this.bitmap.recycle();
				this.bitmap=null;

			}

		}
		
		//super.onDraw(canvas);  
	}  

	@Override
	public void setImageBitmap(Bitmap bitmap)
	{
		try
		{
			super.setImageBitmap(bitmap);
			this.bitmap=bitmap;
			this.imagewidth=bitmap.getWidth();
			this.imageheight=bitmap.getHeight();
			float scaleWidth = ((float)this.getLayoutParams().width) / imagewidth;   
			float scaleHeight = ((float) this.getLayoutParams().height) / imageheight;  
			this.matrix=new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);  
			invalidate();  
		}
		catch(Exception ex)
		{

		}


	}

}
