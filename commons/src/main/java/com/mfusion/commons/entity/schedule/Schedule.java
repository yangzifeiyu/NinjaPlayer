package com.mfusion.commons.entity.schedule;

import com.mfusion.commons.entity.values.SchedulePlayType;

import java.util.ArrayList;

/**
 * Created by jimmy on 7/12/2016.
 */
public class Schedule {

    public String id="default";

    public String idleItem;

    public SchedulePlayType playType=SchedulePlayType.TimeLine;

    public ArrayList<BlockEntity> blockList=new ArrayList<BlockEntity>();
}
