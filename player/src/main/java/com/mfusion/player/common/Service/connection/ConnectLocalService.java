package com.mfusion.player.common.Service.connection;

import java.io.File;

import android.os.FileObserver;
import android.util.Log;

import com.mfusion.commons.tools.FileOperator;
import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ServerConnectStatus;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Service.BasicServiceInterface;
import com.mfusion.player.common.Setting.PlayerKeyWords;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class ConnectLocalService  implements BasicServiceInterface{

	Caller connectCaller;
	
	ScheduleXmlListener scheduleXmlListener;

	public void setConnectCaller(Caller connectCaller){
		this.connectCaller=connectCaller;
		
		try {
			FileOperator.createDir(PlayerStoragePath.XMLStorage);
			this.scheduleXmlListener=new ScheduleXmlListener(PlayerStoragePath.XMLStorage );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public void Restart() {
		// TODO Auto-generated method stub

		try {
			
			if(this.connectCaller!=null){
				Object[] args={ServerConnectStatus.Connection,null,null}; 
				this.connectCaller.call(args);
			}
			
			//this.scheduleXmlListener.startWatching();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("ConnectLocalService Start==>"+e.getMessage());
		}
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		
		try {
			this.scheduleXmlListener.stopWatching();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public class ScheduleXmlListener extends FileObserver {       
		   
        public ScheduleXmlListener(String path) {        
               super(path);       
        }       
 
        @Override      
        public void onEvent(int event, String path) {
        	switch(event) {
        		case FileObserver.ALL_EVENTS:       
                    Log.d("all", "path:"+ path);       
                    break; 
                case FileObserver.CREATE: 
                case FileObserver.MODIFY:
                case FileObserver.MOVED_TO:
                	Log.d("Create", "path:"+ path);     
                	if(PlayerKeyWords.ScheduleXmlName.equalsIgnoreCase(path)){
                		if(connectCaller!=null){
            				Object[] args={ServerConnectStatus.Connection,null,null}; 
            				connectCaller.call(args);
            			}
                	}
                    break;       
            }       
       }       
 } 
}
