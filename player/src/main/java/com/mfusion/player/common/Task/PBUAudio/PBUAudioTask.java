package com.mfusion.player.common.Task.PBUAudio;

import java.util.Hashtable;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Task.BackGroundTaskEntity;


public class PBUAudioTask extends Thread{
	//������Ⱦ����ͼƬ
		public PBUAudioTask(Hashtable<String, Object> paras){
			try
			{
				BackGroundTaskEntity task = new BackGroundTaskEntity();
				Caller condition=new Caller();
				condition.setI(new PBUAudioCallerCondition());
				task.condition =condition;

				
				Caller result=new Caller();
				result.setI(new PBUAudioCallerResult());
				task.result =result;
				task.Paras =paras;
				MainActivity.Instance.TaskManager.AddTask(task);
			}
			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PBUAudioTask==>"+ex.getMessage());
			}
		}
}
