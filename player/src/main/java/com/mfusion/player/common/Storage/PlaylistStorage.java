package com.mfusion.player.common.Storage;
import java.util.Hashtable;

import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Playlist;

public class PlaylistStorage {
	public static Hashtable<Integer,Playlist> playlistmap;

	public static Playlist GetPlaylist(int id) {
		Playlist playlist = null;
		try {
			if(playlistmap!=null&&playlistmap.size()>0&&playlistmap.containsKey(id))
			{
				playlist=playlistmap.get(id);	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LoggerHelper.WriteLogfortxt("PlaylistStorage GetPlaylist==>"+e.getMessage());
		}
		return playlist;
	}
}
