/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *ChangeMediaConnectState
 */
package com.mfusion.player.common.Socket;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.library.Callback.MyCallInterface;


public class ChangeMediaConnectState  implements MyCallInterface{

	@Override
	public Object fuc(Object paras) {
		try
		{
			if (paras == null)
				return false;
			boolean state=Boolean.parseBoolean(paras.toString());
			MainActivity.Instance.FileManager.ChangeConnectionState(state);
			return true;
		}
		catch(Exception ex)
		{
			//LoggerHelper.WriteLogfortxt("ChangeMediaConnectState fuc==>"+ex.getMessage());
			return false;
		}
	}

}