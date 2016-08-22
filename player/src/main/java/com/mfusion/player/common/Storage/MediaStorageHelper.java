package com.mfusion.player.common.Storage;

import java.util.Date;
import java.util.Hashtable;
import java.util.IdentityHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mfusion.player.library.Database.Sqlite.DbHelperSQLite;
import com.mfusion.player.library.Helper.LoggerHelper;

public class MediaStorageHelper {
	private DbHelperSQLite dbOpenHelper;  

	public MediaStorageHelper(Context context) {  
		dbOpenHelper = new DbHelperSQLite(context);  
	}  
	
	public IdentityHashMap<String, String> getMediaByDate(String deleteDay)
	{
		try
		{
			IdentityHashMap<String, String> medias=new IdentityHashMap<String, String>();
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			try
			{
				Cursor cursor=db.query(DbHelperSQLite.Medias_TABLE, new String[] { "Path","MediaType"},"EntryDate<=?", new String[]{deleteDay},null,null, null, null);
				int resultCounts = cursor.getCount();  
				if (resultCounts == 0 || !cursor.moveToFirst()){  
					return null;  
				}  

				for (int i = 0 ; i<resultCounts; i++){  

					String filed=cursor.getString(cursor.getColumnIndex("Path"));  
					String value= cursor.getString(cursor.getColumnIndex("MediaType"));  
			
					medias.put(filed, value);
					cursor.moveToNext();  
				}  
			}
			catch(Exception ex)
			{
				LoggerHelper.WriteLogfortxt("MediaStorageHelper getMediaByDate==>"+ex.getMessage());
			}
			finally
			{
				db.close();
				db=null;
			}
			return medias;
		}
		catch(Exception ex)
		{
			return null;
		}
	}


	public void DeleteExpiryMedia(String deleteDay) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try
		{
			db.delete(DbHelperSQLite.Medias_TABLE, "EntryDate<=?", new String[]{deleteDay});
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MediaStorageHelper DeleteExpiryMedia==>"+ex.getMessage());
		}
		finally
		{
			db.close();
			db=null;
		}
	}

	public boolean UpdateMediaInfo(String path, String date, String mediaType)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try
		{
			ContentValues cv = new ContentValues();
			cv.put("EntryDate", date);
			int result=db.update(DbHelperSQLite.Medias_TABLE, cv, "Path=?  AND MediaType=?", new String[]{path,mediaType}); 
			if(result>0)
			{
				return true;
			}
			else
			{
				ContentValues media = new ContentValues();
				media.put("EntryDate", date);
				media.put("Path", path);
				media.put("MediaType", mediaType);
				long row=db.insert(DbHelperSQLite.Medias_TABLE, null, media);
				if(row>=0)
					return true;
				else
					return false;
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("MediaStorage AddMediaInfo==>"+ex.getMessage());
			return false;
		}
		finally
		{
			db.close();
			db=null;
		}
	}

}
