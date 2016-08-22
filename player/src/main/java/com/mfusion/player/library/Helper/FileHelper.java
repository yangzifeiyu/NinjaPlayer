/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *File Helper
 */
package com.mfusion.player.library.Helper;

import java.io.File;

public class FileHelper {
	/*
	 * �ж�ĳ�洢λ�õ��ļ��Ƿ����
	 */
	public static boolean IsExists(String filepath)
	{
		File file=null;
		try
		{
			file = new File(filepath);
			return file.exists();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileHelper IsExists==>"+ex.getMessage() );
			return false;
		}
		finally
		{
			if(file!=null)
				file=null;
		}
	}
	/*
	 * �ж��ļ��Ƿ���ڣ��������򴴽�
	 */
	public static void IsExistsAndCreate(String filepath)
	{
		File file=null;
		try
		{
			file = new File(filepath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileHelper IsExistsAndCreate==>"+ex.getMessage() );
			
		}
		finally
		{
			file=null;
		}
	}
	
	/*
	 * �ж��ļ��Ƿ���ڣ��������򴴽�
	 */
	public static void IsExistsAndDeleteAndCreate(String filepath)
	{
		File file=null;
		try
		{
			file = new File(filepath);
			if (file.exists()) {
				file.delete();
			}
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileHelper IsExistsAndCreate==>"+ex.getMessage() );
			
		}
		finally
		{
			file=null;
		}
	}
	
	/*
	 * ɾ���ļ�
	 */
	public static void DeleteFile(String filepath) {
		// TODO Auto-generated method stub
		File file=null;
		try
		{
		    file = new File(filepath);
			if (file.exists()) {
				file.deleteOnExit();
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("FileHelper DeleteFile==>"+ex.getMessage() );
			
		}
		finally
		{
			file=null;
		}
	}
}
