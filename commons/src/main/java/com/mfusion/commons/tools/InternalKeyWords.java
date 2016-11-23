package com.mfusion.commons.tools;

import android.os.Environment;

import java.io.File;
/**
 * Created by guoyu on 7/14/2016.
 */
public class InternalKeyWords {

	public static String Log_Path=Environment.getExternalStorageDirectory().getPath()+"/Log/log.txt";

	public static String Config_DBPath="/data/data/com.mfusion.player/database/PlayerConfig.db";

	public static String Config_TableName="setting";

	public static String Config_PlayingSchedule="PlayingSchedule";

	public static String Config_Orientation="ScreenOrientation";

	public static String Config_ShutDownTime="ShutDownTime";

	public static String Config_WakeUpTime="WakeUpTime";

	public static String Config_ExitPassword="ExitPassword";

	public static String Config_DefaulttPassword="mfusion";

	public static String DefaultTemplateXmlPath= Environment.getExternalStorageDirectory().getPath()+"/Storage/Template/";

	public static String DefaultScheduleXmlPath= Environment.getExternalStorageDirectory().getPath()+"/Storage/Schedule/";

	public static String DefaultXmlTempPath= Environment.getExternalStorageDirectory().getPath()+"/Temp/";

	public static String VideoThumbPath= Environment.getExternalStorageDirectory().getPath()+"/Storage/Thumb/";

	public static String TemplateResourceFolder="res"+ File.separator;

	public static String DefaultScheduleName="default";

	public static String AssignedXmlName="MFusion.xml";

	public static String AssignedXmlFolder=Environment.getExternalStorageDirectory().getPath()+"/Data/XML/";

	public static int MaxTemplateCount=10;

	public static String PlayerOriginal="AF1";//AF1 Normal

	public static String DefaultDisplayDateFormat="dd/MM/yyyy";

	public static int TemplateDefaultWidth=1920;

	public static int TemplateDefaultHeight=1080;
}
