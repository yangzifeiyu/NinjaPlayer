package com.mfusion.player.common.Task.PBUAudio;

import java.util.Hashtable;
import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Helper.FileHelper;


public class PBUAudioCallerCondition implements MyCallInterface{

	@SuppressWarnings("unchecked")
	@Override
	public Object fuc(Object paras) {
		
			boolean result=false;
			if (paras == null)
				return false;
	
			Hashtable<String,Object> pars=(Hashtable<String, Object>)paras;
			if (pars.containsKey("Audios"))
			{
				Object[] obj=(Object[]) pars.get("Audios");
				MediaFile audio = (MediaFile)(obj[1]);

				String mediaSourcePath = audio.FilePath.replace("\\", "/");
				String storage=PlayerStoragePath.AudioStorage;
				mediaSourcePath=storage+mediaSourcePath;
				if(FileHelper.IsExists(mediaSourcePath))
				{
					result= true;
				}

			}
			return result;
		
	}

}
