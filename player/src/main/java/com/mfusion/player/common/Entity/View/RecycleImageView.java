package com.mfusion.player.common.Entity.View;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RecycleImageView extends ImageView {
	public RecycleImageView(Context context) {
		super(context);
	}

	public RecycleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecycleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		Drawable previousDrawable = getDrawable();
		super.setImageDrawable(drawable);
		
		//��ʾ�µ�drawable
		notifyDrawable(drawable, true);

		//����֮ǰ��ͼƬ
		notifyDrawable(previousDrawable, false);
	}

	@Override
	protected void onDetachedFromWindow() {
		//��View�Ӵ��������ʱ��,���drawable
		setImageDrawable(null);

		super.onDetachedFromWindow();
	}

	/**
	 * ֪ͨ��drawable��ʾ��������
	 * 
	 * @param drawable
	 * @param isDisplayed
	 */
	public static void notifyDrawable(Drawable drawable, boolean isDisplayed) {
		if (drawable instanceof RecycleBitmapDrawable) {
			((RecycleBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
		} else if (drawable instanceof LayerDrawable) {
			LayerDrawable layerDrawable = (LayerDrawable) drawable;
			for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
				notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
			}
		}
	}

}
