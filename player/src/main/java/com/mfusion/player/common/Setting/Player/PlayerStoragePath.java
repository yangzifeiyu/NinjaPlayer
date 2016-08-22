package com.mfusion.player.common.Setting.Player;


import com.mfusion.player.common.Player.MainActivity;

import android.os.Environment;

public class PlayerStoragePath {
	public static String SdcardPath=Environment.getExternalStorageDirectory().getPath()+"/";
	//public static String SdcardPath="/storage/emulated/legacy/";
	public static String MediaStorage = SdcardPath + "Storage/";

	public static String DataStorage =SdcardPath + "Data/";

	public static String ImageStorage = MediaStorage + "Image/";

	public static String VideoStorage = MediaStorage + "Video/";
	
	public static String AudioStorage = MediaStorage + "Audio/";

	public static String XMLStorage =DataStorage + "XML/";

	public static String LogStorage=SdcardPath + "Log/";
	
	public static String CrashStorage=SdcardPath + "PlayerCrash/";
	
	public static String SqliteStorage="/data/data/com.mfusion.player/database/";

	public static String WeatherIconStorage = MediaStorage + "Weather/";

}
