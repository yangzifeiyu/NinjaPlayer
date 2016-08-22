/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *ChangeMFConnectState
 */
package com.mfusion.player.common.Socket;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Helper.LoggerHelper;

public class ChangeMFConnectState  implements MyCallInterface{

	@Override
	public Object fuc(Object paras) {
		try
		{
			if (paras == null)
				return false;
			boolean state=Boolean.parseBoolean(paras.toString());
			MainActivity.Instance.ConnectManagerService.ChangeConnectionState(state);
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("ChangeMFConnectState fuc==>"+ex.getMessage());
			return false;
		}
	}

}
