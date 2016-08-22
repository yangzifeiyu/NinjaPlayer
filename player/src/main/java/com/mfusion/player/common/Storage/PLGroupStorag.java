package com.mfusion.player.common.Storage;

import java.util.Hashtable;

import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Entity.Plgroup;


public class PLGroupStorag {
	public static Hashtable<Integer,Plgroup> plgroupmap;
	public static Plgroup GetPlgroup(int id) {
		Plgroup plgroup = null;
		try {
			if(plgroupmap!=null&plgroupmap.size()>0&&plgroupmap.containsKey(id))
			{
				plgroup=plgroupmap.get(id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("PLGroupStorag GetPlgroup==>"+e.getMessage());
		}
		return plgroup;
	}


}
