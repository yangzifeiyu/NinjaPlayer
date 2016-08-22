/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 * ScheduleMedia Setting
 */
package com.mfusion.player.common.Setting.Component;


import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Storage.PlaylistStorage;


public class ScheduleMediaSetting {
	//public List<TimelinePlaylist> TimelinePlaylist;//playlist timlines
	public Playlist Idleplaylist;                  //default playlist
	//public List<Playlist> PlaylistList;             //playlist list
	public boolean Mute;                             //mute
	private int idleplaylistId;

	public int getIdleplaylistId() {
		return idleplaylistId;
	}

	public void setIdleplaylistId(int idleplaylistId)
	{
		this.idleplaylistId = idleplaylistId;
		this.Idleplaylist = PlaylistStorage
				.GetPlaylist(idleplaylistId);
	}
	
/*	private int plgroupId;

	public int getPlgroupId() {
		return plgroupId;
	}

	public void setPlgroupId(int plgroupId) {
		this.plgroupId = plgroupId;
		Plgroup  plgroup =PLGroupStorag.GetPlgroup(plgroupId);
		this.Idleplaylist=PlaylistStorage
				.GetPlaylist(plgroup.IdleId);
		this.TimelinePlaylist=plgroup.timelineplayList;
	}*/
}
