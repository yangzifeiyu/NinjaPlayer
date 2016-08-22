/**
 * i * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *Socket Communicate service
 */
package com.mfusion.player.common.Service;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Socket.ChangeMFConnectState;
import com.mfusion.player.common.Socket.SocketHeartThread;
import com.mfusion.player.common.Socket.TCPMessage;
import com.mfusion.player.common.Socket.TcpSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;


import org.w3c.dom.Element;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;


public class SocketMessageService  implements BasicServiceInterface{

	public ServerConnectStatus MFConnectionState=ServerConnectStatus.Unconnection;         //MFConnect State 
	public boolean ConnectionState=false;         //MFConnect State 
	private static final int Timeout=5;//���ճ�ʱ
	private static final String TAG = "TCPClient";
	private boolean RUNNING=false;

	private TcpSocket t_socket = null;//heart
	private static final int m_interval = 2000;
	private SocketHeartThread m_heartThread;        //����server state
	private TCPMessage tcpXML;
	public Caller firstTryConnect;
	private boolean m_IsfirstConnect=true;
	private int m_try=0;
	private static final int MaxTry=3;
	private boolean m_trying=true;

	//����ͨ��
	private Socket socket = null;
	private BufferedReader In = null; // �������
	private BufferedWriter Out = null;
	private PrintWriter Writeout = null;

	/*
	 * ��serverͨ���߳�
	 */
	private Thread m_communicatethread= new Thread(new Runnable() {

		public void run() {
			// TODO Auto-generated method stub

			while (RUNNING) {
				try
				{
					if (ConnectionState) {
						boolean flag=InitSocket();
						if(flag)
						{
							boolean result=SendMessage();
							if(result)
							{
								receive=true;
								first=true;
								length=-1;
								leftLength=0;
								info.delete(0, info.length());
								info.setLength(0);
								ReceiveMessage();
							}
						}

					}
					try {
						
						Thread.sleep(m_interval);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch(Exception ex)
				{

					ex.printStackTrace();
				}

			}
		}
	});

	/*
	 * ���캯��
	 */
	public SocketMessageService() {
		try
		{
			this.tcpXML = new TCPMessage();
			//LoggerHelper.WriteLogfortxt("SocketService ip:"+MainActivity.Instance.PlayerSetting.MFServerIp+";port:"+MainActivity.Instance.PlayerSetting.MFServerPort);
			this.t_socket=new TcpSocket(null,MainActivity.Instance.PlayerSetting.MFServerIp,Integer.parseInt(MainActivity.Instance.PlayerSetting.MFServerPort));
			//��������
			this.m_heartThread=new SocketHeartThread(this.t_socket);
			Caller caller=new Caller();
			caller.setI(new ChangeMFConnectState());
			this.m_heartThread.ChangeStateCaller=caller;

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("SocketMessageService==>"+ex.getMessage());
		}
	}



	/*
	 * �ı�server connect state
	 */
	public void ChangeConnectionState(boolean connection)
	{
		try
		{
			if(!connection)
			{
				this.CloseSocket();
				this.MFConnectionState=MFConnectionState.Unconnection;
				Message msg=new Message();
				msg.what=1;
				Bundle bundle=new Bundle();
				bundle.putString("connection",ServerConnectStatus.Unconnection.toString());
				msg.setData(bundle);
				MainActivity.Instance.PBUDispatcher.mHandler.sendMessage(msg);

			}
			else
			{
				this.InitSocket();
			}

			this.ConnectionState=connection;
			/*if(!ConnectionState&&connection)
			{

				Log.i("Heart", "Reconnect to server");

				this.ConnectionState=true;
			}

			else if(ConnectionState&&!connection)
			{

				Log.i("Heart", "Broken to server");
				this.ConnectionState=false;

			}*/

			if(m_trying)
			{
				if(!ConnectionState)
				{
					if(this.m_try<MaxTry)
					{
						LoggerHelper.WriteLogfortxt("Connect trying==>"+m_try);
						this.m_try++;

					}
					else
					{
						m_trying=false;
						if(this.firstTryConnect!=null)
						{
							Object[] args={ServerConnectStatus.Unconnection,null,null};
							this.firstTryConnect.call(args);
							LoggerHelper.WriteLogfortxt("Server can not connect,call render template");
						}

					}
				}
				else
					m_trying=false;

			}


			if(!connection)
				receive=false;
		}

		catch(Exception ex)
		{
			ex.printStackTrace();
			///LoggerHelper.WriteLogfortxt("SocketMessageService ChangeConnectionState==>"+ex.getMessage());
		}
	}
	/*
	 * ��server ������Ϣ
	 */
	private boolean SendMessage() {

		try {
			Log.i(TAG,"SocketHeartThread begin send");
			//LoggerHelper.WriteLogfortxt("SendMessage");
			String MacAddr = MainActivity.Instance.PlayerSetting.getLicense();
			String Updattime = MainActivity.Instance.PlayerSetting.getUpdateTime();
			String message = tcpXML.WriteMacAddrAndScheUpdate(MacAddr, Updattime);
			if(socket!=null&&!socket.isClosed()&&Writeout!=null)
			{

				Writeout.print(message);
				Writeout.flush();
				Log.i(TAG, "Send:"+message);
				//LoggerHelper.WriteLogfortxt("SendMessage successfully");
				return true;
			}
			else
			{
				this.CloseSocket();
				return false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//LoggerHelper.WriteLogfortxt("SocketMessageService SendMessage==>"+e.getMessage());
			return false;
		}
		
	}
	/*
	 * ���մ�server�˻ص���Ϣ
	 */

	private boolean receive=true;   //�����̱߳�־λ
	private boolean first=true;  //������ʾ�Ƿ��Ƿ��ͺ�ĵ�һ�η���
	private int length=-1;       //������ʾ��Ҫ������ݵ��ܳ���
	private int leftLength=0;    //�ѽ��յ��ĳ���
	private StringBuilder info=new StringBuilder();      //���յ����ַ�
	private Date start;          //��ʼ���յ�ʱ��
	private void ReceiveMessage()
	{
		try
		{
			Log.i(TAG, "StartReceive");
			start=Calendar.getInstance().getTime();
			while(receive)
			{
				InputStream is=null;
				try 
				{
					if(socket!=null&&!socket.isClosed()&&Writeout!=null)
					{
						is = socket.getInputStream();
						Date now=Calendar.getInstance().getTime();
						if(is.available()!=0)
						{
							byte[] buffer = new byte[is.available()];
							//��ȡ������
							is.read(buffer);
							String responseInfo ="";
							if(first)
							{
								length=(int)((((0xff & buffer[3])) << 24) + ((0xff & buffer[2]) << 16) + ((0xff & buffer[1]) << 8) + (0xff & buffer[0]));
								int len;
								if(buffer.length<=length)
									len=buffer.length;
								else//ճ��
									len=length;
								byte[] tmpBuf=new byte[len-4];
								System.arraycopy(buffer, 4, tmpBuf, 0, tmpBuf.length);
								responseInfo = new String(tmpBuf);
								first=false;
							}
							else
							{
								responseInfo = new String(buffer);
							}
							try
							{
								info.append(responseInfo);
								leftLength+=buffer.length;
							}
							catch(Exception ex){System.gc();}
							catch(Error err){System.gc();}
							if(leftLength==length)
							{
								Log.i(TAG, info.toString());
								//ת��Ϊ�ַ�
								if(!info.toString().equals("")&&!info.toString().isEmpty())
								{
									Message msg=new Message();
									msg.what=1;
									Bundle bundle=new Bundle();
									if(!info.equals("<MfusionPlayer/>"))
									{
										this.MFConnectionState=MFConnectionState.Connection;
										bundle.putString("connection",ServerConnectStatus.Connection.toString());


										XMLHelper helper=new XMLHelper(info.toString());
										Element media=helper.getElementByXPath("MfusionPlayer");
										if(media==null)
											return;
										Element[] elements=helper
												.getElementsByParentElement(media);	
										String servertime=media.getAttribute("ServerTime");
										String updatetime=media.getAttribute("UpdateTime");
										String timezonename=media.getAttribute("ServerTimeZone");
										String mediaport=media.getAttribute("MediaPort");
										MainActivity.Instance.PlayerSetting.setMediaport(mediaport);
										String playername=media.getAttribute("PlayerName");
										MainActivity.Instance.PlayerSetting.setPlayername(playername);
										MainActivity.Instance.PlayerSetting.setNetworkStatus(media.getAttribute("ShowNetworkStatus"));
										if(this.m_IsfirstConnect)
										{
											if(media.hasAttribute("WeatherService")){
												MainActivity.Instance.PlayerSetting.setRemoteServiceAddress(media.getAttribute("WeatherService"));
												MainActivity.Instance.PlayerSetting.setRemoteServiceMethod(media.getAttribute("WeatherServiceMethod"));
											}
											
											if(this.firstTryConnect!=null)
											{
												LoggerHelper.WriteLogfortxt("First Connect to mf server!");
												Object[] args={ServerConnectStatus.Connection,servertime,timezonename}; 
												this.firstTryConnect.call(args);
											}
											m_IsfirstConnect=false;
										}
										if(elements!=null&&elements.length>0&&updatetime.compareTo(MainActivity.Instance.PlayerSetting.getUpdateTime())>0)
										{

											LoggerHelper.WriteLogfortxt("New Schedule==>");
											XMLHelper xmlHelper = new XMLHelper();
											String xmlStr="<?xml version='1.0' encoding='utf-8'?>"+info;
											xmlHelper.SaveXml(xmlStr);
											MainActivity.Instance.mHandler.sendEmptyMessage(0);
										}

									}
									else
									{
										this.MFConnectionState=MFConnectionState.OverLimit;
										bundle.putString("connection",ServerConnectStatus.OverLimit.toString());
										if(this.m_IsfirstConnect)
										{

											if(this.firstTryConnect!=null)
											{


												Object[] args={ServerConnectStatus.OverLimit,null,null}; 
												this.firstTryConnect.call(args);
											}
											m_IsfirstConnect=false;
										}
									}
									msg.setData(bundle);
									MainActivity.Instance.PBUDispatcher.mHandler.sendMessage(msg);
									receive=false;
									Log.i(TAG, "EndReceive");
								}
							}



						}
						else if(DateTimeHelper.GetDuration(now,start)>Timeout)//��ʱ
						{
							receive=false;
							this.CloseSocket();
							/*this.InitSocket();*/
							Log.i(TAG, "Timemout,EndReceive");
							//LoggerHelper.WriteLogfortxt("SocketMessageService Timeout");
						}
					}
					else
					{
						this.CloseSocket();
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					//LoggerHelper.WriteLogfortxt("SocketMessageService ReceiveMessage==>"+e.getMessage());
					//e.printStackTrace();
					//e.printStackTrace();
				}


			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			System.gc();
		}

	}

	ReentrantLock lock1=new ReentrantLock();
	//��ʼ��
	private boolean InitSocket() {
		lock1.lock();
		boolean result=true;
		try
		{
			// TODO Auto-generated method stub
			if(this.socket==null||this.socket.isClosed()||this.Writeout==null)
			{
				//LoggerHelper.WriteLogfortxt("New Socket");
				try
				{
					if(ConnectionState)
					{
						Socket m_socket=new Socket();
						InetSocketAddress address=new InetSocketAddress(MainActivity.Instance.PlayerSetting.MFServerIp,CommonConvertHelper.StringToInt(MainActivity.Instance.PlayerSetting.MFServerPort));
						m_socket.connect(address, 1000);
						m_socket.setTcpNoDelay(true);

						m_socket.setOOBInline(true);
						m_socket.setKeepAlive(true);
						this.In = new BufferedReader(new InputStreamReader(
								m_socket.getInputStream()));
						this.Out = new BufferedWriter(
								new OutputStreamWriter(
										m_socket.getOutputStream()));
						this.Writeout = new PrintWriter(Out);
						this.socket=m_socket;
					}

				}
				catch(Exception ex){result=false;}
			}
		}

		catch(Exception ex){result=false;}
		finally
		{
			lock1.unlock();
		}
		return result;

	}
	//�ر�����
	private void CloseSocket() {
		// TODO Auto-generated method stub
		//LoggerHelper.WriteLogfortxt("Close Socket");
		lock1.lock();
		try 
		{
			try
			{
				if(socket!=null)
				{
					socket.shutdownInput();
					socket.shutdownOutput();
				}
			}
			catch(Exception ex)
			{

			}
			try
			{
				if(In!=null)
					In.close();
			}
			catch(Exception ex){}
			try
			{
				if(Writeout!=null)
					Writeout.close();
			}
			catch(Exception ex){}
			try
			{
				if(Out!=null)
					Out.close();
			}
			catch(Exception ex){}
			try
			{
				if(socket!=null)
					socket.close();
			}
			catch(Exception ex){}

			socket=null;
			Writeout=null;
			Out=null;

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			lock1.unlock();
			System.gc();
		}


	}
	@Override
	public void Restart() {
		// TODO Auto-generated method stub
		try
		{
			this.RUNNING=true;
			this.m_communicatethread.start();
		}
		catch(Exception e)
		{
			LoggerHelper.WriteLogfortxt("SocketMessageService Restart==>"+e.getMessage());
		}
	}


	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		try
		{
			this.RUNNING=false;
		}
		catch(Exception e)
		{
			LoggerHelper.WriteLogfortxt("SocketMessageService Stop==>"+e.getMessage());
		}
	}




}
