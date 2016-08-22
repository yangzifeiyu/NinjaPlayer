package com.mfusion.player.library.Database.Sqlite;


import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelperSQLite extends SQLiteOpenHelper {  

	private static final int VERSION = 1;//�汾  
	private static final String DB_NAME = PlayerStoragePath.SqliteStorage+"PlayerConfig.db";//��ݿ���  
	public static final String Setting_TABLE = "setting";//����  
	public static final String Medias_TABLE="mediastorage";
	public DbHelperSQLite(Context context) {  
		super(context, DB_NAME, null, VERSION);  
	}  

	//��ݿ��һ�α�����ʱ����   
	@Override  
	public void onCreate(SQLiteDatabase db) {  
		db.execSQL("CREATE TABLE "+Setting_TABLE+" (key VARCHAR, value VARCHAR)"); 

		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"MFServerIp", "127.0.0.1"});
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"MFServerPort", "6060" });
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"MediaServerPort","4041" });
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"UpdateTime","" });
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"License","" });
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"RealCmdExecuteTime","" });
		db.execSQL("INSERT INTO "+Setting_TABLE+" VALUES (?, ?)", new Object[] {
				"AndroidType","Common" });//Common/Panasonic


		db.execSQL("CREATE TABLE "+Medias_TABLE+" (EntryDate VARCHAR, Path VARCHAR,MediaType VARCHAR)"); 
	}  

	//�汾��ʱ������   
	@Override  
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  

	}  

}  