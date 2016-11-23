package com.mfusion.scheduledesigner.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mfusion.commons.entity.values.BlockType;
import com.mfusion.commons.tools.DateConverter;

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

	@Override
	public String toString(){
		if(itemList.size()>0&&startTime!=null&&endTime!=null)
			return DateConverter.convertShortTimeToStr(startTime)+"-"+DateConverter.convertShortTimeToStr(endTime)+" : "+itemList.get(0).blockItemName;
		return "";
	}

	public BlockUIEntity clone(){
		BlockUIEntity cloneEntity=new BlockUIEntity();
		cloneEntity.startTime=(Date) this.startTime.clone();
		cloneEntity.endTime=(Date) this.endTime.clone();
		cloneEntity.startDate=(Date) this.startDate.clone();
		cloneEntity.endDate=this.endDate==null?null:(Date) this.endDate.clone();
		cloneEntity.isRecurrence=this.isRecurrence;
		cloneEntity.recurrence=this.recurrence;
		cloneEntity.duration=this.duration;

		return cloneEntity;
	}

	public void copy(BlockUIEntity cloneEntity){
		this.startTime=cloneEntity.startTime;
		this.endTime=cloneEntity.endTime;
		this.startDate=cloneEntity.startDate;
		this.endDate=cloneEntity.endDate;
		this.isRecurrence=cloneEntity.isRecurrence;
		this.recurrence=cloneEntity.recurrence;
		this.duration=cloneEntity.duration;
	}
}
