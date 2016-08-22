/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *PlayerSetting
 */
package com.mfusion.player.common.Setting.Player;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Enum.*;


public class PlayerSetting{

	public  ServerType ServerType;
	public  String MFServerIp = "192.168.1.79";
	public  String MediaServerIP = "192.168.1.79";
	public  String MFServerPort="6060";
	public Integer ScreenOrientation=0;
	
	public Boolean ShowNetwork=true;
	public Boolean isModifyFromServer=false;
	
	public AndroidType BoxType=AndroidType.Panasonic; 
	public String RealCmdExecuteTime="";
	
	private  String mediaport="4041";
	
	private String remoteServiceAddress="";
	
	private String remoteServiceMethod="";
	
	private String exitPassword= InternalKeyWords.Config_DefaulttPassword;
	
	private ConnectTargetType conntectTarget=ConnectTargetType.local;
	
	public String getMediaport() {
		return mediaport;
	}

	public void setMediaport(String mediaport)
	{
		if(this.mediaport.equals(mediaport)==false)
		{
			this.mediaport = mediaport;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("MediaPort", mediaport);
			}
			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setMediaport==>"+ex.getMessage());
			}
		}
	}

	public ConnectTargetType getConntectTarget() {
		return conntectTarget;
	}

	public String getExitPassword() {
		return exitPassword;
	}
	
	public String getRemoteServiceAddress() {
		return remoteServiceAddress;
	}
	
	public void setRemoteServiceAddress(String remoteServiceAddress)
	{
		if(this.remoteServiceAddress.equals(remoteServiceAddress)==false)
		{
			this.remoteServiceAddress = remoteServiceAddress;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("ServiceAddress", remoteServiceAddress);
			}
			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setMediaport==>"+ex.getMessage());
			}
		}
	}

	public String getRemoteServiceMethod() {
		return remoteServiceMethod;
	}
	
	public void setRemoteServiceMethod(String remoteServiceMethod)
	{
		if(this.remoteServiceMethod.equals(remoteServiceMethod)==false)
		{
			this.remoteServiceMethod = remoteServiceMethod;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("ServiceMethod", remoteServiceMethod);
			}
			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setMediaport==>"+ex.getMessage());
			}
		}
	}
	
	public void setServerIP(String serverIP)
	{
		if(this.MFServerIp.equals(serverIP)==false)
		{

			this.MFServerIp = serverIP;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("MFServerIp", serverIP);
			}

			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setMFServerIP==>"+ex.getMessage());
			}
		}
	}
	
	public void setScreenOrientation(String screenOrientation)
	{
		if(this.ScreenOrientation.toString().equals(screenOrientation)==false)
		{

			this.ScreenOrientation = Integer.parseInt(screenOrientation);
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("ScreenOrientation", screenOrientation);
			}

			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setScreenOrientation==>"+ex.getMessage());
			}
		}
	}
	
	public void setServerPort(String serverport)
	{
		if(this.MFServerPort.equals(serverport)==false)
		{

			this.MFServerPort = serverport;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("MFServerPort", serverport);
			}

			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setMFServerPort==>"+ex.getMessage());
			}
		}
	}
	
	private String playername="";


	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		if(this.playername.equals(playername)==false)
		{

			this.playername = playername;
			try
			{
				PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
				settinghelper.UpdateFiled("PlayerName", playername);
			}

			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("PlayerSetting setPlayername==>"+ex.getMessage());
			}
		}
	}

	public void setNetworkStatus(String status) {
		try {
			if(status.equals("")==false&&this.ShowNetwork.toString().equalsIgnoreCase(status)==false)
			{
				this.ShowNetwork = Boolean.parseBoolean(status);
				isModifyFromServer=true;
				try
				{
					PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
					settinghelper.UpdateFiled("ShowNetwork", this.ShowNetwork.toString());
				}

				catch(Exception ex)
				{
					LoggerHelper.WriteLogfortxt("PlayerSetting ShowNetwork==>"+ex.getMessage());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public int TCPPort=5059;
	private   String license="";
	private String timezonestr="";
	public TimeZone Timezone=TimeZone.getDefault();
	public String getTimezonestr() {
		return timezonestr;
	}

	public void setTimezonestr(String timezonestr) {
		this.timezonestr = timezonestr;
		try
		{
			Timezone=TimeZone.getTimeZone(this.timezonestr);
		}
		catch(Exception ex)
		{
			Timezone=TimeZone.getDefault();
		}
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		try
		{
			this.license = license;
			PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
			settinghelper.UpdateFiled("License", license);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSetting setLicense==>"+ex.getMessage());
		}
	}

	public void setRealCmdExecuteTime(String realCmdExecuteTime) {
		try
		{
			this.RealCmdExecuteTime = realCmdExecuteTime;
			PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
			settinghelper.UpdateFiled("RealCmdExecuteTime", realCmdExecuteTime);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSetting setLicense==>"+ex.getMessage());
		}
	}

	private  String updateTime="";
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		try
		{
			this.updateTime = updateTime;
			PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
			settinghelper.UpdateFiled("UpdateTime", updateTime);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSetting setUpdateTime==>"+ex.getMessage());
		}

	}

	/*
	 * ��ȡ���������Ϣ
	 */
	public PlayerSetting()
	{
		this.refreshConfigInfo();
	}


	public void refreshConfigInfo() {
		// TODO Auto-generated method stub
		try
		{
		
			
			PlayerSettingHelper settinghelper=new PlayerSettingHelper(MainActivity.Instance);
			Map<String,String> map=settinghelper.GetAllFileds();

			if(map.containsKey("MFServerIp"))
			{
				MFServerIp=MediaServerIP=map.get("MFServerIp");	
				//LoggerHelper.WriteLogfortxt("IP==>"+MFServerIp);
			}
			
			if(map.containsKey("MFServerPort"))
			{
				MFServerPort=map.get("MFServerPort");
				//LoggerHelper.WriteLogfortxt("MFServerPort==>"+MFServerIp);
			}
			
			if(map.containsKey("MediaServerPort"))
			{
				mediaport=map.get("MediaServerPort");
				//LoggerHelper.WriteLogfortxt("MediaServerPort==>"+mediaport);
			}
			
			if(map.containsKey("UpdateTime"))
			{
				this.updateTime=map.get("UpdateTime");
				//LoggerHelper.WriteLogfortxt("UpdateTime==>"+updateTime);
			}
			
			if(map.containsKey("ShowNetwork"))
			{
				this.ShowNetwork=Boolean.parseBoolean(map.get("ShowNetwork"));
			}
			
			if(map.containsKey("License"))
			{
				this.license=map.get("License");
			}
			
			if(map.containsKey("AndroidType"))
			{
				this.BoxType=AndroidType.fromString(map.get("AndroidType"));
			}
			
			if(map.containsKey("ScreenOrientation"))
			{
				this.ScreenOrientation=Integer.parseInt(map.get("ScreenOrientation")) ;
			}

			if(map.containsKey("RealCmdExecuteTime"))
			{
				this.RealCmdExecuteTime=map.get("RealCmdExecuteTime");
			}
			
			if(map.containsKey("ServiceAddress"))
			{
				this.remoteServiceAddress=map.get("ServiceAddress");
			}
			if(map.containsKey("ServiceMethod"))
			{
				this.remoteServiceMethod=map.get("ServiceMethod");
			}
			if(map.containsKey("ExitPassword"))
			{
				this.exitPassword=map.get("ExitPassword");
			}
			if(license.equals(""))
			{
				String license=GetMackAddress();
				this.setLicense(license);
			}
			//LoggerHelper.WriteLogfortxt("License==>"+license);
			
			if(map.containsKey("PlayerName"))
				this.playername=map.get("PlayerName");
			else
				this.playername=this.license;
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSetting GetConfig==>"+ex.getMessage());
		}

	}
	/*
	 * ��ȡmac��ַ
	 */
	public String GetMackAddress() {
		String macAddress="";
		try {
			NetworkInterface NIC = NetworkInterface.getByName("eth0");
			byte[] b = NIC.getHardwareAddress();
			// byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				String str = Integer.toHexString(b[i] & 0xFF);
				buffer.append(str.length() == 1 ? 0 + str : str);
			}
			macAddress = buffer.toString().toUpperCase();


		} catch (Exception ex) {
			LoggerHelper.WriteLogfortxt("PlayerSetting GetMackAddress==>"+ex.getMessage());
			try
			{
				WifiManager manager = (WifiManager)MainActivity.Instance.getSystemService(Context.WIFI_SERVICE);
				macAddress = manager.getConnectionInfo().getMacAddress()
						.replace(":", "");
				macAddress=macAddress.toUpperCase();
			}
			catch(Exception e)
			{

			}
		}
		return macAddress;

	}


}
