/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *TempBackImage�ص�ִ�з���
 */
package com.mfusion.player.common.Task.TemplateBackImage;

import java.util.Hashtable;

import com.mfusion.player.library.Callback.MyCallInterface;

import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class TempBackImageCallerResult implements MyCallInterface {


	@Override
	public Object fuc(Object paras) {

		if (paras == null)
			return null;
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> pars=(Hashtable<String, Object>)paras;
		if (pars.containsKey("BackPhotoPath"))
		{
			String path = pars.get("BackPhotoPath").toString();
			String mediaSourcePath =MainActivity.Instance.PBUDispatcher.m_playing_pbu.Template.BackMediaFile.FilePath.replace("\\", "/");
			String npath=PlayerStoragePath.ImageStorage+mediaSourcePath;
			if (npath.equals(path))
			{
				MainActivity.Instance.PBUDispatcher.mHandler.sendEmptyMessage(0);
			}
		}
		return null;

	}


}
