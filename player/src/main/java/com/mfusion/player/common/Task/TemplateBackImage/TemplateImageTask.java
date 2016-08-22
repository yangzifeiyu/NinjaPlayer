package com.mfusion.player.common.Task.TemplateBackImage;

import java.util.Hashtable;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Task.BackGroundTaskEntity;

public class TemplateImageTask extends Thread {

	//������Ⱦ����ͼƬ
	public TemplateImageTask(Hashtable<String, Object> paras){
		try
		{
			BackGroundTaskEntity task = new BackGroundTaskEntity();
			Caller condition=new Caller();
			condition.setI(new TempBackImageCallerCondition());
			task.condition =condition;

			
			Caller result=new Caller();
			result.setI(new TempBackImageCallerResult());
			task.result =result;
			task.Paras =paras;
			MainActivity.Instance.TaskManager.AddTask(task);
		}catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TemplateImageTask==>"+ex.getMessage());
		}
	}


}
