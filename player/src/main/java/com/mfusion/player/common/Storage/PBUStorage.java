package com.mfusion.player.common.Storage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mfusion.player.library.Helper.LoggerHelper;

import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Entity.Components.AudioComponent;
import com.mfusion.player.common.Entity.Components.BasicComponent;
import com.mfusion.player.common.Entity.Components.ScheduleMediaComponent;
import com.mfusion.player.common.Enum.ComponentType;
import com.mfusion.player.common.Enum.ServerType;
import com.mfusion.player.common.Player.MainActivity;

public class PBUStorage {

	public static Hashtable<String, PBU> pbumap = new Hashtable<String, PBU>();

	public static PBU GetPBUEntity(String id) {
		PBU pbuEntity = null;
		try {
			if(pbumap!=null&&pbumap.size()>0&&pbumap.containsKey(id))
			{
				pbuEntity = pbumap.get(id);
			}

		} catch (Exception e) {
			LoggerHelper.WriteLogfortxt("PBUStorage GetPBUEntity==>"+e.getMessage());
		}
		return pbuEntity;
	}

	public static int GetPBUDuration(PBU pbu) {
		// TODO Auto-generated method stub
		int duration = 0;
		ArrayList<BasicComponent> comlist = pbu.Components;
		int size=comlist.size();
		int max=0;
		for ( int i=0;i<size;i++) 
		{
			BasicComponent comp=comlist.get(i);
			if(comp!=null)
			{
				if (comp.componentType.equals(ComponentType.ScheduleMedia))
				{
					ScheduleMediaComponent schedulemedia = (ScheduleMediaComponent) comp;
					Playlist playlist=schedulemedia.setting.Idleplaylist;
					duration=playlist.Duration;
					max=duration>max?duration:max;//��ȡ����duration
				}
			}
		}
		return max;
	}

	public static List<Integer> GetPbuMediaFiles(String id) {
		// TODO Auto-generated method stub

		List<Integer> list=new ArrayList<Integer>();
		PBU pbu=GetPBUEntity(id);
		if(pbu==null)
			return list;
		if((pbu.Template.BackMediaId>=0&&!list.contains(pbu.Template.BackMediaId))||(pbu.Template.BackMediaFile!=null&&!pbu.Template.BackMediaFile.MediaSource.equalsIgnoreCase("local")))
		{
			list.add(pbu.Template.BackMediaId);
		}
		ArrayList<BasicComponent> comlist=pbu.Components;
		if(comlist!=null)
		{
			int size=comlist.size();
			for(int i=0;i<size;i++)
			{
				BasicComponent com=comlist.get(i);
				if(com!=null)
				{
					if(com.componentType.equals(ComponentType.ScheduleMedia))
					{
						ScheduleMediaComponent schcom=(ScheduleMediaComponent)com;
						if(schcom.setting.Idleplaylist!=null)
						{
							
							List<MediaFile>files=schcom.setting.Idleplaylist.Medias;
							for(MediaFile media:files)
							{
								if(!media.MediaSource.equalsIgnoreCase("local")&&!list.contains(media.MediaId))
								{
									list.add(media.MediaId);
								}
							}
						}
					}
					else if(com.componentType.equals(ComponentType.AudioComponent))
					{
						AudioComponent audiocom=(AudioComponent)com;
						for(MediaFile media:audiocom.setting.AudioList)
						{
							if(!media.MediaSource.equalsIgnoreCase("local")&&!list.contains(media.MediaId))
							{
								list.add(media.MediaId);
							}
						}
					}
				}
			}
		}

		return list;
	}

}
