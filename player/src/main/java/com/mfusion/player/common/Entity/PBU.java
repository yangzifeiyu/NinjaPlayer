package com.mfusion.player.common.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mfusion.player.common.Entity.Components.BasicComponent;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Trigger.VirtualTrigger;

public class PBU implements Cloneable, Comparable<PBU>{
	public Integer Index;
	public String ID;
	public int Duration = 0; //PBU��ʱ�䳤�ȣ���λ����
	public int OriginalDuration = 0; //PBU��ʱ�䳤�ȣ���λ����
	public Template Template; //ʹ�õ�ģ��
	public ArrayList<BasicComponent> Components;
	public Date StartTime;
	public Date EndTime;
	public String TriggerContent;
	public List<VirtualTrigger> internalTriggers;
	public boolean CursorEnabled=false;
	public boolean isTriggered = false;
	public TriggerActionType pbuTriggerAction=TriggerActionType.App;
	public boolean isResetTime=false;
	@Override  
	public Object clone() throws CloneNotSupportedException  
	{  
		PBU pl = new PBU();
		// �����õĶ���teacherҲclone��  
		pl.Index=Index;
		pl.Duration=Duration;
		pl.OriginalDuration=OriginalDuration;
		pl.ID=ID;
		pl.Template=Template;
		pl.StartTime=StartTime;
		pl.EndTime=EndTime;
		pl.Components=Components;
		pl.TriggerContent=TriggerContent;
		pl.CursorEnabled=CursorEnabled;
		pl.internalTriggers=internalTriggers;
		return pl;  
	}
	@Override
	public int compareTo(PBU another) {
		// TODO Auto-generated method stub
		return this.Index.compareTo(another.Index);
	}  
}
