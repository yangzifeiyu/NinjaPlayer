package com.mfusion.player.common.Entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.R.integer;

import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.AndroidType;
import com.mfusion.player.common.Enum.CommandType;
import com.mfusion.player.common.Player.MainActivity;

public abstract class Command {

	public String target="";
	
	public abstract CommandType getCommandType();
	
	public abstract void Run();
}
