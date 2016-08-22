package com.mfusion.player.common.XML.Parser;

import com.mfusion.player.common.Storage.MediaStorage;
import com.mfusion.player.common.Storage.PBUStorage;
import com.mfusion.player.common.Storage.PLGroupStorag;
import com.mfusion.player.common.Storage.PlaylistStorage;
import com.mfusion.player.common.XML.MediaXML;
import com.mfusion.player.common.XML.PBUXML;
import com.mfusion.player.common.XML.PlaylistXML;
import com.mfusion.player.common.XML.PlgroupXML;

public class WindowsXmlParser extends BaseXmlParser{
	
	private MediaXML mediaXml;      //����medias
	private PlaylistXML playlistXML; //����playlist
	private PlgroupXML plgroupXML;   //����playlistgroup
	private PBUXML pbuXml;
	
	public WindowsXmlParser() {
		super();
		// TODO Auto-generated constructor stub
		this.mediaXml=new MediaXML();
		this.playlistXML=new PlaylistXML();
		this.plgroupXML=new PlgroupXML();
		this.pbuXml=new PBUXML();
	}

	@Override
	protected void parseXml() {
		// TODO Auto-generated method stub

		MediaStorage.mediamap=mediaXml.GetMediaMap();             //��ȡmedialist
		PlaylistStorage.playlistmap=playlistXML.GetPlaylistMap(); //��ȡplaylist list
		PLGroupStorag.plgroupmap=plgroupXML.GetGLMap();            //��ȡplgroup list
		PBUStorage.pbumap=pbuXml.GetPBUList();   
		
	}

}
