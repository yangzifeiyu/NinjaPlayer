/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *TempBackImage�ص�ִ�з���������
 */
package com.mfusion.player.common.Task.TemplateBackImage;

import java.util.Hashtable;
import com.mfusion.player.library.Callback.MyCallInterface;
import com.mfusion.player.library.Helper.FileHelper;



public class TempBackImageCallerCondition implements MyCallInterface {

	@SuppressWarnings("unchecked")
	@Override
	public Object fuc(Object paras) {

		boolean result=false;
		if (paras == null)
			return false;
		Hashtable<String,Object> pars=(Hashtable<String, Object>)paras;
		if (pars.containsKey("BackPhotoPath"))
		{
			String path = pars.get("BackPhotoPath").toString();
			if (FileHelper.IsExists(path))
				result= true;
		}
		return result;
	}


}
