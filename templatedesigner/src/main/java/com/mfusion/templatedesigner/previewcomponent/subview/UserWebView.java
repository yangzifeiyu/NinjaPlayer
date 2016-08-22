package com.mfusion.templatedesigner.previewcomponent.subview;

import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

public class UserWebView extends WebView {

	public UserWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    boolean ret = super.onTouchEvent(ev);
	    /*if (mPreventParentTouch) {
	        switch (ev.getAction()) {
	            case MotionEvent.ACTION_MOVE:
	                requestDisallowInterceptTouchEvent(true);
	                ret = true;
	                break;
	            case MotionEvent.ACTION_UP:
	            case MotionEvent.ACTION_CANCEL:
	                requestDisallowInterceptTouchEvent(false);
	                mPreventParentTouch = false;
	                break;
	        }
	    }*/
	    return false;
	}

}
