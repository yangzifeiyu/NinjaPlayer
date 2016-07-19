package com.mfusion.commons.data;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.SQLiteDBHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;

/**
 * Created by jimmy on 7/12/2016.
 */
public class DALSettings {
    private volatile static DALSettings singleton;

    public static DALSettings getInstance() {
        if (singleton == null) {
            synchronized (DALSettings.class) {
                if (singleton == null) {
                    singleton = new DALSettings();
                }
            }
        }
        return singleton;
    }

    public DALSettings(){}

    /**
     * Get all system settings
     * @param
     * @return
     * @throws Exception
     */
    public HashMap<String, String> getSystemSetting() {
        // TODO Auto-generated method stub
        return SQLiteDBHelper.getConfiguration(InternalKeyWords.Config_DBPath,InternalKeyWords.Config_TableName);
    }

    public String getShutDownTime(){
        return this.getSettingByKey(InternalKeyWords.Config_ShutDownTime);
    }

    public Boolean setShutDownTime(String timeString){
        return this.updateSetting(InternalKeyWords.Config_ShutDownTime,timeString);
    }

    public String getExitPassword(){
        return this.getSettingByKey(InternalKeyWords.Config_ExitPassword);
    }

    public Boolean setExitPassword(String password){
        return this.updateSetting(InternalKeyWords.Config_ExitPassword,password);
    }

    public String getWakeUpTime(){
        return this.getSettingByKey(InternalKeyWords.Config_WakeUpTime);
    }

    public Boolean setWakeUpTime(String timeString){
        return this.updateSetting(InternalKeyWords.Config_WakeUpTime,timeString);
    }

    public String getSettingByKey(String key) {
        // TODO Auto-generated method stub
        return SQLiteDBHelper.getConfiguration(InternalKeyWords.Config_DBPath,InternalKeyWords.Config_TableName,key);
    }

    public Boolean updateSetting(String key, String value) {
        // TODO Auto-generated method stub
        if( SQLiteDBHelper.updateDB(InternalKeyWords.Config_DBPath,InternalKeyWords.Config_TableName, key, value))
            return true;
        return false;
    }

    public Boolean assignDeviceCmd(String assignDestFolder) {
        // TODO Auto-generated method stub
        try {
            String scheduleName=this.getSettingByKey(InternalKeyWords.Config_PlayingSchedule);

            return XMLSchedule.getInstance().assignSchedule(scheduleName);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }


}
