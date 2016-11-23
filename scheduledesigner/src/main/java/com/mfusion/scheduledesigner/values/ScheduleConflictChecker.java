package com.mfusion.scheduledesigner.values;

import com.mfusion.scheduledesigner.entity.BlockUIEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Guoyu on 2016/11/1.
 */
public class ScheduleConflictChecker {
    public static Boolean isBlockConflict(List<BlockUIEntity> all_block_list, BlockUIEntity checkedBlock) {
        try {
            if (all_block_list.size() == 0)
                return false;

            for (BlockUIEntity sourceBlock : all_block_list) {
                if(sourceBlock==checkedBlock)
                    continue;
                //compare block play time on daily
                if (sourceBlock.startTime.compareTo(checkedBlock.endTime) > 0 || sourceBlock.endTime.compareTo(checkedBlock.startTime) < 0)
                    continue;
                //compare block play date(play date not overlap)
                if ((checkedBlock.endDate != null && sourceBlock.startDate.compareTo(checkedBlock.endDate) > 0) || (sourceBlock.endDate != null && sourceBlock.endDate.compareTo(checkedBlock.startDate) < 0))
                    continue;

                //get the date range of blocks overlap
                Date range_start = checkedBlock.startDate.compareTo(sourceBlock.startDate) > 0 ? checkedBlock.startDate : sourceBlock.startDate;
                Date range_end = null;
                if (checkedBlock.endDate == null)
                    range_end = sourceBlock.endDate;
                else if (sourceBlock.endDate == null)
                    range_end = checkedBlock.endDate;
                else
                    range_end = checkedBlock.endDate.compareTo(sourceBlock.endDate) <= 0 ? checkedBlock.endDate : sourceBlock.endDate;

                String overlapWeek=getOverlapWeekNum(range_start,range_end);
                for(int week=1;week<=7;week++){
                    if(overlapWeek.substring(week-1,week).equals("1")&&checkedBlock.recurrence.substring(week-1,week).equals("1")&&sourceBlock.recurrence.substring(week-1,week).equals("1"))
                        return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String getOverlapWeekNum(Date startDate,Date endDate){
        String overlapStr="1111111";
        if(endDate!=null){
            int[] weekArray=new int[7];
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(startDate);
            while (calendar.getTime().compareTo(endDate)<=0){
                int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
                if(week<1)
                    week=7;
                //overlap more than a week
                if(weekArray[week-1]==1)
                    return overlapStr;

                weekArray[week-1]=1;
                calendar.add(Calendar.DAY_OF_YEAR,1);
            }
            StringBuilder overlapBuilder=new StringBuilder();
            for (int overlapStatus : weekArray)
                overlapBuilder.append(String.valueOf(overlapStatus));
            return overlapBuilder.toString();
        }
        return overlapStr;
    }
}
