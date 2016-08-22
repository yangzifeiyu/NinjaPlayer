package com.mfusion.player.common.Trigger;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.PBUStorage;

public class MouseTrigger extends VirtualTrigger implements MyCallInterface{

	@Override
	public void InitTrigger() {
		// TODO Auto-generated method stub
		this.decodeTriggerAction();
		Caller caller=new Caller();
		caller.setI(this);
		MainActivity.Instance.PBUMouseDown=caller;

	}

	@Override
	public void StartTrigger() {
		// TODO Auto-generated method stub
		if (actionType == TriggerActionType.App)
		{
			if (MainActivity.Instance.PBUDispatcher.triggerOtherApp(interval, true, this))
			{
				if(StartTriggerForApp()==false)
                    this.StopTrigger();
			}
		}
		else if (actionType == TriggerActionType.PBU)
		{
			PBU pbu=PBUStorage.GetPBUEntity((String) triggerAction);
			if(pbu!=null)
			{
				MainActivity.Instance.PBUDispatcher.triggerPBU(pbu, interval,this);
			}
		}
		else
			LoggerHelper.WriteLogfortxt("StartTrigger ==> Neither App nor PBU.");
	}

	@Override
	public void StopTrigger() {
		// TODO Auto-generated method stub
		try
		{
			 this.StopTriggerForApp();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MouseTrigger StopTrigger==>"+ex.getMessage());
		}
	}

	@Override
	public void DisposeTrigger() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object fuc(Object paras) {
		// TODO Auto-generated method stub
		this.StartTrigger();
		return null;
	}

}
