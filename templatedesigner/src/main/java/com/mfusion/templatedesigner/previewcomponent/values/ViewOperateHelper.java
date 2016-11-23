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
			for (int i = 1; i < container.getChildCount(); i++) {
				view = container.getChildAt(i);
				if (selectedView == view) {
					selectedIndex = i;
					break;
				}
			}

			RelativeLayout.LayoutParams layout=null;
			for (int i = moveUp?selectedIndex+1:1; i < (moveUp?container.getChildCount():selectedIndex); i++) {
				view=container.getChildAt(i);

				layout=(RelativeLayout.LayoutParams)view.getLayoutParams();

				System.out.println("selected Left :"+selectedLayout.leftMargin+" selected Width :"+selectedLayout.width+" layout Left :"+layout.leftMargin+" layout Width :"+layout.width);
				if(selectedLayout.leftMargin>=(layout.leftMargin+layout.width)||(selectedLayout.leftMargin+selectedLayout.width<=layout.leftMargin)){
					//not override in x
					continue;
				}
				System.out.println("selected top :"+selectedLayout.topMargin+" selected height :"+selectedLayout.height+" layout top :"+layout.topMargin+" layout height :"+layout.height);
				if(selectedLayout.topMargin>=(layout.topMargin+layout.height)||(selectedLayout.topMargin+selectedLayout.height<=layout.topMargin)){
					//not override in y
					continue;
				}

				overrideIndex=i;
				if(moveUp)
					break;
			}

			System.out.println("selectedIndex:"+selectedIndex+" overrideIndex:"+overrideIndex);

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
