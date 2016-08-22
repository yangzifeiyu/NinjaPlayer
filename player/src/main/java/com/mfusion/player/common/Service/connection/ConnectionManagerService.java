package com.mfusion.player.common.Service.connection;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ConnectTargetType;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Service.BasicServiceInterface;
import com.mfusion.player.common.Service.SocketMessageService;

public class ConnectionManagerService implements BasicServiceInterface {

	BasicServiceInterface connectService;
	
	public ServerConnectStatus MFConnectionState=ServerConnectStatus.Connection;
	
	public ConnectionManagerService(Caller connectCaller){
		try {

			if(MainActivity.Instance.PlayerSetting.getConntectTarget()==ConnectTargetType.local){
				connectService=new ConnectLocalService();
				((ConnectLocalService)connectService).setConnectCaller(connectCaller);
			}
			else {
				connectService=new SocketMessageService();
				((SocketMessageService)connectService).firstTryConnect=connectCaller;
			}
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("ConnectLocalService Start==>"+e.getMessage());
		}
	}
	
	public ServerConnectStatus getConnectStatus(){
		if(MainActivity.Instance.PlayerSetting.getConntectTarget()==ConnectTargetType.local)
			return ServerConnectStatus.Connection;
		else
			return ((SocketMessageService)connectService).MFConnectionState;
	}
	
	public void ChangeConnectionState(boolean connection){
		if(MainActivity.Instance.PlayerSetting.getConntectTarget()!=ConnectTargetType.local)
			((SocketMessageService)connectService).ChangeConnectionState(connection);
	}
	
	@Override
	public void Restart() {
		// TODO Auto-generated method stub

		this.connectService.Restart();
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub

		this.connectService.Stop();
	}

}
