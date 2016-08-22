package com.mfusion.player.common.Trigger;

import android.content.ComponentName;
import android.content.Intent;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.AppInfo;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Enum.TriggerExitType;
import com.mfusion.player.common.Enum.TriggerOpenType;
import com.mfusion.player.common.Enum.TriggerType;
import com.mfusion.player.common.Player.MainActivity;

public abstract class VirtualTrigger {
	public TriggerType triggerType=TriggerType.Mouse;
	public TriggerActionType actionType;
	public TriggerOpenType openType;
	public TriggerExitType exitType;  // ��Ҫ�����APP��
	public Object triggerAction; //1)action=App,triggerActionΪapp·��;2)action=PBU,triggerActionΪPBUEntity
	public int interval; // ���ʱ�䣬��MinuteΪ��λ



	protected String AppPath;
	protected int ProcessID;
	protected String AppArguments;

	abstract public void InitTrigger();
	abstract public void StartTrigger();
	abstract public void StopTrigger();
	abstract public void DisposeTrigger();



	protected void decodeTriggerAction()
	{
		if (this.actionType == TriggerActionType.App)
		{
			try
			{
				String app = this.triggerAction.toString();
				if (app.indexOf("\"") >= 0)
				{

					int split = app.indexOf("\"");
					if (split >= 0)
					{
						this.AppPath = app.substring(0, split);
						this.AppArguments = app.substring(split + 1);
						LoggerHelper.WriteLogfortxt("VirtualTrigger decodeTriggerAction ==> " + this.AppPath);
						return;
					}
				}
			}
			catch(Exception ex) {  LoggerHelper.WriteLogfortxt("VirtualTrigger decodeTriggerAction ==> " + ex.getMessage());}
			this.AppPath = this.triggerAction.toString(); 
		}
	}

	public boolean StartTriggerForApp() {
		try
		{
			if(MainActivity.Instance.appInfos.containsKey(this.AppPath))
			{
				AppInfo appinfo=MainActivity.Instance.appInfos.get(this.AppPath);
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);   
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(appinfo.getPkgName(), appinfo.getClassName());            
				intent.setComponent(cn);
				MainActivity.Instance.startActivity(intent);
			}
			return true;
		}catch(Exception ex)
		{
			return false;
		}
	}


	public boolean  StopTriggerForApp()
	{
/*
		ActivityManager mAm = (ActivityManager) MainActivity.Instance.getSystemService(Context.ACTIVITY_SERVICE);  
		int pid = android.os.Process.myPid();
		mAm.moveTaskToFront(pid,0);  */
		Intent resultIntent = new Intent(MainActivity.Instance, MainActivity.class);  
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);  
        MainActivity.Instance.startActivity(resultIntent);  
		/*String packagename=MainActivity.Instance.appInfos.get(this.AppPath).getPkgName();
		ActivityManager mAm = (ActivityManager) MainActivity.Instance.getSystemService(Context.ACTIVITY_SERVICE);
		mAm.killBackgroundProcesses(packagename);*/
		return true;


	}
	
	

	
}

