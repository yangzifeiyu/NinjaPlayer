package com.mfusion.commons.data;

import android.content.pm.ActivityInfo;

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

    public int getOrientation(){
        String orientation=this.getSettingByKey(InternalKeyWords.Config_Orientation);
        if(orientation==null||orientation.isEmpty())
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        return Integer.parseInt(orientation);
    }

    public Boolean setOrientation(int orientation){
        return this.updateSetting(InternalKeyWords.Config_Orientation,String.valueOf(orientation));
    }

    public String getShutDownTime(){
        return this.getSettingByKey(InternalKeyWords.Config_ShutDownTime);
    }

    public Boolean setShutDownTime(String timeString){
        String oldDatas=getShutDownTime();
        Boolean result =this.updateSetting(InternalKeyWords.Config_ShutDownTime,timeString);
        if(timeString!=null&&!timeString.equalsIgnoreCase(oldDatas)){
            try {
                XMLSchedule.getInstance().assignDeviceSchedule();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return result;
    }

    public String getExitPassword(){
        String password=this.getSettingByKey(InternalKeyWords.Config_ExitPassword);
        if(password==null)
            return InternalKeyWords.Config_DefaulttPassword;

        return password;
    }

    public Boolean setExitPassword(String password){
        return this.updateSetting(InternalKeyWords.Config_ExitPassword,password);
    }

    public String getWakeUpTime(){
        return this.getSettingByKey(InternalKeyWords.Config_WakeUpTime);
    }

    public Boolean setWakeUpTime(String timeString){
        String oldDatas=getWakeUpTime();
        Boolean result =this.updateSetting(InternalKeyWords.Config_WakeUpTime,timeString);
        if(result&&timeString!=null&&!timeString.equalsIgnoreCase(oldDatas)){
            try {
                XMLSchedule.getInstance().assignDeviceSchedule();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
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

    public Boolean saveConfigParameters(int orientation,String password,String shutdowntime,String wakeuptime){
        Boolean result=true;
        String old_shutdown=this.getShutDownTime(),old_wakeup=this.getWakeUpTime();
        result=result&&this.updateSetting(InternalKeyWords.Config_Orientation,String.valueOf(orientation));
        result=result&&this.updateSetting(InternalKeyWords.Config_ExitPassword,password);
        result=result&&this.updateSetting(InternalKeyWords.Config_ShutDownTime,shutdowntime);
        result=result&&this.updateSetting(InternalKeyWords.Config_WakeUpTime,wakeuptime);
        if((shutdowntime!=null&&!shutdowntime.equalsIgnoreCase(old_shutdown))||(wakeuptime!=null&&!wakeuptime.equalsIgnoreCase(old_wakeup))){
            try {
                XMLSchedule.getInstance().assignDeviceSchedule();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return result;
    }

}
