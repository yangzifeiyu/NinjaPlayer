package com.mfusion.player.common.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Entity.*;
import com.mfusion.player.common.MathPBU.MergePBUHelper;
import com.mfusion.player.common.XML.ScheduleXML;

public class ScheduleStorage {
	public static PBU defaultPBU;
	public List<TimelinePBUBlock> timelinepbulist;

	public Stack<PBU> m_PBUCollection;
	public List<String> m_pbu_id_list;

	public ScheduleStorage() {
		this.m_PBUCollection = new Stack<PBU>();
		this.m_pbu_id_list = new ArrayList<String>();

	}

	public void Refresh() {
		// TODO Auto-generated method stub
		try 
		{
			this.GetLogicalUnitCollection();
			this.GetFullPBUCollection();
			this.MergePBU();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void MergePBU() {
		// TODO Auto-generated method stub
		try {
			this.m_PBUCollection.clear();
			MergePBUHelper.MergeAllTimelilneUnit(this.timelinepbulist);
			this.m_PBUCollection = MergePBUHelper.pbulist;
			if(this.timelinepbulist!=null)
				this.timelinepbulist.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("ScheduleStorage MergePBU==>"+e.getMessage());
		}
	}

	private void GetFullPBUCollection() {
		// TODO Auto-generated method stub
		if(timelinepbulist!=null&&timelinepbulist.size()>0)
		{
			for (TimelinePBUBlock timeline : this.timelinepbulist) {
				if(timeline!=null)
				{
					timeline.GetPBUCollection();
				}
			}
		}
		if (m_pbu_id_list!=null&&ScheduleStorage.defaultPBU!=null&&!m_pbu_id_list.contains(ScheduleStorage.defaultPBU.ID))
			this.m_pbu_id_list.add(ScheduleStorage.defaultPBU.ID);

		try {
			if(timelinepbulist!=null&&timelinepbulist.size()>0)
			{
				for (int i = this.timelinepbulist.size() - 1; i >= 0; i--) {
					TimelinePBUBlock timelinepbu = this.timelinepbulist.get(i);
					if(timelinepbu!=null&&timelinepbu.PBUList!=null)
					{
						int size=timelinepbu.PBUList.size();
						for (int j = 0; j <size ; j++) {
							if (this.m_pbu_id_list
									.contains(timelinepbu.PBUList.get(j).ID) == false)

								this.m_pbu_id_list.add(timelinepbu.PBUList.get(j).ID);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void GetLogicalUnitCollection() {
		// TODO Auto-generated method stub
		try {
			if(this.timelinepbulist!=null)
				timelinepbulist.clear();
			ScheduleXML scheduleXml = new ScheduleXML();
			ScheduleStorage scheduleStorage = scheduleXml.GetScheduleInfo();
			this.timelinepbulist = scheduleStorage.timelinepbulist;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
