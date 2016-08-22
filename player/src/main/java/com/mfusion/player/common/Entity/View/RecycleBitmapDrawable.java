package com.mfusion.player.common.Entity.View;



import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class RecycleBitmapDrawable extends BitmapDrawable {
	private int mCacheRefCount = 0;//�������ü�����
    private int mDisplayRefCount = 0;//��ʾ���ü�����
    private boolean mHasBeenDisplayed=false;

    public RecycleBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }
	
    
    
 
    // ��ǰBitmap�Ƿ���ʾ��UI������
    public void setIsDisplayed(boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                mDisplayRefCount++;
                mHasBeenDisplayed = true;
            } else {
                mDisplayRefCount--;
            }
        }

        checkState();
    }

    //����Ƿ񱻻���
    public void setIsCached(boolean isCached) {
        synchronized (this) {
            if (isCached) {
                mCacheRefCount++;
            } else {
                mCacheRefCount--;
            }
        }

        checkState();
    }

    //���ڼ��Bitmap�Ƿ��Ѿ�������
    private synchronized void checkState() {
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()) {
            getBitmap().recycle();
        }
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }
	
	


}