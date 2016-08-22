package com.mfusion.player.common.Entity;

import android.content.Intent;


public class AppInfo {


	private String appLabel;    //Ӧ�ó����ǩ  
	private Intent intent ;     //����Ӧ�ó����Intent ��һ����ActionΪMain��CategoryΪLancher��Activity  
	private String pkgName ;    //Ӧ�ó������Ӧ�İ���  
	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public AppInfo(){}  

	public String getAppLabel() {  
		return appLabel;  
	}  
	public void setAppLabel(String appName) {  
		this.appLabel = appName;  
	}  
	public Intent getIntent() {  
		return intent;  
	}  
	public void setIntent(Intent intent) {  
		this.intent = intent;  
	}  
	public String getPkgName(){  
		return pkgName ;  
	}  
	public void setPkgName(String pkgName){  
		this.pkgName=pkgName ;  
	}  

}
