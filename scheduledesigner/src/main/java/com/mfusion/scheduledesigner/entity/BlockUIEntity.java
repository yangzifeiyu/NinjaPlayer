package com.mfusion.scheduledesigner.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mfusion.commons.entity.values.BlockType;

public class BlockUIEntity {

	//0:single,1:interval
	public BlockType blockType=BlockType.Single;

	public Date startDate;

	public Date endDate;
	
	public Date startTime;

	public Date endTime;
	
	public boolean isRecurrence=false;
	
	public String recurrence="0000000";
	
	public int duration=120;
	
	public List<BlockUIItemEntity> itemList=new ArrayList<BlockUIItemEntity>();
}
