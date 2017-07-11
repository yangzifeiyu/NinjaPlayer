package com.mfusion.player.common.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


import com.mfusion.player.common.Task.BackGroundTaskEntity;

import com.mfusion.player.library.Helper.LoggerHelper;

public class TaskManagerService implements BasicServiceInterface{

	private boolean Running=false;
	private Queue<BackGroundTaskEntity> TaskQueue;
	private Thread taskThread = new Thread(new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(Running)
			{
				try
				{
					if (TaskQueue.size()!= 0)
					{
						//����Task����
						BackGroundTaskEntity entity = TaskQueue.poll();
						if (entity.TaskExecute() == false)
							TaskQueue.add(entity);
					}
					Thread.sleep(20000);
				}
				catch (Exception ex) {
					LoggerHelper.WriteLogfortxt("TaskManager AddTask==>"+ex.getMessage());
				}

			}

		}});

	/*
	 * ���캯��
	 */
	public  TaskManagerService()
	{
		this.TaskQueue = new LinkedBlockingQueue<BackGroundTaskEntity>();

	}
	/*
	 * �������
	 */
	public void AddTask(BackGroundTaskEntity task)
	{

		this.TaskQueue.add(task);


	}
	@Override
	public void Restart() {
		// TODO Auto-generated method stub
		try
		{
			this.Running=true;
			this.taskThread.start();
		}catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TaskManagerService Restart==>"+ex.getMessage());
		}
	}
	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		Running=false;
		try{

			this.TaskQueue.clear();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}



}
