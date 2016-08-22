/**
 * 
MainActivity.Instance.Clock.NowMainActivity.Instance.Clock.NowMainActivity.Instance.Clock.NowMainActivity.Instance.Clock.NowMainActivity.Instance.Clock.NowMainActivity.Instance.Clock.Now * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *Tcp Socket
 */
package com.mfusion.player.common.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.mfusion.player.common.Player.MainActivity;
public class TcpSocket {
	public Socket Socket;
	public String SERVER_IP;
	public int Server_PORT;
	public BufferedReader In = null; // �������
	public BufferedWriter Out = null;
	public PrintWriter Writeout = null;
	
	
	public TcpSocket(Socket socket,String ip,int port)
	{
		this.Socket=socket;
		this.SERVER_IP=ip;
		this.Server_PORT=port;
	}
}
