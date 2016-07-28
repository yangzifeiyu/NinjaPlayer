package com.mfusion.scheduledesigner.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleDrawHelper {

	public static String getRecurrenceByStartDate(Date date){

		String recurrence="0000000";
		try {
			int start_week = getWeekOfDate(date);
			
			return recurrence.substring(0, start_week-1)+"1"+recurrence.substring(start_week);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return recurrence;
	}
	
	public static String getRecurrenceBylocation(int lineH,int y){

		String recurrence="0000000";
		try {
			int line_num=y/lineH;
			if(line_num>=7)
				line_num=6;
			
			return recurrence.substring(0, line_num)+"1"+recurrence.substring(line_num+1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return recurrence;
	}

	private static int minsInDaily=1440;
	public static void initBlockBylocation(BlockUIEntity block, float mins_w, int x, int lineH, int y, Calendar calendar){
		
		int line_num=y/lineH;
		String recurrence="0000000";
		block.recurrence=recurrence.substring(0, line_num)+"1"+recurrence.substring(line_num+1);
		calendar.add(Calendar.DAY_OF_WEEK, line_num+1);
		block.startDate =block.endDate =  calendar.getTime();

		int startTotalMins=(int) (x/mins_w)-block.duration/2;
		startTotalMins=startTotalMins<0?0:startTotalMins;
		int endTotalMins=startTotalMins+block.duration;
		if(endTotalMins>=minsInDaily){
			startTotalMins=startTotalMins-(endTotalMins-minsInDaily+1);
			endTotalMins=minsInDaily-1;
		}
		block.startTime=new Date(1970, 1, 1, 0, startTotalMins, 0);
		block.endTime=new Date(1970, 1, 1, 0, endTotalMins, 0);
		
	}
	
	public static Boolean checkBlockRecurrence(BlockUIEntity block, int weekIndex){
		if(block.recurrence==null||block.recurrence.isEmpty())
			return false;
		
		if(Integer.parseInt(block.recurrence.substring(weekIndex, weekIndex+1))!=1)
			return false;

		return true;
	}
	
	public static int getDurtion(Date startTime,Date endTime) {
		return (endTime.getHours()-startTime.getHours())*60+(endTime.getMinutes()-startTime.getMinutes());
	}
	
	public static int getBlockLeftMargin(Date startTime,float per_mins_w) {
		return (int) ((startTime.getHours()*60+startTime.getMinutes())*per_mins_w);
	}
	
	public static void getBlockTime(BlockUIEntity block, float per_mins_w, int leftMargin) {
		
		int totalMins=(int) (leftMargin/per_mins_w);
		int start_h=totalMins/60;
		int start_m=totalMins%60;
		
		block.startTime=new Date(1970, 1, 1, start_h, start_m, 0);
		block.endTime=new Date(1970, 1, 1, start_h, start_m+block.duration, 0);
	}
	
	public static List<BlockUIEntity> getBlockByDateRange(List<BlockUIEntity> all_block_list, Date startDate, Date endDate) {
		List<BlockUIEntity> block_list=new ArrayList<BlockUIEntity>();
		
		for (BlockUIEntity blockEntity : all_block_list) {
			if(blockEntity.startDate!=null&&blockEntity.startDate.compareTo(endDate)>0)
				continue;
			if(blockEntity.endDate!=null&&blockEntity.endDate.compareTo(startDate)<0)
				continue;
			
			block_list.add(blockEntity);
		}
		
		return block_list;
	}

	public static int getWeekOfDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(week<1)
			week=7;
		return week;
	}

	public static Calendar getFirstdayOfWeek(Date date){
		Calendar calendar = Calendar.getInstance();
		date=new Date(date.getYear(), date.getMonth(), date.getDate());

		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(week<1)
			week=7;
		calendar.add(Calendar.DAY_OF_YEAR, -week+1);
		return calendar;
	}
}
