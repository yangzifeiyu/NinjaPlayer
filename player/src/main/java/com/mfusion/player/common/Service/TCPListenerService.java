package com.mfusion.player.common.Service;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Enum.TriggerType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Trigger.TCPTrigger;
import com.mfusion.player.common.Trigger.VirtualTrigger;

public class TCPListenerService implements BasicServiceInterface
{
	private TCPTrigger CurrentTriggerObject;
	private ServerSocket sever=null;
	private boolean Listening=false;
	private int interval=0;
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	private Thread m_listenThread= new Thread(new Runnable() {

		public void run() {
			ListenMethod();
		}
	});


	@Override
	public void Restart() {
		// TODO Auto-generated method stub
		Listening=true;
		m_listenThread.start();
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		try {
			sever.close();
			Listening=false;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block

		}
	}
	private void ListenMethod()
	{

		try {
			sever = new ServerSocket(MainActivity.Instance.PlayerSetting.TCPPort);
			while(Listening){
				try{
					final Socket socket = sever.accept();
					String str="";
					try{
						InputStream inputStream = socket.getInputStream();
						byte buffer[]=new byte[1024*4];
						int temp=0;
						while ((temp=inputStream.read(buffer))!=-1){
							str=new String(buffer,0,temp);
							if(str!=null)
							{
								break;
							}
						}
						socket.close();
						if(str.contains("mfusion"))
						{
							LoggerHelper.WriteLogfortxt("TcpListener ReceiveMessageAndStartTrigger==>Receive data:" + str);

							if (this.CurrentTriggerObject != null)
							{
								CurrentTriggerObject.StartTrigger();

							}
						}
					}
					catch(IOException e)
					{
						//e.printStackTrace();
					}


				}
				catch(IOException e)
				{
					//e.printStackTrace();
				}
			}
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}

	public void SetTriggerObject(PBU pbu)
	{
		try
		{
			if (pbu != null && pbu.internalTriggers != null)
			{
				int size=pbu.internalTriggers.size();
				for(int i=0;i<size;i++)
				{
					VirtualTrigger trigger=pbu.internalTriggers.get(i);
					if(trigger!=null)
					{
						if(trigger.triggerType.equals(TriggerType.TCP))
						{
							this.CurrentTriggerObject = (TCPTrigger) trigger;
							break;
						}
					}
				}
				if (this.CurrentTriggerObject != null)
					this.setInterval(this.CurrentTriggerObject.interval);
			}
			else
			{
				this.CurrentTriggerObject = null;
				this.setInterval(0);
			}
		}
		catch (Exception ex) 
		{ 
			LoggerHelper.WriteLogfortxt("TCPTrigger SetTriggerObject==>" + ex.getMessage()); 
		}
	}



}
