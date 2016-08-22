/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-29
 *
 *Webcontrol
 *
 */
package com.mfusion.player.common.Entity.Control;

import com.mfusion.player.library.Helper.LoggerHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebControl extends AControl{
	public WebControl(Context context)
	{
		this.CreateControl(context);
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
		this.Element=null;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void CreateControl(Context context) {

		// TODO Auto-generated method stub
		try
		{
			WebView WebControl=new WebView(context);
			WebControl.setWebViewClient(new WebViewClient(){       
				public boolean shouldOverrideUrlLoading(WebView view, String url) {       
					view.loadUrl(url);       
					return true;       
				}       
			});   
			WebSettings webSettings = WebControl.getSettings();       
			webSettings.setJavaScriptEnabled(true); 
			WebControl.setVisibility(View.INVISIBLE);
			this.Element=WebControl;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("WebControl CreateControl==>"+ex.getMessage());
		}
	}

	public void LoadUrl(String path)
	{
		try
		{
			((WebView)this.Element).loadUrl(path); 
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("WebControl LoadUrl==>"+ex.getMessage());
		}
	}
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}
	public void Stop() {
		// TODO Auto-generated method stub
		try
		{
			
			((WebView)this.Element).clearCache(true);
			((WebView)this.Element).clearHistory();
			((WebView)this.Element).removeAllViews();
			//((WebView)this.Element).destroy();

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("WebControl Stop==>"+ex.getMessage());
		}
	}

}
