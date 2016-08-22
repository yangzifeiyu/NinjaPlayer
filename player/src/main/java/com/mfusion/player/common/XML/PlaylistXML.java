/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *����playlist
 */
package com.mfusion.player.common.XML;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Element;

import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Enum.PlayMode;
import com.mfusion.player.common.Storage.MediaStorage;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class PlaylistXML {
	private XMLHelper xmlHelper;

	private Hashtable<Integer,Playlist> playlistmap;

	public PlaylistXML() {
		try
		{
			this.playlistmap = new Hashtable<Integer,Playlist>();
			this.xmlHelper = new XMLHelper();

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlaylistXML==>"+ex.getMessage());
		}
	}

	/*
	 * ��ȡplaylist list
	 */
	public Hashtable<Integer,Playlist> GetPlaylistMap() {
		try {

			if (xmlHelper != null) {

				Element display_playlist_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\Playlists");
				if(display_playlist_element==null)
					return this.playlistmap;

				Element[] playlists = xmlHelper
						.getElementsByParentElement(display_playlist_element);
				for (int i = 0; i < playlists.length; i++) {
					Playlist playlist = new Playlist();
					Element element = playlists[i];
					String playlistid = element.getAttribute("Id");
					String Playmode = element.getAttribute("PlayMode");
					playlist.ID = Integer.parseInt(playlistid);
					playlist.Mode = PlayMode.fromString(Playmode);
					Element[] medias = xmlHelper
							.getElementsByParentElement(element);
					List<MediaFile> medialist = new ArrayList<MediaFile>();
					int duration=0;
					for (int j = 0; j < medias.length; j++) {

						Element media = medias[j];
						String mediaId = media.getAttribute("Id");
						String mediaDuration = media.getAttribute("Duration");
						String mediaEffect = media.getAttribute("Effect");
						MediaFile mediafile=MediaStorage.GetMediaFile(CommonConvertHelper.StringToInt(mediaId));
						if(mediafile!=null)//����mediafile��Ϊ�յ��ж�
						{
							mediafile.MediaId = Integer.parseInt(mediaId);
							mediafile.Duration = DateTimeHelper
									.ConvertToMin(mediaDuration);
							duration+=mediafile.Duration;
							mediafile.Effect = mediaEffect;
							medialist.add(mediafile);
						}
					}
					playlist.Duration=duration;
					playlist.Medias = medialist;
					if(!playlistmap.containsKey(playlist.ID))
						playlistmap.put(playlist.ID,playlist);
				}
			}

		} catch (Exception ex) {
			LoggerHelper.WriteLogfortxt("PlaylistXML GetPlaylistlist==>"+ex.getMessage());
		}
		return this.playlistmap;

	}
}
