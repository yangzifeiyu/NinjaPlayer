package com.mfusion.player.common.Entity.command;

import com.panasonic.avc.vsbd.displaycontrollibrary.DisplayControl;

import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Command;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;

public class VolumeCommand extends Command {


	public Integer volume=0;
	@Override
	public CommandType getCommandType() {
		// TODO Auto-generated method stub
		return CommandType.Volume;
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		try {
			LoggerHelper.WriteLogfortxt("Command volume==>"+this.volume+"%");
			
			if(this.target!=null&&this.target.equalsIgnoreCase("af1")){

				DisplayControl control=new DisplayControl();
				control.setVolume(volume);
				return;
			}
			
			Integer nextVolume=this.volume*15/100;
			MainActivity.Instance.setVolume(nextVolume);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LoggerHelper.WriteLogfortxt("Command volume==>"+e.getMessage());
		}
	}

}
