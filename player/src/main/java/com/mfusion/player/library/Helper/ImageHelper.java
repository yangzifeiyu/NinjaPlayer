package com.mfusion.player.library.Helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


public class ImageHelper {

	private static ImageCache cache=new ImageCache();
	public  static Bitmap readBitmapAutoSize(String filePath, int outWidth, int outHeight) {    
		//outWidth��outHeight��Ŀ��ͼƬ������Ⱥ͸߶ȣ���������  
		FileInputStream fs = null;  
		BufferedInputStream bs = null; 
		Bitmap bitmap=null;
		try
		{  
			fs = new FileInputStream(filePath);  
			bs = new BufferedInputStream(fs);  

			BitmapFactory.Options options = setBitmapOption(filePath, outWidth, outHeight);  
			bitmap= BitmapFactory.decodeStream(bs,null, options);  
		
			if(bitmap==null)
			{
				bitmap=BitmapFactory.decodeFile(filePath);
			}
		} 
		catch(OutOfMemoryError e)
		{

		}
		catch (Exception e) 
		{  
			e.printStackTrace();  
			LoggerHelper.WriteLogfortxt("ImageHelper readBitmapAutoSize==>"+e.getMessage());
		}
		finally 
		{  
			try {  
				bs.close();  
				fs.close();
				bs=null;
				fs=null;
			} catch (Exception e) {  
				e.printStackTrace();  
			}  
			System.gc();

		}  

		return bitmap;

	}
	public static byte[] getBytes(InputStream is) throws IOException {  
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
		byte[] buffer = new byte[1024]; // �����װ  
		int len = -1;  
		while ((len = is.read(buffer)) != -1) {  
			outstream.write(buffer, 0, len);  
		}  
		outstream.close();  
		// �ر���һ��Ҫ�ǵá�  
		return outstream.toByteArray();  
	}  
	
	
	private  static BitmapFactory.Options setBitmapOption(String file, int width, int height)
	{  
		BitmapFactory.Options opt = new BitmapFactory.Options();  
		try
		{
			opt.inJustDecodeBounds = true;            
			//����ֻ�ǽ���ͼƬ�ı߾࣬�˲���Ŀ���Ƕ���ͼƬ��ʵ�ʿ�Ⱥ͸߶�  
			BitmapFactory.decodeFile(file, opt);  
			int outWidth = opt.outWidth; //���ͼƬ��ʵ�ʸߺͿ�  
			int outHeight = opt.outHeight;  
			opt.inDither = false;  
			opt.inPreferredConfig = Bitmap.Config.RGB_565;      
			//���ü���ͼƬ����ɫ��Ϊ16bit��Ĭ����RGB_8888����ʾ24bit��ɫ��͸��ͨ������һ���ò���  
			opt.inSampleSize =1;                            
			//�������ű�,1��ʾԭ����2��ʾԭ�����ķ�֮һ....  
			//�������ű�  
			if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {  
				int sampleSize = (outWidth / width + outHeight / height) / 2;  
				opt.inSampleSize = sampleSize;  
			}  

			opt.inJustDecodeBounds = false;//���ѱ�־��ԭ  
			if (ImageCache.hasHoneycomb()) {  
				opt.inMutable = true;  

				if (cache != null) {  
					Bitmap inBitmap = cache.getBitmapFromReusableSet(opt);  

					if (inBitmap != null) {  
						opt.inBitmap = inBitmap;  
					}  
				}  
			}  
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ImageHelper setBitmapOption==>"+ex.getMessage());
		}
		return opt;  
	}  

	public static Bitmap bitmapZoomBySize(Bitmap srcBitmap,int newWidth,int newHeight)
	{
		int srcWidth = srcBitmap.getWidth();   
		int srcHeight = srcBitmap.getHeight();    

		float scaleWidth = ((float) newWidth) / srcWidth;   
		float scaleHeight = ((float) newHeight) / srcHeight;   

		return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}

	public static Bitmap bitmapZoomByScale(Bitmap srcBitmap,float scaleWidth,float scaleHeight)
	{
		int srcWidth = srcBitmap.getWidth();   
		int srcHeight = srcBitmap.getHeight();  
		Matrix matrix = new Matrix();   
		matrix.postScale(scaleWidth, scaleHeight);     
		Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,   
				srcHeight, matrix, true);

		return resizedBitmap;

	}

	public static Bitmap bitmapZoomBySize(String filePath,int newWidth,int newHeight)
	{
		FileInputStream fs = null;  
		BufferedInputStream bs = null; 
		Bitmap bitmap=null;
		try
		{  
			fs = new FileInputStream(filePath);  
			bs = new BufferedInputStream(fs);  

			BitmapFactory.Options options = setBitmapOption(filePath, newWidth, newWidth);  
			bitmap= BitmapFactory.decodeStream(bs,null, options);  
		
			if(bitmap==null)
			{
				bitmap=BitmapFactory.decodeFile(filePath);
			}
			
			return bitmapZoomBySize(bitmap,newWidth,newHeight);
		} 
		catch (Exception e) 
		{  
			e.printStackTrace();  
			LoggerHelper.WriteLogfortxt("ImageHelper bitmapZoomBySize==>"+e.getMessage());
		}
		finally 
		{  
			try {  
				bs.close();  
				fs.close();
				bs=null;
				fs=null;
			} catch (Exception e) {  
				e.printStackTrace();  
			}  
			System.gc();

		}  

		return null;

	}

	public static Bitmap createImageThumbnail(String filePath){
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, 1280*768);
		opts.inJustDecodeBounds = false;

		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}



}
