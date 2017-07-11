package com.mfusion.commons.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mfusion.commons.entity.license.LicenseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by ThinkPad on 2017/1/6.
 */
public class LicenseStorage {

    private static String storage_path= Environment.getExternalStorageDirectory().getPath()+"/.ninja_license.db";

    private static String storage_table_name="licenseRecords";

    public static Boolean saveLicense(LicenseEntity entity){
        try{

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(storage_path,null);
            db.beginTransaction();

            try {
                db.execSQL("create table if not exists '"+storage_table_name+"'(deviceId VARCHAR,deviceCode int, license VARCHAR, startDate VARCHAR,expiryDate VARCHAR )");
                db.execSQL("insert into '"+storage_table_name+"'(deviceId,deviceCode, license,startDate,expiryDate) values(?,?,?,?,?)", new Object[]{entity.deviceId,entity.deviceHashCode, entity.license,DateConverter.convertDateToStr(entity.startDate),DateConverter.convertDateToStr(entity.validDate)});
                db.setTransactionSuccessful();//调用此方法会在执行到endTransaction() 时提交当前事务，如果不调用此方法会回滚事务
                return true;
            } finally {
                db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
                db.close();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static LicenseEntity readLicense(){
        LicenseEntity licenese=new LicenseEntity();
        try{
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(storage_path,null);
            Cursor c = db.rawQuery("SELECT * FROM '"+storage_table_name+"' ",new String[] {  });

            if(c.moveToNext()) {
                licenese.deviceId=InternalKeyWords.DeviceId;
                licenese.deviceHashCode = c.getInt(c.getColumnIndex("deviceCode"));
                licenese.license = c.getString(c.getColumnIndex("license"));
                licenese.startDate =DateConverter.convertStrToDate( c.getString(c.getColumnIndex("startDate")));
                licenese.validDate=DateConverter.convertStrToDate( c.getString(c.getColumnIndex("expiryDate")));
            }

            c.close();
            db.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return licenese;
    }
}
