/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *����plgroup
 */
package com.mfusion.player.common.XML;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.w3c.dom.Element;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Plgroup;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Entity.TimelinePlaylist;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.PlaylistStorage;

public class PlgroupXML {
	private XMLHelper xmlHelper;
	private Date currentTime;// player������ʱ��
	private String date;
	private String time;
	private int dayofweek;
	public Hashtable<Integer,Plgroup> plgroupmap;

	public PlgroupXML() {
		try
		{
			xmlHelper = new XMLHelper();
			plgroupmap = new Hashtable<Integer,Plgroup>();
			currentTime = MainActivity.Instance.Clock.Now;
			date = DateTimeHelper.ConvertToString(currentTime, 1);
			time = DateTimeHelper.ConvertToString(currentTime, 2);
			dayofweek = DateTimeHelper.GetDayOfWeek(currentTime);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("PlgroupXML=="+ex.getMessage());
		}
	}
	/*
	 * ��ȡplgroup
	 */
	public  Hashtable<Integer,Plgroup> GetGLMap()
	{
		try
		{
			if(xmlHelper!=null)
			{
				// playgroups
				Element plgroupelement = xmlHelper.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\PLGroups");
				if(plgroupelement==null)
					return this.plgroupmap;
				Element[] plgroups = xmlHelper
						.getElementsByParentElement(plgroupelement);

				for (int i = 0; i < plgroups.length; i++) {
					Plgroup plgroup = new Plgroup();
					Element element = plgroups[i];
					String plid = element.getAttribute("Id");
					plgroup.plgroupId = Integer.parseInt(plid);
					Element timeline = xmlHelper
							.getElementsByParentElement(element)[0];
					if(timeline==null)
						continue;
					Element[] lines = xmlHelper
							.getElementsByParentElement(timeline);
					List<TimelinePlaylist> timelinelist = new ArrayList<TimelinePlaylist>();
					if(lines!=null)
					{
						for (int j = 0; j < lines.length; j++) {
							TimelinePlaylist timelineplaylist = new TimelinePlaylist();
							Element line = lines[j];
							if (line.getTagName().equals("IdlePlaylist")) {
								String id = line.getAttribute("Id");
								plgroup.IdleId = Integer.parseInt(id);
							} else {
								String enddate = line.getAttribute("EndDate");
								String startdate = line.getAttribute("StartDate");
								String endtime = line.getAttribute("EndTime");
								String starttime = line.getAttribute("StartTime");
								String recurrence = line.getAttribute("Recurrence");
								if (startdate.compareTo(date) <= 0
										&& endtime.compareTo(time) >= 0
										&& (enddate.equals("0001,01,01")!=false || enddate.compareTo(date) >= 0)) {

									if ((recurrence.equals("") && startdate.equals(date))
											|| (!recurrence.equals("") && (recurrence
													.charAt(dayofweek) == '1'))) {
										timelineplaylist.EndDate = enddate;
										timelineplaylist.StartDate = startdate;
										timelineplaylist.EndTime = endtime;
										timelineplaylist.StartTime = starttime;

										Element[] lists = xmlHelper
												.getElementsByParentElement(line);
										List<Playlist> playlistList = new ArrayList<Playlist>();

										for (int k = 0; k < lists.length; k++) {

											Element list = lists[k];
											String listindex = list
													.getAttribute("Index");
											String listid = list.getAttribute("Id");
											int id=CommonConvertHelper.StringToInt(listid);
											Playlist playlist=PlaylistStorage.GetPlaylist(id);
											if(playlist!=null)
											{
												playlist.Index = Integer
														.parseInt(listindex);
												playlistList.add(playlist);
											}
										}
										timelineplaylist.playlistList = playlistList;
										timelinelist.add(timelineplaylist);
									}

								}

							}


						}
					}
					plgroup.timelineplayList=timelinelist;
					if(!plgroupmap.containsKey(plgroup.plgroupId))
						plgroupmap.put(plgroup.plgroupId,plgroup);
				}
			}




		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlgroupXML GetGLList=="+ex.getMessage());
		}
		return this.plgroupmap;
	}
}
