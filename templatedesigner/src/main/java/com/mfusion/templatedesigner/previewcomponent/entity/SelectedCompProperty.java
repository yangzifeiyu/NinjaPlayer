package com.mfusion.templatedesigner.previewcomponent.entity;

import android.view.View;

import com.mfusion.templatedesigner.previewcomponent.values.CompOperateType;

public class SelectedCompProperty {

	public View selectedView;
	
	public CompOperateType operateType=CompOperateType.none;
	
	public float left;
	
	public float top;
	
	public int width;
	
	public int height;
	
	public float mouseX;
	
	public float mouseY;
}
