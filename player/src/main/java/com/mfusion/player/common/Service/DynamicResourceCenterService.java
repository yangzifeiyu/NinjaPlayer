package com.mfusion.player.common.Service;

import java.util.Hashtable;
import java.util.Iterator;

import com.mfusion.player.library.Controller.HandleTimer;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Entity.DynamicResource;
import com.mfusion.player.common.Entity.SubscribedResource;
import com.mfusion.player.common.Enum.ExternalType;
import com.mfusion.player.common.Player.MainActivity;

public class DynamicResourceCenterService implements BasicServiceInterface{
	private Hashtable<String,Object> m_resourceTable;
	private static final int interval=1000;
	private HandleTimer m_refreshTimer=new HandleTimer() {

		@Override
		protected void onTime() {
			// TODO Auto-generated method stub
			m_refreshTimer_Elapsed();
		}
	};
	public DynamicResourceCenterService()
	{
		m_resourceTable=new Hashtable<String, Object>();
	}
	@Override
	public void Restart() {
		// TODO Auto-generated method stub
		m_refreshTimer.start(interval);
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		m_refreshTimer.stop();

	}

	public boolean RegeditControl(String RssURL) {
		// TODO Auto-generated method stub
		try
		{
			if (this.m_resourceTable.containsKey(RssURL))
			{
				DynamicResource resource = (DynamicResource)(this.m_resourceTable.get(RssURL));
				if (DateTimeHelper.CompareTime(resource.InvalidTime, MainActivity.Instance.Clock.Now)<= 0)
					resource.InvalidTime =DateTimeHelper.GetAddedDate(MainActivity.Instance.Clock.Now, resource.RefreshInterval*2,MainActivity.Instance.PlayerSetting.Timezone);
			}
		}
		catch(Exception ex) {return false; }
		return true;
	}
	@SuppressWarnings("rawtypes")
	private void m_refreshTimer_Elapsed()
	{
		this.m_refreshTimer.stop();
		try
		{
			Hashtable tmp = null;

			tmp = (Hashtable) this.m_resourceTable.clone();

			SubscribedResource request = null;

			for (Iterator itr = tmp.keySet().iterator(); itr.hasNext();)
			{
				Object key=itr.next();
				DynamicResource entity = (DynamicResource)tmp.get(key);
				if (DateTimeHelper.CompareTime(entity.InvalidTime, MainActivity.Instance.Clock.Now)> 0 && DateTimeHelper.CompareTime(entity.RefreshTime, MainActivity.Instance.Clock.Now)<= 0)
				{
					request = new SubscribedResource();
					request.Resource=key.toString();
					request.TimeRange = entity.RefreshInterval * 2;
					request.ResourcePath = entity.ResourceDir;
					if (ExternalType.RSS.equals(entity.type))
					{
						request.UpdateTime = entity.UpdateTime;
					}

				}
			}


		}
		catch (Exception ex) { LoggerHelper.WriteLogfortxt("DynamicResourceCenter ==> Refresh Timer : " + ex.getMessage()); }
		finally
		{
			
			this.m_refreshTimer.start(interval);
		}
	}
}
