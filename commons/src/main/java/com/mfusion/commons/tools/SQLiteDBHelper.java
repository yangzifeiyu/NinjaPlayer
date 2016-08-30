package com.mfusion.commons.tools;

import java.io.File;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by guoyu on 7/14/2016.
 */
public class SQLiteDBHelper {

	public static HashMap<String, String> getConfiguration(String dbPath,String tableName){
		
		HashMap<String, String> settings=new HashMap<String, String>();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath,null);

		db.execSQL("create table if not exists '"+tableName+"'(key VARCHAR, value VARCHAR)");
		Cursor c = db.rawQuery("SELECT * FROM "+tableName,new String[] {  });
		String key="",value="";
		while (c.moveToNext()) {
			key=c.getString(c.getColumnIndex("key"));
			value=c.getString(c.getColumnIndex("value"));
			settings.put(key, value);
		}

		c.close();
		db.close();
		return settings;
	}
	
	public static String getConfiguration(String dbPath,String tableName,String key){

		String value=null;
		try{
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
			Cursor c = db.rawQuery("SELECT * FROM "+tableName+" where key='"+key+"'",new String[] {  });
			
			if(c.moveToNext())
				value=c.getString(c.getColumnIndex("value"));
			
			c.close();
			db.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return value;
	}
	
	public static Boolean updateDB(String dbPath,String tableName,String key,String value){
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
		db.execSQL("create table if not exists '"+tableName+"'(key VARCHAR, value VARCHAR)");
		ContentValues values = new ContentValues();
		values.put("key", key);//keyΪ�ֶ���valueΪֵ
		values.put("value", value);//keyΪ�ֶ���valueΪֵ
		int result = db.update(tableName, values, "key=?", new String[]{key});
		if(result<=0){
			db.insert(tableName, null, values);
		}
		db.close();
		return true;
	}
}
