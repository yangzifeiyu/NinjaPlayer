/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *PlayerSetting Helper
 */
package com.mfusion.player.common.Setting.Player;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mfusion.player.common.Enum.AndroidType;

import com.mfusion.player.library.Database.Sqlite.DbHelperSQLite;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class PlayerSettingHelper {
	private DbHelperSQLite dbOpenHelper;  
	/*
	 * ���캯��
	 */
	public PlayerSettingHelper(Context context) {  
		dbOpenHelper = new DbHelperSQLite(context);  
	}  

	/*
	 * ����ĳ��key�������
	 */
	public void UpdateFiled(String filed,String value) {  
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try
		{
			ContentValues values = new ContentValues();  
			values.put("value", value);  
			int result=db.update(DbHelperSQLite.Setting_TABLE, values, "key=?", new String[]{filed}); 
			if(result>0)
				return;
			else
			{
				ContentValues playername = new ContentValues();
				playername.put("key", filed);
				playername.put("value", value);
				db.insert(DbHelperSQLite.Setting_TABLE, null, playername);
				
			}
		}

		catch(Exception ex)
		{

			LoggerHelper.WriteLogfortxt("PlayerSettingHelper UpdateFiled==>"+ex.getMessage());
		}
		finally
		{
			db.close();
			db=null;
		}
	}  

	public void UpdateAllFileds(Map<String,String> map) { 
		try
		{
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String> ) iter.next();
				String key = entry.getKey().toString();
				String val = entry.getValue().toString();
				UpdateFiled(key,val);

			}
		}
		catch(Exception ex)
		{

			LoggerHelper.WriteLogfortxt("PlayerSettingHelper UpdateAllFileds==>"+ex.getMessage());
		}

	}  

	public void UpdatePlayerSetting (String serverip,String serverPort,String screenOrientation)
	{
		try
		{
			Map<String,String> map=new HashMap<String,String>();
			map.put("MFServerIp",serverip);
			map.put("MFServerPort", serverPort);
			map.put("ScreenOrientation",screenOrientation);
			UpdateAllFileds(map);
		}
		catch(Exception ex)
		{

			LoggerHelper.WriteLogfortxt("PlayerSettingHelper UpdatePlayerSetting==>"+ex.getMessage());
		}
	}

	public PlayerSetting GetPlayerSetting()
	{
		PlayerSetting playersetting=new PlayerSetting();
		try
		{
			Map<String,String> map=this.GetAllFileds();

			if(map.containsKey("MFServerIp"))
				playersetting.MediaServerIP=playersetting.MFServerIp=map.get("MFServerIp");		
			if(map.containsKey("MFServerPort"))
				playersetting.MFServerPort=map.get("MFServerPort");
			if(map.containsKey("MediaServerPort"))
				playersetting.setMediaport(map.get("MediaServerPort"));
			if(map.containsKey("UpdateTime"))
				playersetting.setUpdateTime(map.get("UpdateTime"));
			if(map.containsKey("AndroidType"))
				playersetting.BoxType=AndroidType.fromString(map.get("AndroidType"));
			if(map.containsKey("ScreenOrientation"))
				playersetting.ScreenOrientation=Integer.parseInt(map.get("ScreenOrientation"));

		}catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSettingHelper GetPlayerSetting==>"+ex.getMessage());
		}
		return playersetting;
	}

	public Map<String,String> GetAllFileds()
	{
		Map<String,String> map=new HashMap<String,String>();
		try
		{
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			Cursor cursor=db.query(DbHelperSQLite.Setting_TABLE, new String[] { "key","value" },null, null, null,null, null, null);
			int resultCounts = cursor.getCount();  
			if (resultCounts == 0 || !cursor.moveToFirst()){  
				return map;  
			}  

			for (int i = 0 ; i<resultCounts; i++){  

				String filed=cursor.getString(cursor.getColumnIndex("key"));  
				String value= cursor.getString(cursor.getColumnIndex("value"));  
				map.put(filed, value);

				cursor.moveToNext();  
			}  
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PlayerSettingHelper GetAllFileds==>"+ex.getMessage());
		}
		return map;
	}

}




