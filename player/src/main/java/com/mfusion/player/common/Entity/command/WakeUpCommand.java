package com.mfusion.player.common.Entity.command;

import com.mfusion.player.common.Entity.Command;
import com.mfusion.player.common.Enum.AndroidType;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.panasonic.avc.vsbd.displaycontrollibrary.CallbackReceiveListener;
import com.panasonic.avc.vsbd.displaycontrollibrary.DisplayControl;

/**
 * Created by ThinkPad on 2016/10/21.
 */
public class WakeUpCommand extends Command {

    @Override
    public CommandType getCommandType() {
        // TODO Auto-generated method stub
        return CommandType.WakeUp;
    }

    @Override
    public void Run() {
        // TODO Auto-generated method stub
        try {
            if(this.target!=null&&this.target.equalsIgnoreCase("af1")){
                LoggerHelper.WriteLogfortxt("Command wakeup==>af1");
                final DisplayControl control=new DisplayControl();
                control.setCallbackListener(new CallbackReceiveListener() {
                    @Override
                    public void onCallbackReceive() {
                        System.out.println("wakeup "+control.getCallbackMsg());
                        LoggerHelper.WriteLogfortxt("Command wakeup==>"+control.getCallbackMsg());
                    }
                });
                control.setPowerOn(0);
                return;
            }
        }
        catch (Exception e) {
            LoggerHelper.WriteLogfortxt("Command wakeup==>"+e.getMessage());
        }
    }
}
