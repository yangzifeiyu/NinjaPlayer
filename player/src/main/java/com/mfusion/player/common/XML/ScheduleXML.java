
/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *����Schedule Xml
 */
package com.mfusion.player.common.XML;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Element;

import android.util.Log;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.TimelinePBUBlock;
import com.mfusion.player.common.Enum.ServerType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.MediaStorage;
import com.mfusion.player.common.Storage.PBUStorage;
import com.mfusion.player.common.Storage.PLGroupStorag;
import com.mfusion.player.common.Storage.PlaylistStorage;
import com.mfusion.player.common.Storage.ScheduleStorage;
import com.mfusion.player.common.XML.Parser.BaseXmlParser;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class 	ScheduleXML
{
	private static final String ScheduleXML = "ScheduleXML";
	private MediaXML mediaXml;      //����medias
	private PlaylistXML playlistXML; //����playlist
	private PlgroupXML plgroupXML;   //����playlistgroup
	private PBUXML pbuXml;           //����pbu
	private XMLHelper xmlHelper;     
	private Date currentTime;        //player������ʱ��
	private String date;             
	private String time;
	private int dayofweek;
	public List<TimelinePBUBlock> timelinepbulist;

	/*
	 * ���캯��
	 */
	public ScheduleXML() {
		try
		{
			this.mediaXml=new MediaXML();
			this.playlistXML=new PlaylistXML();
			this.plgroupXML=new PlgroupXML();
			this.pbuXml=new PBUXML();
			this.xmlHelper = new XMLHelper();
			
			this.timelinepbulist = new ArrayList<TimelinePBUBlock>();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ScheduleXML==>"+ex.getMessage());
		}

	}
	/*
	 * ��ȡschedule
	 */
	public ScheduleStorage GetScheduleInfo() 
	{
		this.currentTime =MainActivity.Instance.Clock.Now;
		this.date = DateTimeHelper.ConvertToString(currentTime, 1);
		this.time = DateTimeHelper.ConvertToString(currentTime, 2);
		this.dayofweek = DateTimeHelper.GetDayOfWeek(currentTime);
		
		ScheduleStorage scheduleStorage = new ScheduleStorage();
		try 
		{
			 //��ȡpbu list

			if (xmlHelper != null) {
				
				String updatetime="";
				String servertype="";
				Element player_element = xmlHelper
						.getElementByXPath("MfusionPlayer");
				if(player_element==null)
					return scheduleStorage;
				//����ʱ��
				updatetime=player_element.getAttribute("UpdateTime");
				if (!updatetime.equalsIgnoreCase("")&&!updatetime.isEmpty())
				{
					MainActivity.Instance.PlayerSetting.setUpdateTime(updatetime);
				}
				//����servertype
				servertype=player_element.getAttribute("ServerType");
				if(!servertype.equalsIgnoreCase("")&&!servertype.isEmpty())
				{
					MainActivity.Instance.PlayerSetting.ServerType=ServerType.fromString(servertype);
				}
				
				try {

					(new BaseXmlParser(player_element.getAttribute("XMLVersion"))).initPlayingContent();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				/*MediaStorage.mediamap=mediaXml.GetMediaMap();             //��ȡmedialist
				PlaylistStorage.playlistmap=playlistXML.GetPlaylistMap(); //��ȡplaylist list
				PLGroupStorag.plgroupmap=plgroupXML.GetGLMap();            //��ȡplgroup list
				PBUStorage.pbumap=pbuXml.GetPBUList();   */
				
				Element display_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\");
				if(display_element==null)
					return scheduleStorage;
				String iscompare=display_element.getAttribute("IsComparePBU");
			
				MainActivity.Instance.PBUDispatcher.IsComparePBU=CommonConvertHelper.parseIntToBoolean(iscompare);
				// display_timeline
				Element display_run_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\Run\\");
				if(display_run_element==null)
					return scheduleStorage;
				MainActivity.Instance.PBUDispatcher.m_pbu_based_timeline=CommonConvertHelper.parseIntToBoolean(display_run_element.getAttribute("PlayBasedOn"));
				
				Element[] displaytimelines = xmlHelper
						.getElementsByParentElement(display_run_element);

				for (int i = 0; i < displaytimelines.length; i++) {
					TimelinePBUBlock timelinepbu = new TimelinePBUBlock();
					Element element = displaytimelines[i];
					String name=element.getNodeName();
					if(name.equalsIgnoreCase("timeline"))
					{

						String recurrence = "";
						String enddate = "";
						String startdate = "";
						String endtime = "";
						String starttime = "";
						recurrence = element.getAttribute("Recurrence");
						enddate = element.getAttribute("EndDate");
						startdate = element.getAttribute("StartDate");
						endtime = element.getAttribute("EndTime");
						starttime = element.getAttribute("StartTime");
						if (startdate.compareTo(date) <= 0
								&& endtime.compareTo(time) >= 0
								&& (enddate.equals("0001,01,01")!=false || enddate.compareTo(date) >= 0)) {

							if ((recurrence.equals("") && startdate.equals(date))
									|| (!recurrence.equals("") && (recurrence
											.charAt(dayofweek) == '1'))) {

								timelinepbu.Recurrence = recurrence;

								startdate=enddate=date;
								timelinepbu.EndDate = enddate;
								timelinepbu.StartDate = startdate;
								timelinepbu.StartTime = starttime;
								timelinepbu.EndTime = endtime;

								Element[] pbus = xmlHelper
										.getElementsByParentElement(element);
								ArrayList<PBU> pbulist = new ArrayList<PBU>();

								for (int j = 0; j < pbus.length; j++) {

									Element pbu = pbus[j];
									int index = CommonConvertHelper.StringToInt(pbu.getAttribute("Index"));
									String pbuid = pbu.getAttribute("Id");
									PBU p =PBUStorage.GetPBUEntity(pbuid);
									if(p!=null)                                          //����pbu��Ϊ�յ��ж�
									{
										p.Index=index;
										pbulist.add(p);
									}

								}
								Collections.sort(pbulist);
								timelinepbu.PBUList = pbulist;
								//this.timelinepbulist.add(timelinepbu);
								this.insertTimeline(timelinepbu,time);
							}
						}
					}

					else if(name.equalsIgnoreCase("IdlePBU"))
					{

						String defaultPBUId = element.getAttribute("Id");
						PBU defaultPBU = new PBU();
						defaultPBU=PBUStorage.GetPBUEntity(defaultPBUId);
						ScheduleStorage.defaultPBU=defaultPBU;
					}
				}
				scheduleStorage.timelinepbulist = this.timelinepbulist;

			}



		} catch (Exception ex) {
			Log.i(ScheduleXML, ex.getMessage());
			LoggerHelper.WriteLogfortxt("ScheduleXml GetScheduleInfo==>"+ex.getMessage() );
		}
		return scheduleStorage;
	}

	private void insertTimeline(TimelinePBUBlock timelinepbu,String currentTime){
		if(this.timelinepbulist.size()==0){
			this.timelinepbulist.add(timelinepbu);
			return;
		}
		
		Integer insertIndex=0;
		TimelinePBUBlock currentBlock=null;
		TimelinePBUBlock cuttedBlokBlock=null;
		List<TimelinePBUBlock> modifyBlocks=new ArrayList<TimelinePBUBlock>();
		for (int i = this.timelinepbulist.size()-1; i >=0; i--) {
			currentBlock=this.timelinepbulist.get(i);
			if(timelinepbu.StartTime.compareTo(currentBlock.StartTime)>=0){
				if(timelinepbu.StartTime.compareTo(currentBlock.EndTime)>=0)
					continue;
				
				if(timelinepbu.EndTime.compareTo(currentBlock.EndTime)<=0){
					cuttedBlokBlock= new TimelinePBUBlock();
					cuttedBlokBlock.Recurrence=currentBlock.Recurrence;
					cuttedBlokBlock.StartDate=cuttedBlokBlock.StartDate;
					cuttedBlokBlock.EndDate=cuttedBlokBlock.EndDate;
					cuttedBlokBlock.StartTime=currentBlock.StartTime;
					cuttedBlokBlock.EndTime=timelinepbu.StartTime;
					for (PBU pbu : currentBlock.PBUList) {
						PBU newPbu=PBUStorage.GetPBUEntity(pbu.ID);
						newPbu.Index=pbu.Index;
						cuttedBlokBlock.PBUList.add(newPbu);
					}
					
					currentBlock.StartTime=timelinepbu.EndTime;
					modifyBlocks.add(currentBlock);
					insertIndex=i+1;
					break;
				}
				currentBlock.EndTime=timelinepbu.StartTime;
				modifyBlocks.add(currentBlock);
				continue;
			}
			
			if(timelinepbu.EndTime.compareTo(currentBlock.StartTime)>0){
				currentBlock.StartTime=timelinepbu.EndTime;
				modifyBlocks.add(currentBlock);
			}
			
			if(timelinepbu.EndTime.compareTo(currentBlock.EndTime)<=0){
				insertIndex=i+1;
				break;
			}
			
		}
		
		if(cuttedBlokBlock!=null){
			if(insertIndex==this.timelinepbulist.size())
				this.timelinepbulist.add(cuttedBlokBlock);
			else
				this.timelinepbulist.add(insertIndex, cuttedBlokBlock);
		}
		if(insertIndex==this.timelinepbulist.size())
			this.timelinepbulist.add(timelinepbu);
		else
			this.timelinepbulist.add(insertIndex, timelinepbu);
		
		for (TimelinePBUBlock timelinePBUBlock : modifyBlocks) {
			if(timelinePBUBlock.StartTime.compareTo(timelinePBUBlock.EndTime)>=0||timelinePBUBlock.EndTime.compareTo(currentTime)<=0)
				this.timelinepbulist.remove(timelinePBUBlock);
		}
	}

}
