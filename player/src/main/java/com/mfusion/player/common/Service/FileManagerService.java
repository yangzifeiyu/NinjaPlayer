/**
 * 
i * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *File Manager Service
 */
package com.mfusion.player.common.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.mfusion.player.common.Enum.ConnectTargetType;
import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.DownloadObject;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Player.PlayerMenu;
import com.mfusion.player.common.Socket.ChangeMediaConnectState;
import com.mfusion.player.common.Socket.SocketHeartThread;
import com.mfusion.player.common.Socket.TcpSocket;
import com.mfusion.player.common.Storage.MediaStorage;
import com.mfusion.player.common.Task.DownloadFileTask;


public class FileManagerService implements BasicServiceInterface {
	private final int MaxNum=3;
	private List<DownloadFileTask> m_threadPool;
	//private List<DownloadObject> m_medias;
	private Queue<DownloadObject> m_medias;
	private List<String> m_pbu_id_list;
	private int m_token = 0;
	public boolean m_isRunning=false;
	public  boolean MediaConnectionState=false;
	public boolean DownloadState=false;
	public String downloadSpeedString="";
	public HashMap<String, DownloadObject> m_play_medias;
	private Thread DownLoadThread;

	private TcpSocket t_socket = null;
	private SocketHeartThread heartThread;//mediaserver����
	ReentrantLock downlodlock=new ReentrantLock();
	private Runnable runnable=new Runnable() {
		public void run() {
			while(m_isRunning)
			{
				try {

					downlodlock.lock();
					for(DownloadObject file:m_medias)
					{
						file.DownloadStatus=0;
						GetToken();
						m_threadPool.get(m_token).ThreadMethod(file);
						//m_token = (m_token + 1) % MaxNum;
					}
					for (int i = 0; i < MaxNum; i++)
					{
						m_threadPool.get(i).Start();
					}
					m_isRunning=false;
					downlodlock.unlock();
				} catch (Exception ex) {
					// TODO: handle exception
					LoggerHelper.WriteLogfortxt("FileManagerService run==>"+ex.getMessage());
				}
			}
		}

		
	};

	private void GetToken() {
		// TODO Auto-generated method stub
		double minsize=0;
		this.m_token=0;
		if(this.m_threadPool!=null&&this.m_threadPool.size()==MaxNum)
		{
			minsize=this.m_threadPool.get(0).NowSize;
			for(int i=1;i<MaxNum;i++)
			{
				if(this.m_threadPool.get(i).NowSize<minsize)
				{
					minsize=this.m_threadPool.get(i).NowSize;
					m_token=i;
				}
			}
		}
	}

	/*
	 * ���캯��
	 */
	public FileManagerService() {
		try
		{


			this.m_play_medias=new HashMap<String, DownloadObject>();
			this.m_medias= new LinkedBlockingQueue<DownloadObject>();

		}
		catch(Exception ex){
			LoggerHelper.WriteLogfortxt("FileManagerService==>"+ex.getMessage());
		}

	}

	/*
	 * ������mediaserver����
	 */
	private boolean IsTry=false;
	public void StartTryConnectMediaServer()
	{
		try
		{
			if(!IsTry)
			{
				
				IsTry=true;
				if(MainActivity.Instance.PlayerSetting.getConntectTarget()== ConnectTargetType.local)
					return;

				this.t_socket=new TcpSocket(null,MainActivity.Instance.PlayerSetting.MediaServerIP,
						Integer.parseInt(MainActivity.Instance.PlayerSetting.getMediaport()));
				this.heartThread=new SocketHeartThread(this.t_socket);
				Caller caller=new Caller();
				caller.setI(new ChangeMediaConnectState());
				this.heartThread.ChangeStateCaller=caller;//������mediaplayer����״̬
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileManagerService StartTryConnectMediaServer==>"+ex.getMessage());
		}
	}

	public void ChangeConnectionState(boolean state) {
		// TODO Auto-generated method stub
		
		if(MediaConnectionState!=state&&this.m_threadPool!=null&&this.m_threadPool.size()==MaxNum)
		{
			for(int i=0;i<MaxNum;i++)
			{
				this.m_threadPool.get(i).InitSocket(state);
			}
		}
		
		if(!MediaConnectionState&&state)
		{
			MediaConnectionState=true;
			Log.i("MediaHeart", "Reconnect to media server");
		}

		else if(MediaConnectionState&&!state)
		{
			MediaConnectionState=false;
			Log.i("MediaHeart", "Broken to media server");
		}

		
	}
	/*
	 * ��ʼ����
	 */
	public void Restart(List<String> pbu_id_list) {
		try
		{
			if(this.m_medias!=null)
				this.m_medias.clear();
			if (pbu_id_list == null)
				return;
			this.m_pbu_id_list = pbu_id_list;
			this.m_play_medias.clear();
			this.m_medias = MediaStorage.GetMediasAndUpdate(this.m_pbu_id_list,this.m_play_medias);
			PlayerMenu.isReloadMedias=true;
			this.Restart();
		}
		catch (Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileManagerService Restart==>"+ex.getMessage());
		}
	}
	@Override
	public void Restart() 
	{
		try 
		{
			m_token=0;
			this.DownloadState=false;
			if(this.m_threadPool!=null)
				this.m_threadPool.clear();
			this.m_threadPool=null;

			if (this.m_medias != null && this.m_medias.size() > 0)
			{

				LoggerHelper.WriteLogfortxt("FileManagerService Start==>Files Count:"+this.m_medias.size());
				this.ChangeState(true);
				this.m_isRunning=true;
				this.m_threadPool = new ArrayList<DownloadFileTask>();
				for (int i = 0; i < MaxNum; i++)
				{
					DownloadFileTask task=new DownloadFileTask();
					//task.Start();
					m_threadPool.add(task);
				}
				DownLoadThread= new Thread(runnable);
				DownLoadThread.start();
			}
			else
			{
				this.ChangeState(false);
				m_isRunning=false;
			}
		} 
		catch (Exception ex) 
		{
			LoggerHelper.WriteLogfortxt("FileManagerService Restart==>"+ex.getMessage());
		}
		finally
		{
			System.gc();
		}

	}
	// / <summary>
	// / ֹͣ�����߳�
	// / </summary>
	@Override
	public void Stop() {
		try
		{
			//LoggerHelper.WriteLogfortxt("FileManagerService Stop");
			downlodlock.lock();
			if(this.m_medias!=null)
				this.m_medias.clear();
			DownloadFileTask.requests.clear();
			this.m_isRunning=false;
			if(m_threadPool!=null&&m_threadPool.size()==MaxNum)
			{
				for (int i = 0; i < MaxNum; i++)
				{
					DownloadFileTask task=m_threadPool.get(i);
					task.Stop();
				}
			}

			downlodlock.unlock();

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileManagerService Stop==>"+ex.getMessage());
		}
	}

	public void ChangeState(boolean state) {
		// TODO Auto-generated method stub
		if(this.DownloadState!=state)
		{

			Message msg=new Message();
			msg.what=2;
			Bundle bundle=new Bundle();
			bundle.putBoolean("download",state);
			msg.setData(bundle);
			MainActivity.Instance.PBUDispatcher.mHandler.sendMessage(msg);
			this.DownloadState=state;
		}
	}
}
