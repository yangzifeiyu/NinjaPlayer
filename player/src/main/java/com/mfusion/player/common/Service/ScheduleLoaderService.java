/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-09
 *
 *ScheduleLoader�߼�
 */
package com.mfusion.player.common.Service;
import java.util.List;
import java.util.Stack;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.ScheduleStorage;

public class ScheduleLoaderService implements BasicServiceInterface{

	private ScheduleStorage m_schedule_storage;
	public Stack<PBU> pbus ;
	public List<String> pbuid_list;
	/*
	 * ���캯��
	 */
	public ScheduleLoaderService() {
		this.m_schedule_storage = new ScheduleStorage();
	}


	/*
	 * ����pbuջ�Լ�pbuidlist
	 */
	private void Loaded() {
		// TODO Auto-generated method stub
		try {
			Stack<PBU> pbus = new Stack<PBU>();
			List<String> pbuid_list = null;
			this.m_schedule_storage.Refresh();
			pbus = m_schedule_storage.m_PBUCollection;     //��õ�pbulist
			pbuid_list = m_schedule_storage.m_pbu_id_list;  //pbuidlist
			
			this.pbus=pbus;
			this.pbuid_list=pbuid_list;
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("ScheduleLoader Loaded==>"+e.getMessage());
		}
	}

	@Override
	public void Restart() {
		try 
		{
			LoggerHelper.WriteLogfortxt("SchduleLoader Restart");
			this.Loaded();// ����
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("ScheduleLoader Restart==>"+e.getMessage());
		}

	}
	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		
	}
}
