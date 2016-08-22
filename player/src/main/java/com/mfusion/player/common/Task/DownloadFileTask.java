/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *Download File
 */
package com.mfusion.player.common.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import org.w3c.dom.Element;
import com.mfusion.player.common.Entity.DownloadObject;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

import android.util.Log;
import com.mfusion.player.common.Enum.*;



public class DownloadFileTask {


	private static final int Timeout=8;
	private static String TAG="DownloadFile";
	private final static int Buffer = 8 * 1024;
	private Queue<DownloadObject> m_request;
	int token=0;//when token is zero, but queue is not null, sleep some mins, then restart to download

	private Thread m_thread;
	private int m_waiting_Interval = 1000;
	private DownloadObject downfile;
	private String fileName,fileNameXML;

	private String source;
	private FileType fileType;
	private String localpath =	PlayerStoragePath.ImageStorage;

	private Socket t_socket = null;
	private BufferedReader In = null; // �������
	private BufferedWriter Out = null;
	private PrintWriter Writeout = null;

	private  boolean runningstate=false;
	private boolean Request=true;                //��ʼĳһ���ļ������ر�ʶλ
	private boolean NoComplete=true;               //��ʼĳ�ļ�ĳһ�ε����ر�ʶλ

	public static ArrayList<DownloadObject> requests=new ArrayList<DownloadObject>();

	public double NowSize=0;

	/*
	 * ���캯��
	 */
	public DownloadFileTask()
	{
		try
		{

			this.m_request= new LinkedBlockingQueue<DownloadObject>();//��ִ�е��ļ�
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DownloadFile==>"+ex.getMessage());
		}
	}


	public void ThreadMethod(DownloadObject o)
	{
		try
		{
			if(!requests.contains(o))
			{
				LoggerHelper.WriteLogfortxt("DownloadFileTask request:"+o.FileName+";size:"+o.MediaLength);
				
				this.lock.lock();
				requests.add(o);
				m_request.add(o);
				token++;
				NowSize+=o.MediaLength;
				this.lock.unlock();			
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DownloadFile ThreadMethod==>"+ex.getMessage());
		}
	}



	/*
	 * ���ط���
	 */
	ReentrantLock lock = new ReentrantLock();

	StringBuilder info=new StringBuilder(); 
	StringBuilder message=new StringBuilder();
	long offset = 0;                  //temp�ļ��Ĵ�С
	String createTime = "";

	String tempFilename="";              //temp�ļ�
	String tempFilepath="";               //temp�ļ�·��
	File tempFile=null;                    //temp�ļ�

	String filename="";                    //���յ����ļ�����
	boolean isover=false;                  //������ȫ��־λ
	int serveroffset=0;                    //����offet
	boolean first=true;               //send���Ƿ�Ϊ��һ�ν���
	double fileLength=0;              //�ļ��ܳ���
	int totallength=-1;               //�ļ�ĳһ���ܳ���
	int xmllength=-1;                 //xml����
	int streamlength=-1;              //һ�η��ͺ󷵻��ļ����ݳ���
	byte[] xmlBuf=null;
	int leftlength=0;                 //�ļ�ĳһ�ν��յĳ���
	Date Start;   

	private  void download() throws Exception {
		try 
		{
			
			while(runningstate)
			{
				try
				{
					if(m_request.size()==0)
						return;
					if(token==0){
						token=m_request.size();
						Thread.sleep(m_waiting_Interval);
					}
					else
					{
						this.lock.lock();
						token--;
						if(MainActivity.Instance.FileManager.MediaConnectionState&&MainActivity.Instance.ConnectManagerService.getConnectStatus().equals(ServerConnectStatus.Connection))
						{
							this.NoComplete=true;//whether download completed
							this.Request=true;//can start download
							this.downfile=m_request.poll();
							this.fileName=downfile.FileName.replace("\\", "/");
							this.fileNameXML=this.fileName.replace("&", "&amp;").replace("'", "&apos;");
							this.fileType=downfile.FileType;
							this.source=downfile.FileSource;
							this.fileLength=downfile.MediaLength;
							if(this.fileType.equals(FileType.Image))
							{
								this.localpath=PlayerStoragePath.ImageStorage;
							}
							else if(this.fileType.equals(FileType.Video))
							{
								this.localpath=PlayerStoragePath.VideoStorage;
							}
							else if(this.fileType.equals(FileType.Audio))
							{
								this.localpath=PlayerStoragePath.AudioStorage;
							}

							tempFilename=this.fileName+".temp";        //temporary file
							tempFilepath=this.localpath+this.tempFilename;
							info=new StringBuilder(); 
							message=new StringBuilder();
							offset = 0;                  //lenght that has downloaded
							createTime = "";
							filename="";
							isover=false;   //whether download completed
							serveroffset=0;
							first=true;               //whether is the first socket whitch have mediainfo
							totallength=-1;                    //media bytes lenght
							xmllength=-1;                 //XML about media info
							streamlength=-1; 
							
							//��ʼ������ݵ�ʱ��
							tempFile = new File(tempFilepath);
							try
							{
								if (tempFile.exists()) {
									offset= tempFile.length();
								}
								else
								{
									tempFile.getParentFile().mkdirs();
									tempFile.createNewFile();
								}

								while(Request)//Start download media
								{
									if(MainActivity.Instance.FileManager.MediaConnectionState&&MainActivity.Instance.ConnectManagerService.getConnectStatus().equals(ServerConnectStatus.Connection))
									{
										//get available socket
										boolean flag=this.InitializeSocket();
										if(flag==true)
										{
											try
											{
												//download a part of media
												boolean result=SendFileDownRequest();
												if(result)
												{
													//get ready to start download
													first=true;
													NoComplete=true;
													Request=true;
													info.delete(0, info.length());
													info.setLength(0);
													leftlength=0;//already downloaded length in these media part
													if(ReceiveFileContent()){
														if(requests.contains(downfile))
															requests.remove(downfile);
														if(requests.size()<=0)
														{
															LoggerHelper.WriteLogfortxt("DownloadFileTask complete.");
															MainActivity.Instance.FileManager.ChangeState(false);
														}
														continue;
													}
												}

											}
											catch (Exception ex) //socket�쳣
											{
												ex.printStackTrace();
											}
											finally
											{
												System.gc();
											}
										}
									}
									this.m_request.add(downfile);
									break;
								}
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
								this.m_request.add(downfile);
							}
						}
						else//can not connect server
						{
							this.pause();
							System.gc();
						}
						lock.unlock();
					}
					Thread.sleep(100);
				}
				catch (Exception ex) 
				{
				}
				catch(Error err)
				{
					System.gc();
				}
				finally
				{
					System.gc();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("DownloadFile download==>"+ex.getMessage());

		}
		finally
		{
			System.gc();
		}
	}

	//��������
	private boolean  SendFileDownRequest() {
		// TODO Auto-generated method stub
		try
		{
			if(this.t_socket!=null&&!this.t_socket.isClosed()&&Writeout!=null)
			{
				message=new StringBuilder("<DownloadMedia SocketVersion='1.1' MediaPath='").append(this.fileNameXML).append("' MediaSource='").append(source).append("'  CreateTime='" ).append(createTime).append("' OffSet='" ).append(offset).append("' MaxBytes='10240'/>");
				Start=MainActivity.Instance.Clock.Now;
				this.Writeout.print(message.toString());
				this.Out.flush();
				Log.i(TAG, message.toString());
				message.delete(0, message.length());
				message.setLength(0);
				return true;
			}
			else
			{
				this.CloseSocket();
				return false;
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("SendFiledownRequest==>"+ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	private Boolean ReceiveFileContent()
	{
		try
		{
			//check connection status
			if(MainActivity.Instance.FileManager.MediaConnectionState&&MainActivity.Instance.ConnectManagerService.getConnectStatus().equals(ServerConnectStatus.Connection))
			{
				
				Date now=MainActivity.Instance.Clock.Now;//last receive message time

				while(NoComplete)//whether end socket receive
				{
					try
					{
						if(this.t_socket!=null&&!this.t_socket.isClosed()&&Writeout!=null)
						{
							InputStream isstr = this.t_socket.getInputStream();
							byte[] filebytes=null;
							
							if(isstr.available()!=0)//receive message
							{
								try
								{
									byte[] buffer = new byte[isstr.available()];
									isstr.read(buffer);

									if(first)
									{
									    //In the bytes,the top eight is for totla lenght(xml length + media streaming length) and xml length
										//the top four is total lenght, other is xml length
										totallength=(int)((((0xff & buffer[3])) << 24) + ((0xff & buffer[2]) << 16) + ((0xff & buffer[1]) << 8) + (0xff & buffer[0]));
										xmllength=(int)((((0xff & buffer[7])) << 24) + ((0xff & buffer[6]) << 16) + ((0xff & buffer[5]) << 8) + (0xff & buffer[4]));
										if(xmllength<0)//������С��0��ֹͣ���ν��գ����·�������
										{
											NoComplete=false;
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" error xml");
											break;

										}
										//get media bytes length
										streamlength=totallength-8-xmllength;		
										try
										{
											xmlBuf=new byte[xmllength];
											System.arraycopy(buffer,8, xmlBuf, 0, xmlBuf.length);
											String xmlinfo = new String(xmlBuf);
											Log.i(TAG, xmlinfo);

											//get media info from server
											XMLHelper helper=new XMLHelper(xmlinfo);

											Element media =helper.getElementByXPath("DownloadMedia");
											filename=media.getAttribute("MediaPath");
											createTime = media.getAttribute("CreateTime");
											isover=Boolean.parseBoolean(media.getAttribute("IsOver").toLowerCase());
											serveroffset=Integer.parseInt(media.getAttribute("OffSet"));
											if(serveroffset!=offset)//not match
											{
												Request=false;//�ļ��������
												NoComplete=false;
												tempFile.delete();
												tempFile=null;
												LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" receive error");
												break;
											}
										}
										catch(Exception ex)//���·���
										{
											ex.printStackTrace();						
											NoComplete=false;						
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" "+ex.getMessage());
											break;
										}
										finally
										{
											xmlBuf=null;
										}

										try
										{
											int len;
											if(buffer.length<=totallength)
												len=buffer.length;
											else//ճ��
											{
												len=totallength;
											}
											byte[] tmpBuf=new byte[len-8-xmllength];
											System.arraycopy(buffer,8+xmllength, tmpBuf, 0, tmpBuf.length);
											filebytes=tmpBuf;
											first=false;//���ճɹ���
										}
										catch(Exception ex)//���·���
										{
											ex.printStackTrace();		
											NoComplete=false;
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" "+ex.getMessage());
											break;
										}
									}
									else//all socket bytes are media stream
									{
										filebytes=buffer;

									}
									leftlength+=filebytes.length;//downloaded bytes length
									
									//media file is exist or not
									if(filename.equals(this.fileName)&&(!createTime.equals("")&&!createTime.isEmpty()))
									{
										if(fileLength>0&&leftlength<=streamlength)
										{
											RandomAccessFile fs;
											fs= new RandomAccessFile(tempFilepath, "rwd");
											FileChannel channel = fs.getChannel();
											FileLock filelock = channel.lock();
											InputStream in=null;
											try
											{
												in=new ByteArrayInputStream(filebytes);
												byte[] buf = new byte[Buffer];
												int len;
												fs.seek(offset);
												while ((len = in.read(buf)) >= 0) {

													fs.write(buf, 0, len);
													offset += len;
												}
												downfile.LoadingLength=offset;
											}
											catch(Exception ex)
											{
												ex.printStackTrace();
												break;
											}
											finally
											{ 
												if(in!=null)
													in.close();
												in=null;
												buffer=null;

												filelock.release();
												channel.close();
												channel=null;
												if(fs!=null)
													fs.close();

												fs=null;
												filebytes=null;

												System.gc();
											}
											
											if(leftlength==streamlength)
											{
												NoComplete=false;
												Log.i(TAG, "Package completed.");
											}
										}else {//receiving bytes is more than request bytes
											NoComplete=false;
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" not match");
											break;
										} 

										if(fileLength==offset&&isover)//media files download finished
										{
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" completed.");
											Log.i(TAG, "DownloadFileTask==>"+this.fileName+" completed.");
											File reNameFile = new File(localpath+this.fileName);
											reNameFile.exists();
											tempFile.renameTo(reNameFile);
											Request=false;
											NoComplete=false;
											tempFile.delete();
											tempFile=null;
											reNameFile=null;
											downfile.LoadingLength=downfile.MediaLength;
											downfile.DownloadStatus=1;
											downfile.ModifyTime=MainActivity.Instance.Clock.Now;
											
											return true;
										}
										else if(fileLength<offset)//media streaming not match to server
										{
											Request=false;
											NoComplete=false;
											tempFile.delete();
											tempFile=null;
											LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" receive error");
										}

									}
									else//invalid data or media file not exist
									{
										Request=false;//�ļ��������
										NoComplete=false;
										LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" medialibrary not exists");
										break;
									}
								}
								catch(Exception ex)
								{
									ex.printStackTrace();
									LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" "+ex.getMessage());
								}
								finally
								{
									now=MainActivity.Instance.Clock.Now;//update last receiving socket time
								}
							}
							else if(DateTimeHelper.GetDuration(MainActivity.Instance.Clock.Now,now)>Timeout)//check time whether more than 
							{
								NoComplete=false;	
								Log.i(TAG, this.fileName+" Timemout,EndReceive");
								LoggerHelper.WriteLogfortxt("DownloadFileTask==>"+this.fileName+" Timemout");
								this.CloseSocket();//������գ���ʼ���µ�����
							}
						}
						else//Socket can not connect
						{
							NoComplete=false;
							this.CloseSocket();
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}//end while 
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	ReentrantLock lock1=new ReentrantLock();
	private boolean InitializeSocket() {
		lock1.lock();
		boolean result=true;
		try
		{
			// TODO Auto-generated method stub
			if(this.t_socket==null||this.t_socket.isClosed()||this.Writeout==null)
			{
				try
				{
					if(MainActivity.Instance.FileManager.MediaConnectionState)
					{
						//LoggerHelper.WriteLogfortxt("New socket begin==>"+this.fileName);
						Socket socket=new Socket();
						InetSocketAddress address=new InetSocketAddress(MainActivity.Instance.PlayerSetting.MediaServerIP,CommonConvertHelper.StringToInt(MainActivity.Instance.PlayerSetting.getMediaport()));
						socket.connect(address, 1000);
						socket.setTcpNoDelay(true);		
						socket.setOOBInline(true);
						socket.setKeepAlive(true);
						this.t_socket=socket;
						this.In = new BufferedReader(new InputStreamReader(
								t_socket.getInputStream()));
						this.Out = new BufferedWriter(
								new OutputStreamWriter(
										t_socket.getOutputStream()));
						this.Writeout = new PrintWriter(Out);
					}else{
						result=false;
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
					result=false;
				}
			}
		}
		catch(Exception ex)
		{
			result=false;
		}
		finally
		{
			lock1.unlock();
		}
		return result;

	}


	private void CloseSocket() {
		lock1.lock();
		try 
		{
			//LoggerHelper.WriteLogfortxt("Socket Close==>"+this.fileName);
			try
			{
				if(t_socket!=null)
				{
					t_socket.shutdownInput();
					t_socket.shutdownOutput();
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
				if(t_socket!=null)
					t_socket.close();
			}
			catch(Exception ex){}

			t_socket=null;
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


	/*
	 * ֹͣ����
	 */
	public void Stop() 
	{
		// TODO Auto-generated method stub
		if(this.m_request!=null)
			this.m_request.clear();
		this.runningstate=false;
		this.Request=false;
		this.NoComplete=false;
		this.NowSize=0;
		this.token=0;
		try{
			if(this.m_thread.isAlive()){
				this.m_thread.interrupt();
			}
		}catch(Exception ex){
			
		}
		this.m_thread=null;
		this.CloseSocket();
	}


	/*
	 * ��ͣ����
	 */
	public void pause() {
		// TODO Auto-generated method stub
		Request=false;
		NoComplete=false;
	}


	public void Start() {
		// TODO Auto-generated method stub
		try {
			this.runningstate=true;
			if(m_thread==null)
			{
				m_thread=new Thread(new Runnable() {
					public void run() {
						try {
							download();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
				m_thread.start();
			}
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("DownloadFileTask==>==>"+e.getMessage());
		}
		
	}


	public void InitSocket(boolean state) 
	{
		try
		{
			// TODO Auto-generated method stub
			if(state)
			{
				LoggerHelper.WriteLogfortxt("Change state==>"+this.fileName);
				this.InitializeSocket();
			}
			else
			{
				this.pause();
				this.CloseSocket();
			}
		}
		catch(Exception ex)
		{

		}
	}


}
