package com.mfusion.player.common.MathPlaylist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Player.MainActivity;

public class PlaylistCollectionHelper {
	public static Date logicStart;
	public static List<Playlist> collection;

	public static void  getIntervalCollection(
			List<Playlist> blockUnit, Date start, Date end) {
		try {
			if (blockUnit == null || blockUnit.size() <= 0)
				return;
			
	
			collection = new ArrayList<Playlist>();
			Date currentTime = MainActivity.Instance.Clock.Now;
			Date cursor = start;// block��unit��StartTime
			int j = 0;// block�е�һ�����ŵ�Unit
			int totalDuration = 0;
			int size=blockUnit.size();
			for (int i = 0; i <size ; i++) {
				totalDuration += blockUnit.get(i).Duration;
			}
			if (currentTime.compareTo(start) > 0) {
				logicStart = currentTime;
				j = FirstPlayPlaylist(blockUnit, start, currentTime, totalDuration);
				if (j >= blockUnit.size())
					j = 0;
				cursor = logicStart;
				for (; j < blockUnit.size(); j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					
					Playlist playlist =blockUnit.get(j);
					Playlist pl=(Playlist) playlist.clone();
					
					pl.StartTime = cursor;
					if (tem.compareTo(end) >= 0) {
						pl.EndTime = end;
						collection.add(pl);
						if (collection.size() > 0)
							collection.get(0).StartTime = currentTime;
						MergePlaylistHelper.MergePlaylistUnit(collection);
						collection=MergePlaylistHelper.playlistcollection;
						return;
					} else {
						pl.EndTime = cursor = tem;
						collection.add(pl);
					}
				}
			}
			
			
			if (collection.size() > 0)
				collection.get(0).StartTime = currentTime;
			int num = DateTimeHelper.GetDuration(end, cursor) / totalDuration;
			for (int i = 0; i < num; i++) {
				for (j = 0; j < size; j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					Playlist playlist = blockUnit.get(j);
					Playlist pl=(Playlist) playlist.clone();
					Date st=new Date(cursor.getTime());
					pl.StartTime = st;
					pl.EndTime =  tem;
					collection.add(pl);
					cursor=new Date(tem.getTime());
				}
			}
			if (cursor != end) {
				for (j = 0; j < size; j++) {
					Date tem = DateTimeHelper.GetAddedDate(cursor,
							blockUnit.get(j).Duration,MainActivity.Instance.PlayerSetting.Timezone);
					Playlist playlist = blockUnit.get(j);
					Playlist pl=(Playlist) playlist.clone();
					Date st=new Date(cursor.getTime());
					pl.StartTime = st;
					if (tem.compareTo(end) >= 0) {
						pl.EndTime = end;
						collection.add(pl);
						break;
					} else {
						pl.EndTime = tem;
						collection.add(pl);
						cursor=new Date(tem.getTime());
					}
				}
			}
			MergePlaylistHelper.MergePlaylistUnit(collection);
			collection=MergePlaylistHelper.playlistcollection;
			

		} catch (Exception ex) {
			
		}
		
	}
//start:timeline start,current:now 
	private static int FirstPlayPlaylist(List<Playlist> col, Date start,
			Date currentTime, int totalDuration) {
		Date cursor = start;
		
		int num = DateTimeHelper.GetDuration(currentTime, cursor)
				/ totalDuration;
		cursor = DateTimeHelper.GetAddedDate(cursor, num * totalDuration,MainActivity.Instance.PlayerSetting.Timezone);

		int startnum = 0;
		if (cursor != currentTime) {
			int size=col.size();
			for (int i = 0; i < size; i++) {
				logicStart = cursor;
				Date tem = DateTimeHelper.GetAddedDate(cursor,
						col.get(i).Duration,MainActivity.Instance.PlayerSetting.Timezone);
				if (tem.compareTo(currentTime) > 0) {
					startnum = i;
					break;
				} else if (tem == currentTime) {
					startnum = ++i;
					break;
				} else
					cursor = tem;
			}
		}
		return startnum;
	}

}
