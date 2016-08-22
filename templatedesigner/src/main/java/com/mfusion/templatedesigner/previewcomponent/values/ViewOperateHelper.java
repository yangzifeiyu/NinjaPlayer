package com.mfusion.templatedesigner.previewcomponent.values;

import org.w3c.dom.Element;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ViewOperateHelper {

	public static Boolean changeViewZindex(ViewGroup container,View selectedView,Boolean moveUp) {
		try {
			
			int selectedIndex=-1;
			int overrideIndex=-1;
			RelativeLayout.LayoutParams selectedLayout=(RelativeLayout.LayoutParams)selectedView.getLayoutParams();
			
			View view =null;
			RelativeLayout.LayoutParams layout=null;
			for (int i = 1; i < container.getChildCount(); i++) {
				view=container.getChildAt(i);
				if(selectedView==view){
					
					selectedIndex=i;
					
					if(moveUp==false)
						break;
					else
						continue;
				}
				
				if(moveUp==false||(moveUp&&selectedIndex>=0)){

					layout=(RelativeLayout.LayoutParams)view.getLayoutParams();
					
					if(selectedLayout.leftMargin>=(layout.leftMargin+layout.width)||(selectedLayout.leftMargin+layout.width<=layout.leftMargin)){
						//not override in x
						continue;
					}
					if(selectedLayout.topMargin>=(layout.topMargin+layout.height)||(selectedLayout.topMargin+layout.height<=layout.topMargin)){
						//not override in y
						continue;
					}
					
					overrideIndex=i;
					if(moveUp)
						break;
				}
			}
			
			if(selectedIndex<0||overrideIndex<0)
				return false;
			
			container.removeView(selectedView);
			container.addView(selectedView, overrideIndex);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
}
