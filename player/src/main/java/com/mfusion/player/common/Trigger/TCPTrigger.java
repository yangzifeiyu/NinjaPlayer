package com.mfusion.player.common.Trigger;

import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.PBUStorage;


public class TCPTrigger extends VirtualTrigger{

	@Override
	public void InitTrigger() {
		// TODO Auto-generated method stub
		this.decodeTriggerAction();
	}

	@Override
	public void StartTrigger() {
		// TODO Auto-generated method stub
		if (actionType == TriggerActionType.App)
		{
			if (MainActivity.Instance.PBUDispatcher.triggerOtherApp(interval, true, this) == false)
			{
				return;
			}
		}
		if (actionType == TriggerActionType.PBU)
		{
			PBU pbu=PBUStorage.GetPBUEntity((String) triggerAction);
			if(pbu!=null)
			{
				MainActivity.Instance.PBUDispatcher.triggerPBU(pbu, interval,this);
			}
			return;
		}


		StartTriggerForApp();
	}

	@Override
	public void StopTrigger() {
		// TODO Auto-generated method stub
		 this.StopTriggerForApp();
	}

	@Override
	public void DisposeTrigger() {
		// TODO Auto-generated method stub

	}

}
