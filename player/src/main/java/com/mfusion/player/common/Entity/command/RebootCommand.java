package com.mfusion.player.common.Entity.command;

import com.panasonic.avc.vsbd.displaycontrollibrary.DisplayControl;

import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Command;
import com.mfusion.player.common.Enum.AndroidType;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;

public class RebootCommand extends Command {

	@Override
	public CommandType getCommandType() {
		// TODO Auto-generated method stub
		return CommandType.Restart;
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		try {
			if(this.target!=null&&this.target.equalsIgnoreCase("af1")){
				return;
			}
			
			String cmd="";
			if(MainActivity.Instance.PlayerSetting.BoxType.equals(AndroidType.Common))
				cmd="su -c ";
			cmd += "reboot";
			LoggerHelper.WriteLogfortxt("Command reboot==>"+cmd);
			Runtime.getRuntime().exec(cmd);
		} 
		catch (Exception e) {
			LoggerHelper.WriteLogfortxt("Command reboot==>"+e.getMessage());
		} 
	}

}
