/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *Device Manager
 */
package com.mfusion.player.common.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Command;
import com.mfusion.player.common.Entity.TimelineCommand;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.CommandStorage;

public class DeviceManagerService implements BasicServiceInterface{

	private HandleTimer m_monitor;

	private List<TimelineCommand> m_timeline;

	private static final int interval=1000;

	/*
	 * ���캯��
	 */
	public DeviceManagerService()
	{
		try
		{
			this.m_timeline = new ArrayList<TimelineCommand>();
			m_monitor = new HandleTimer() {
				@Override
				protected void onTime() {
					m_monitor_Elapsed();
				}
			};
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DeviceManager==>"+ex.getMessage());
		}

	}

	/*
	 * ִ��timelinecommand
	 */
	private void m_monitor_Elapsed() {
		// TODO Auto-generated method stub
		try 
		{
			if (this.m_timeline.size()> 0)
			{
				TimelineCommand t_device = this.m_timeline.get(0);
				//һ����
				Date date=DateTimeHelper.GetAddedDate(t_device.Runtime,60,MainActivity.Instance.PlayerSetting.Timezone);//1����֮����Ч
				if (DateTimeHelper.CompareTime(t_device.Runtime, MainActivity.Instance.Clock.Now)<=0&&DateTimeHelper.CompareTime(MainActivity.Instance.Clock.Now, date)<=0)
				{
					int size=t_device.commondList.size();
					for (int i=0;i<size;i++)
					{
						Command device=t_device.commondList.get(i);
						if(device.getCommandType()!=CommandType.Volume){
							MainActivity.Instance.PlayerSetting.setRealCmdExecuteTime(device.getCommandType()+t_device.StartDate+t_device.StartTime);
						}
						device.Run();
					}
					this.m_timeline.remove(t_device);
				}
				else if (DateTimeHelper.CompareTime(MainActivity.Instance.Clock.Now, date)>0) {
					this.m_timeline.remove(t_device);
				}
			}
		}
		catch (Exception ex) 
		{
			LoggerHelper.WriteLogfortxt("DeviceManager m_monitor_Elapsed==>"+ex.getMessage());

		}
	}

	/*
	 * ��ʼdevicemanagerʱ�Ӽ��
	 */
	@Override
	public void Restart()
	{
		try
		{
			this.m_monitor.start(0,interval);
			CommandStorage device_storage = new CommandStorage();
			this.m_timeline = device_storage.GetAllTimeLineDevices();

		}
		catch (Exception ex) 
		{ 
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("DeviceManager Restart==>"+ex.getMessage());

		}

	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		this.m_monitor.stop();
	}


}
