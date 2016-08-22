package com.mfusion.player.common.Helper.RSS;

import android.graphics.Paint;

public class RSSUnit {
	public String text;
	public Paint paint;
	public float width;
	public float baseline;
	public RSSUnit(String text,Paint paint,float width,float baseline)
	{
		this.text=text;
		this.paint=paint;
		this.width=width;
		this.baseline=baseline;
	}
}
