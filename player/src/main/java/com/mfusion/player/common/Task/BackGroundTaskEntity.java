
/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *
 */
package com.mfusion.player.common.Task;

import java.util.Hashtable;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.library.Helper.LoggerHelper;



public class BackGroundTaskEntity {
	public Caller result;
	public Caller condition;
	public Hashtable<String,Object> Paras;

	public boolean TaskExecute()
	{
		try{
			boolean conditionAccord = Boolean.parseBoolean(this.condition.call(this.Paras).toString());
			if (conditionAccord)
			{
				this.result.call(this.Paras);
				if (this.Paras != null)
					this.Paras.clear();
			}
			return conditionAccord;
		}
		catch(Exception ex)
		{
			//LoggerHelper.WriteLogfortxt("BackGroundTaskEntity==>"+ex.getMessage());
			ex.printStackTrace();		
			return false;
		}
	}
}
