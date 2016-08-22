package com.mfusion.player.common.Task.PBUAudio;

import java.util.Hashtable;

import com.mfusion.player.common.Entity.Components.AudioComponent;
import com.mfusion.player.common.Player.MainActivity;


import com.mfusion.player.library.Callback.MyCallInterface;

public class PBUAudioCallerResult implements MyCallInterface{

	@Override
	public Object fuc(Object paras) {
		// TODO Auto-generated method stub
		if (paras == null)
			return null;
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> pars=(Hashtable<String, Object>)paras;
		if (pars.containsKey("Audios"))
		{
			Object[] obj=(Object[]) pars.get("Audios");
			String  id = (String) (obj[0]);
			AudioComponent audioCmp=(AudioComponent) obj[2];
			if(id.equalsIgnoreCase(MainActivity.Instance.PBUDispatcher.m_playing_pbu.ID))
			{
				audioCmp.mHandler.sendEmptyMessage(0);
			}
		}
		return false;
	}

}
