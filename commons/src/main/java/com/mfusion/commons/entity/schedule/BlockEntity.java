package com.mfusion.commons.entity.schedule;

import com.mfusion.commons.entity.values.BlockType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jimmy on 7/12/2016.
 */
public class BlockEntity {
    public BlockType blockType= BlockType.Single;

    public Date startDate;

    public Date endDate;

    public Date startTime;

    public Date endTime;

    public boolean isRecurrence=false;

    public String recurrence="0000000";

    /*
     * @Description Consists of one or more template
     */
    public List<String> itemList=new ArrayList<String>();
}
