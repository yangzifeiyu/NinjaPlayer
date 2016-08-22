package com.mfusion.player.library.Helper;



import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mfusion.player.common.Entity.View.RecycleBitmapDrawable;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {
	//private final static int MAX_MEMORY = 4 * 102 * 1024;
	private LruCache<String, BitmapDrawable> mMemoryCache;//�������ͼƬ�ﵽ��Ԥ���趨��ֵ��ʱ����ô����ʹ�ô������ٵ�ͼƬ�ͻᱻ���յ�

	private Set<SoftReference<Bitmap>> mReusableBitmaps;//�����ã�ֻҪ���㹻�ռ䣬��һֱ���ֶ���

	public ImageCache()
	{
		this.init();
	}
	private void init() {
		if (hasHoneycomb())
		{
			mReusableBitmaps = Collections
					.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
		}
		int MAXMEMONRY = (int) (Runtime.getRuntime() .maxMemory() / 1024);
		mMemoryCache = new LruCache<String, BitmapDrawable>(MAXMEMONRY) {


			@Override
			protected int sizeOf(String key, BitmapDrawable bitmapDrawable) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
					return bitmapDrawable.getBitmap().getByteCount() ;
				} else {
					return bitmapDrawable.getBitmap().getRowBytes() * bitmapDrawable.getBitmap().getHeight();
				}
			}
			
			
			@Override
			protected void entryRemoved(boolean evicted, String key,
					BitmapDrawable oldValue, BitmapDrawable newValue) {

				if (RecycleBitmapDrawable.class.isInstance(oldValue)) {

					((RecycleBitmapDrawable) oldValue).setIsCached(false);
				} 
				else
				{
					if (hasHoneycomb()) {
						Log.i("ImageCache", "hard cache is full , push to soft cache");
						mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue
								.getBitmap()));
					}
				}
			}

		};
	}
	
	
	protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
		Bitmap bitmap = null;

		if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) 
		{
			synchronized (mReusableBitmaps) {
				final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps
						.iterator();
				Bitmap item;
				while (iterator.hasNext()) {
					item = iterator.next().get();

					if (null != item && item.isMutable()) {
						if (canUseForInBitmap(item, options)) {
							bitmap = item;
							iterator.remove();
							break;
						}
					} else {
						iterator.remove();
					}
				}
			}
		}
		return bitmap;
	}

	/**
	 * �жϸ�Bitmap�Ƿ�������õ�BitmapFactory.Options.inBitmap��
	 * 
	 * @param candidate
	 * @param targetOptions
	 * @return
	 */
	@TargetApi(VERSION_CODES.KITKAT)
	public static boolean canUseForInBitmap(Bitmap candidate,
			BitmapFactory.Options targetOptions) {

		// ��Anroid4.4�Ժ����Ҫʹ��inBitmap�Ļ���ֻ��Ҫ�����Bitmap��inBitmap���õ�С�����ˣ���inSampleSize
		// û������
		if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			int width = targetOptions.outWidth / targetOptions.inSampleSize;
			int height = targetOptions.outHeight / targetOptions.inSampleSize;
			int byteCount = width * height
					* getBytesPerPixel(candidate.getConfig());
			return byteCount <= candidate.getAllocationByteCount();
		}

		// ��Android
		// 4.4֮ǰ�������ʹ��inBitmap�Ļ��������Bitmap�����inBitmap���õĿ����ȣ���inSampleSizeΪ1
		return candidate.getWidth() == targetOptions.outWidth
				&& candidate.getHeight() == targetOptions.outHeight
				&& targetOptions.inSampleSize == 1;
	}

	/**
	 * ��ȡÿ��������ռ�õ�Byte��
	 * 
	 * @param config
	 * @return
	 */
	public static int getBytesPerPixel(Config config) {
		if (config == Config.ARGB_8888) {
			return 4;
		} else if (config == Config.RGB_565) {
			return 2;
		} else if (config == Config.ARGB_4444) {
			return 2;
		} else if (config == Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	@TargetApi(VERSION_CODES.HONEYCOMB)
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

}
