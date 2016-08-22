package com.mfusion.scheduledesigner.values;

import com.mfusion.commons.entity.schedule.BlockEntity;
import com.mfusion.commons.entity.schedule.Schedule;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.scheduledesigner.entity.BlockUIItemEntity;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;

import java.util.List;

/**
 * Created by ThinkPad on 2016/7/21.
 */
public class ScheduleDataConverter {

    public static Schedule convertToSave(Schedule schedule, List<BlockUIEntity> blockUIEntityList){
        try {

            if(blockUIEntityList!=null&&!blockUIEntityList.isEmpty()){
                BlockEntity block=null;
                for (BlockUIEntity blockUI:blockUIEntityList) {
                    block=new BlockEntity();
                    block.blockType=blockUI.blockType;
                    block.startDate=blockUI.startDate;
                    block.endDate=blockUI.endDate;
                    block.startTime=blockUI.startTime;
                    block.endTime=blockUI.endTime;
                    block.isRecurrence=blockUI.isRecurrence;
                    block.recurrence=blockUI.recurrence;
                    for (BlockUIItemEntity itemUI:blockUI.itemList) {
                        block.itemList.add(itemUI.blockItemName);
                    }

                    schedule.blockList.add(block);
                }
            }

            return schedule;
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static void convertToDisplay(Schedule schedule,List<BlockUIEntity> blockUIEntityList){
        try {
            blockUIEntityList.clear();

            BlockUIEntity blockUI=null;
            BlockUIItemEntity blockItemUI=null;
            for (BlockEntity block:schedule.blockList) {
                blockUI=new BlockUIEntity();
                blockUI.blockType=block.blockType;
                blockUI.startDate=block.startDate;
                blockUI.endDate=block.endDate;
                blockUI.startTime=block.startTime;
                blockUI.endTime=block.endTime;
                blockUI.duration= ScheduleDrawHelper.getDurtion(block.startTime, block.endTime);
                blockUI.isRecurrence=block.isRecurrence;
                blockUI.recurrence=block.recurrence;

                for (String itemName:block.itemList) {
                    blockItemUI=new BlockUIItemEntity();
                    blockItemUI.blockItemName=itemName;

                    blockUI.itemList.add(blockItemUI);
                }
                blockUIEntityList.add(blockUI);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
