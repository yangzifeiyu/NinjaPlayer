package com.mfusion.commons.tools;

import java.io.File;
import java.io.FileOutputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;

import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
/**
 * Created by guoyu on 7/14/2016.
 */
public class ImageHelper {

	public static Bitmap getBitmap(String imagePath, ResourceSourceType sourceType, AssetManager assetManager){
		try {
			if(sourceType==ResourceSourceType.http){
				
			}if(sourceType==ResourceSourceType.internal){
				return BitmapFactory.decodeStream(assetManager.open(imagePath));
			}
			
			return BitmapFactory.decodeFile(imagePath);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	public static Boolean saveBitmap(Bitmap bitmap,String savePath){
		try {
			if(bitmap==null)
				return true;
			
			File saveFile=new File(savePath);
			if(!saveFile.getParentFile().exists())
				saveFile.getParentFile().mkdirs();
			
			FileOutputStream out = new FileOutputStream(new File(savePath));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	public static Boolean createTemplateThumb(TemplateEntity templateInfo, String thumbPath) {
		try {
			
			File thumbFile=new File(thumbPath);
			FileOperator.createDir(thumbFile.getParent());

			Paint bodyPaint=new Paint();
			bodyPaint.setStrokeWidth(1);
			
			Integer border_width=4;
			Integer border_width_half=2;
			Paint borderPaint=new Paint();
			borderPaint.setStrokeWidth(border_width);
			borderPaint.setColor(Color.WHITE);
			borderPaint.setStyle(Style.STROKE);
			
			Bitmap bmp=Bitmap.createBitmap(templateInfo.width, templateInfo.height, Config.ARGB_4444); 
			Canvas canvas=new Canvas(bmp);
			
			bodyPaint.setColor(templateInfo.backColor);
			bodyPaint.setStyle(Style.FILL);
			canvas.drawRect(0, 0, templateInfo.width, templateInfo.height, bodyPaint);
			
			Bitmap compTypeImage=null;//BitmapFactory.decodeResource(MainActivity.APKResources, R.drawable.ic_launcher);
			for (ComponentEntity comp : templateInfo.compList) {
				bodyPaint.setColor(Color.GRAY);
				bodyPaint.setStyle(Style.FILL);
				canvas.translate(comp.left, comp.top);
				canvas.drawRect(0, 0, comp.width, comp.height, bodyPaint);
				canvas.drawRect(border_width_half, border_width_half, comp.width-border_width_half, comp.height-border_width_half, borderPaint);
				if(compTypeImage!=null){
					Bitmap resizedBitmap = resizeBitmap(compTypeImage, compTypeImage.getWidth(), compTypeImage.getHeight(),comp.width,comp.height);
					canvas.drawBitmap(resizedBitmap, (comp.width-resizedBitmap.getWidth())/2, (comp.height-resizedBitmap.getHeight())/2, null);
				}
				canvas.translate(-comp.left, -comp.top);
			}
			
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			//�洢·��
			try {
				//(new File(thumbPath)).createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(thumbFile);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	private static Bitmap resizeBitmap(Bitmap sourceBitmap,Integer width,Integer height,Integer comp_w,Integer comp_h){
		Integer baseSize=comp_w>=comp_h?comp_h:comp_w;
		if(width*5>baseSize)
			return sourceBitmap;
		
		float scale=(baseSize/5)/width;
		Matrix matrix=new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(sourceBitmap, 0, 0, width, height,matrix, true);
	}
}
