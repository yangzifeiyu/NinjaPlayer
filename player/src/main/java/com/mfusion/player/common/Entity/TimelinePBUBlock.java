package com.mfusion.player.common.Entity;

import java.util.ArrayList;
import java.util.Date;


import com.mfusion.player.common.MathPBU.PBUCollectionHelper;
import com.mfusion.player.common.Player.MainActivity;



import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class TimelinePBUBlock extends Timeline {

	public ArrayList<PBU> PBUList=new ArrayList<PBU>();

	public void GetPBUCollection() {
		// TODO Auto-generated method stub
		try {
			Date startdate=DateTimeHelper.ConvertToDateTime(this.StartDate, this.StartTime,MainActivity.Instance.PlayerSetting.Timezone);
			Date enddate=DateTimeHelper.ConvertToDateTime(this.EndDate, this.EndTime,MainActivity.Instance.PlayerSetting.Timezone);
			PBUCollectionHelper.getIntervalCollection(PBUList, startdate, enddate);
			this.PBUList =PBUCollectionHelper.collection ;
		
		} catch (Exception e) {
			LoggerHelper.WriteLogfortxt("TimelinePBUBlock GetPBUCollection==>"+e.getMessage());
		}
	}
}
