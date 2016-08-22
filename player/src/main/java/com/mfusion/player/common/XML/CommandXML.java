package com.mfusion.player.common.XML;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import android.R.integer;

import com.mfusion.player.common.Entity.Command;
import com.mfusion.player.common.Entity.TimelineCommand;
import com.mfusion.player.common.Entity.command.RebootCommand;
import com.mfusion.player.common.Entity.command.ShutdownCommand;
import com.mfusion.player.common.Entity.command.VolumeCommand;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class CommandXML {
	private XMLHelper xmlHelper;
	private Date currentTime;// player������ʱ��
	private String date;
	private String time;
	private int dayofweek;
	private ArrayList<TimelineCommand> timelinecommandlist = new ArrayList<TimelineCommand>();

	public CommandXML() {
		xmlHelper = new XMLHelper();
		currentTime = MainActivity.Instance.Clock.Now;
		date = DateTimeHelper.ConvertToString(currentTime, 1);//Ҫ�ı�
		time = DateTimeHelper.ConvertToString(currentTime, 2);
		dayofweek = DateTimeHelper.GetDayOfWeek(currentTime);
	}

	public ArrayList<TimelineCommand> GetCommandLineInfo() {
		try {

			if (xmlHelper != null) {
				// device_timeline
				ArrayList<TimelineCommand> volumelist = new ArrayList<TimelineCommand>();
				Element device_run_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Device\\Volume");
				if(device_run_element!=null)
				{
					Element[] devicetimelines = xmlHelper
							.getElementsByParentElement(device_run_element);
					volumelist.addAll(GetCommandLine(devicetimelines));
				}
				
				device_run_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Device\\Power");
				if(device_run_element!=null)
				{
					Element[] devicetimelines = xmlHelper
							.getElementsByParentElement(device_run_element);
					timelinecommandlist.addAll(GetCommandLine(devicetimelines));
				}
				
				int insertIndex=0,index=0;
				for (TimelineCommand volumeCmd : volumelist) {
					insertIndex=0;
					for(index=timelinecommandlist.size()-1;index>=0;index--){
						if(volumeCmd.Runtime.compareTo(((TimelineCommand)timelinecommandlist.get(index)).Runtime)>0){
							insertIndex=index+1;
							break;
						}
					}
					if(insertIndex>=timelinecommandlist.size()){
						timelinecommandlist.add(volumeCmd);
					}else {
						timelinecommandlist.add(insertIndex,volumeCmd);
					}
				}

			}


		} catch (Exception ex) {

		}
		return timelinecommandlist;
	}

	public ArrayList<TimelineCommand> GetCommandLine(Element[] devicetimelines)
	{
		ArrayList<TimelineCommand> list = new ArrayList<TimelineCommand>();
		try
		{
			for (int i = 0; i < devicetimelines.length; i++) {
				TimelineCommand timelinecommand = new TimelineCommand();
				TimelineCommand timelinecommand_end = null;
				Element element = devicetimelines[i];
				Element[] commands;
				String recurrence = "";
				String enddate = "";
				String startdate = "";
				String runtime = "";
				String endtime = "";
				Boolean isrealcmd=false;
				startdate = element.getAttribute("StartDate");
				enddate = element.getAttribute("EndDate");
				recurrence = element.getAttribute("Recurrence");
				runtime = element.getAttribute("RunTime");
				endtime= element.getAttribute("EndTime");
				isrealcmd=Boolean.parseBoolean(element.getAttribute("RealTimeCmd"));
				if (startdate.compareTo(date) <= 0&& (runtime.compareTo(time) >= 0|| runtime.equals("")||(endtime.equals("")==false&&endtime.compareTo(time) >= 0 ))&& (enddate.isEmpty()||enddate.equals("0001,01,01") || enddate.compareTo(date) >= 0)) {

					if ((recurrence.equals("") && startdate.equals(date))|| (!recurrence.equals("") && (recurrence.charAt(dayofweek) == '1'))) {
						timelinecommand.Recurrence = recurrence;
						timelinecommand.EndDate = enddate;
						timelinecommand.StartDate = startdate;
						timelinecommand.StartTime = runtime;
						if(isrealcmd){
							timelinecommand.Runtime=MainActivity.Instance.Clock.Now;
						}
						else
							timelinecommand.Runtime =DateTimeHelper.ConvertToDateTime(date.toString(),runtime,MainActivity.Instance.PlayerSetting.Timezone);

						commands = xmlHelper
								.getElementsByParentElement(element);
						List<Command> commandlist = new ArrayList<Command>();
						for (int j = 0; j < commands.length; j++) {
							Command com = null;
							Element command = commands[j];
							String commandval = command
									.getAttribute("value");
							String commandname = command
									.getAttribute("name");
							String target=command
									.getAttribute("target");
							CommandType cmdType = CommandType.fromString(commandname);
							if(cmdType==CommandType.Volume){
								com=new VolumeCommand();
								((VolumeCommand)com).volume=Integer.valueOf(commandval);
								if(timelinecommand.Runtime.compareTo(MainActivity.Instance.Clock.Now) < 0)
									timelinecommand.Runtime= MainActivity.Instance.Clock.Now; 
								timelinecommand_end=GetVolumeEndCmd(timelinecommand,endtime);
							}else{
								if(MainActivity.Instance.PlayerSetting.RealCmdExecuteTime.equalsIgnoreCase(commandname+startdate+runtime)){
									LoggerHelper.WriteLogfortxt(commandname+startdate+runtime+" has be executed");
									continue;
								}
								
								if(cmdType==CommandType.Shutdown){
									com=new ShutdownCommand();
								}
								if(cmdType==CommandType.Restart){
									com=new RebootCommand();
								}
							}
							if(com!=null){
								com.target=target;
								commandlist.add(com);
							}
						}
						timelinecommand.commondList = commandlist;
						list.add(timelinecommand);
						if(timelinecommand_end!=null)
							list.add(timelinecommand_end);
					}
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	private TimelineCommand GetVolumeEndCmd(TimelineCommand startCmdLine,String runtime){
		TimelineCommand endcmdLine = new TimelineCommand();
		endcmdLine.Recurrence = startCmdLine.Recurrence;
		endcmdLine.EndDate = startCmdLine.EndDate;
		endcmdLine.StartDate = startCmdLine.StartDate;
		endcmdLine.StartTime = runtime;
		endcmdLine.Runtime =DateTimeHelper.ConvertToDateTime(date.toString(),runtime,MainActivity.Instance.PlayerSetting.Timezone);
		endcmdLine.commondList=new ArrayList<Command>();
		Command endCmd = new VolumeCommand();
		((VolumeCommand)endCmd).volume=MainActivity.Instance.getVolume();
		endcmdLine.commondList.add(endCmd);
		
		return endcmdLine;
	}
}
