/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *��������ֹserver�Ͽ�
 */
package com.mfusion.player.common.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.util.Log;

public class SocketHeartThread implements Runnable {
	private static final boolean CONNECT = true;
	//private static final int MaxTry=3;
	private static final int WAIT_TIME = 2000;
	private TcpSocket t_socket = null;
	
	
	
	public Caller  ChangeStateCaller;    //�ⲿ���ûص�����
	//private int DisconnectCount=0;
	
	private static final String TAG = "Heart";
	/*
	 * ���캯��
	 */
	public SocketHeartThread(TcpSocket socket) {
		this.t_socket = socket;
		initiate();
	}

	public void run() {
		// TODO Auto-generated method stub
		reunion();
	}

	ReentrantLock lock=new ReentrantLock();
	// run����


	public boolean isIpReachable(String ip) 
	{      
		try    
		{          

			Process p = Runtime.getRuntime().exec("ping -c 3 " + ip);//m_strForNetAddress���������ַ����Ip��ַ
			int status;
			status = p.waitFor();

			if (status == 0) {  
				return true;
			}    
			else 
			{ 
				return false;
			} 
		}      
		catch (InterruptedException e)       
		{      
			return false;
		}      
		catch (IOException e)    
		{   
			return false;
		}  
	}
	private void reunion() {
		try
		{
			while (CONNECT) 
			{

				try
				{

					if(t_socket.Socket==null||t_socket.Socket.isClosed()||t_socket.Writeout==null)
					{
						Socket socket=new Socket();
						InetSocketAddress address=new InetSocketAddress(t_socket.SERVER_IP,t_socket.Server_PORT);
						socket.connect(address, 5000);

						t_socket.Socket=socket;
						t_socket.Socket.setTcpNoDelay(true);
						t_socket.Socket.setOOBInline(true);
						t_socket.Socket.setKeepAlive(true);
						t_socket.In = new BufferedReader(new InputStreamReader(
								t_socket.Socket.getInputStream()));
						t_socket.Out = new BufferedWriter(
								new OutputStreamWriter(
										t_socket.Socket.getOutputStream()));
						t_socket.Writeout = new PrintWriter(t_socket.Out );
						Log.i(TAG,"New Socket");
						//LoggerHelper.WriteLogfortxt("New Socket");
						if(ChangeStateCaller!=null)
						{
							//DisconnectCount=0;
							ChangeStateCaller.call(true);
						}

					}

				}

				catch(Exception ex)
				{
					ex.printStackTrace();
					/*if(ChangeStateCaller!=null)
						DisconnectCount++;

					if(DisconnectCount>=MaxTry)
					{
						DisconnectCount=0;
						ChangeStateCaller.call(false);
					}*/
					ChangeStateCaller.call(false);
				}
				finally
				{
					System.gc();
					holdTime(WAIT_TIME);
				}



			}
		}
		catch(Exception ex)
		{

		}
	}
	// �ȴ�ʱ��
	private void holdTime(int time) {
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e1)
		{

		}
		finally
		{
			sendTestData();
		}
	}

	// ���Ͳ������
	private void sendTestData() {
		Log.i("Heart", "sendTestData");
		try
		{

			if(this.isIpReachable(t_socket.SERVER_IP)&&t_socket.Socket!=null&&!t_socket.Socket.isClosed())
			{

				t_socket.Out.write("heart test");
				t_socket.Out.flush();
				if(ChangeStateCaller!=null)
				{
					//this.DisconnectCount=0;
					ChangeStateCaller.call(true);
				}
			}
			else
			{
				/*if(ChangeStateCaller!=null)
					DisconnectCount++;

				if(DisconnectCount>=MaxTry)
				{
					DisconnectCount=0;
					if(ChangeStateCaller!=null)
					ChangeStateCaller.call(false);
				}*/
				if(ChangeStateCaller!=null)
					ChangeStateCaller.call(false);
				CloseSocket();
			}


		}
		catch (Exception e) 
		{
			/*if(ChangeStateCaller!=null)
				DisconnectCount++;

			if(DisconnectCount>=MaxTry)
			{
				DisconnectCount=0;
				ChangeStateCaller.call(false);
			}*/
			if(ChangeStateCaller!=null)
				ChangeStateCaller.call(false);
			CloseSocket();
		}

	}


	// �쳣����

	public void CloseSocket() 
	{

		lock.lock();
		/*Log.i(TAG,"Close Socket");
		LoggerHelper.WriteLogfortxt("Close Socket");*/
		try 
		{
			try
			{
				if(this.t_socket.Socket!=null)
				{
					this.t_socket.Socket.shutdownInput();
					this.t_socket.Socket.shutdownOutput();
				}
			}
			catch(Exception ex)
			{

			}
			try
			{
				if(this.t_socket.In!=null)
					this.t_socket.In.close();
			}
			catch(Exception ex){}
			try
			{
				if(this.t_socket.Writeout!=null)
					this.t_socket.Writeout.close();
			}
			catch(Exception ex){}
			try
			{
				if(this.t_socket.Out!=null)
					this.t_socket.Out.close();
			}
			catch(Exception ex){}
			try
			{
				if(t_socket.Socket!=null)
					t_socket.Socket.close();
			}
			catch(Exception ex){}

			t_socket.Socket=null;
			t_socket.Writeout=null;
			t_socket.Out=null;

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
			System.gc();
		}
	}
	// ���������߳�
	private void initiate() {
		try
		{
			Thread isAgainThread = new Thread(SocketHeartThread.this);
			isAgainThread.start();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("SocketHeartThread initiate==>"+ex.getMessage());
		}

	}
}
