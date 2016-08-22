package com.mfusion.player.common.MathPBU;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Player.MainActivity;

public class PBUCollectionHelper {
	public static Date logicStart;
	public static ArrayList<PBU> collection;

	public static void  getIntervalCollection(
			List<PBU> blockUnit, Date start, Date end) {
		try {
			if (blockUnit == null || blockUnit.size() <= 0)
				return;
			
	
			collection = new ArrayList<PBU>();
			Date currentTime =  MainActivity.Instance.Clock.Now;/// player������ʱ��
			Date cursor = start;// block��unit��StartTime
			int j = 0;// block�е�һ�����ŵ�Unit
			int totalDuration = 0;
			int size=blockUnit.size();
			for (int i = 0; i <size ; i++) {
				totalDuration += blockUnit.get(i).Duration;
			}
			if (DateTimeHelper.CompareTime(currentTime, start) > 0) {
				logicStart = currentTime;
				j = FirstPlayPBU(blockUnit, start, currentTime, totalDuration);
				if (j >= size)
					j = 0;
				cursor = logicStart;
				for (; j < size; j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					
					PBU pbu =(PBU)blockUnit.get(j).clone();
					pbu.StartTime = cursor;
					if (DateTimeHelper.CompareTime(tem, end) >= 0) {
						pbu.EndTime = end;
						collection.add(pbu);
						if (collection.size() > 0)
							collection.get(0).StartTime = currentTime;
						MergePBUHelper.MergeBlockUnit(collection);
						collection=MergePBUHelper.pbucollection;
						return;
					} else {
						pbu.EndTime = cursor = tem;
						collection.add(pbu);
					}
				}
			}
			
			
			if (collection.size() > 0)
				collection.get(0).StartTime = currentTime;
			int num = DateTimeHelper.GetDuration(end, cursor) / totalDuration;
			for (int i = 0; i < num; i++) {
				for (j = 0; j < size; j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					PBU pbu = (PBU)blockUnit.get(j).clone();
					pbu.StartTime = cursor;
					pbu.EndTime = cursor = tem;
					collection.add(pbu);
				}
			}
			if (cursor != end) {
				for (j = 0; j < size; j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					PBU pbu = (PBU)blockUnit.get(j).clone();
					pbu.StartTime = cursor;
					if (tem.compareTo(end) >= 0) {
						pbu.EndTime = end;
						collection.add(pbu);
						break;
					} else {
						pbu.EndTime = cursor = tem;
						collection.add(pbu);
					}
				}
			}
			MergePBUHelper.MergeBlockUnit(collection);
			collection=MergePBUHelper.pbucollection;
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
//start:timeline start,current:now 
	private static int FirstPlayPBU(List<PBU> col, Date start,
			Date currentTime, int totalDuration) {
		Date cursor = start;
		
		int num = DateTimeHelper.GetDuration(currentTime, cursor)
				/ totalDuration;
		cursor = DateTimeHelper.GetAddedDate(cursor, num * totalDuration,MainActivity.Instance.PlayerSetting.Timezone);

		int startnum = 0;
		if (cursor != currentTime) {
			int size=col.size();
			for (int i = 0; i < size; i++) {
				logicStart = cursor;
				Date tem = DateTimeHelper.GetAddedDate(cursor,
						col.get(i).Duration,MainActivity.Instance.PlayerSetting.Timezone);
				if (tem.compareTo(currentTime) > 0) {
					startnum = i;
					break;
				} else if (tem == currentTime) {
					startnum = ++i;
					break;
				} else
					cursor = tem;
			}
		}
		return startnum;
	}

	

}
