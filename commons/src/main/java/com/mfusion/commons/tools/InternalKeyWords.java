package com.mfusion.commons.tools;

import android.os.Environment;

import java.io.File;


public class InternalKeyWords {

    public static String Config_DBPath="/data/data/common.Player/database/PlayerConfig.db";

    public static String Config_TableName="setting";

    public static String Config_PlayingSchedule="PlayingSchedule";

    public static String Config_ShutDownTime="ShutDownTime";

    public static String Config_WakeUpTime="WakeUpTime";

    public static String Config_ExitPassword="ExitPassword";

    public static String DefaultTemplateXmlPath= Environment.getExternalStorageDirectory().getPath()+"/Storage/Template/";

    public static String DefaultScheduleXmlPath= Environment.getExternalStorageDirectory().getPath()+"/Storage/Schedule/";

    public static String DefaultXmlTempPath= Environment.getExternalStorageDirectory().getPath()+"/Temp/";

    public static String TemplateResourceFolder="res"+ File.separator;

    public static String DefaultScheduleName="default";

    public static String AssignedXmlName="MFusion.xml";

    public static String AssignedXmlFolder=Environment.getExternalStorageDirectory().getPath()+"/Data/";

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().toString();
    public static final String APP_FOLDER = "/MFusion/";
    public static final String DB_NAME = "MfusionDataBase.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_TEMPLATE="tamplate";
    public static final String TABLE_NAME_COMPONENT="component";
    public static final String TABLE_NAME_USER_SCREEN="userscreen";
    public static final String TABLE_NAME_USER_SCREEN_COMPONENT="userscreencomponent";
    public static final String TAG="DBController";

}
