package com.mfusion.player.common.Storage;

import java.util.ArrayList;
import java.util.List;
import com.mfusion.player.common.Entity.TimelineCommand;
import com.mfusion.player.common.XML.CommandXML;

public class CommandStorage {
	private CommandXML m_commandXML;
	public CommandStorage()
	{
		m_commandXML=new CommandXML();
	}
	public List<TimelineCommand> GetAllTimeLineDevices()
	{

		ArrayList<TimelineCommand> t_device = new ArrayList<TimelineCommand>();
		try
		{
			t_device=m_commandXML.GetCommandLineInfo();
		}
		catch (Exception ex) { ex.printStackTrace(); }

		return t_device;
	}
}
