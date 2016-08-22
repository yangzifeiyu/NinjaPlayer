package com.mfusion.player.library.Helper;

public class UnitHelper {
	public static int dip2px(float scale, float dpValue){
		//final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dpValue*scale + 0.5f);
	}

	public static int px2dip(float scale, float pxValue){
		// final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue/scale + 0.5f);
	}
}
