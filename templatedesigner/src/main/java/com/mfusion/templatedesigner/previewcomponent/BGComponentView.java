package com.mfusion.templatedesigner.previewcomponent;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.CallbackBundle;

public class BGComponentView extends BasicComponentView {
	
	private Bitmap c_bg_image;
	
	private String c_bg_image_path;

	public BGComponentView(Context context,int width,int height, int backColor, Bitmap image) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_back_color=backColor;
		this.c_bg_image=image;
		this.c_w=width;
		this.c_h=height;
		
		this.c_type= ComponentType.Unkown;
		this.c_name="Background";

		this.setAlpha(1f);
	}
	
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			if(c_bg_image==null)
				canvas.drawColor(c_back_color);
			else {
				canvas.drawBitmap(c_bg_image, null, new Rect(0, 0, getLayoutParams().width, getLayoutParams().height), null);
			}
		}
    }
	
	@Override
	public ComponentEntity getComponentProperty(Document doc, TemplateEntity templateEntity) throws Exception  {
		templateEntity.width=c_w;
		templateEntity.height=c_h;
		templateEntity.backColor=this.c_back_color;
		templateEntity.backImagePath=c_bg_image_path;
		templateEntity.backImageBitmap=this.c_bg_image;
		return null;
	}

	@Override
	protected void updateViewLayout(Integer width,Integer height,Integer left,Integer top){
		super.updateViewLayout(width,height,left,top);

		if(TemplateSizeChangedCallback!=null){
			Bundle bundle=new Bundle();
			bundle.putInt("w",width);
			bundle.putInt("h",height);
			TemplateSizeChangedCallback.callback(bundle);
		}
	}

	@Override
	public Boolean canDeleted(){
		return false;
	}

	public CallbackBundle TemplateSizeChangedCallback=null;

	public CallbackBundle ImageChangedCallback=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			if(bundle==null)
				return;
				
            ArrayList<String> medias=bundle.getStringArrayList("selectedFiles");
            if(medias!=null&&medias.size()>0){
				componentPropertyChanged();

            	c_bg_image_path=medias.get(0);
				ImageHelper.recycleBitmap(c_bg_image);
            	c_bg_image= ImageHelper.getBitmap(c_bg_image_path);
            	invalidate();
            }
		}
	};

	@Override
	public void stop(){
		ImageHelper.recycleBitmap(c_bg_image);
	}
}
