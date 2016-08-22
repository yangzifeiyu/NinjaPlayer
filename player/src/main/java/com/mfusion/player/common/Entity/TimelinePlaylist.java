package com.mfusion.player.common.Entity;

import java.util.Date;
import java.util.List;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.MathPlaylist.PlaylistCollectionHelper;
import com.mfusion.player.common.Player.MainActivity;

public class TimelinePlaylist extends Timeline {
	public List<Playlist> playlistList;
	

	public void GetPlaylistCollection() {
		// TODO Auto-generated method stub
		try {
			Date startdate=DateTimeHelper.ConvertToDateTime(this.StartDate, this.StartTime,MainActivity.Instance.PlayerSetting.Timezone);
			Date enddate=DateTimeHelper.ConvertToDateTime(this.EndDate, this.EndTime,MainActivity.Instance.PlayerSetting.Timezone);
			PlaylistCollectionHelper.getIntervalCollection(playlistList, startdate, enddate);
			this.playlistList =PlaylistCollectionHelper.collection ;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("TimelinePBUBlock GetPlaylistCollection==>"+e.getMessage());
		}
	}
}
