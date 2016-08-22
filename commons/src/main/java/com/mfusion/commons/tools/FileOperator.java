package com.mfusion.commons.tools;

import com.mfusion.commons.entity.exception.PathAccessException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
/**
 * Created by guoyu on 7/14/2016.
 */
public class FileOperator {

	public static Boolean createDir(String folder){
		try {
			
			File tempFile=new File(folder);
			if(!tempFile.exists())
				tempFile.mkdirs();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>createDir "+folder+" :"+e.getMessage());
		}
		return false;
	}
	

	public static Boolean createWithDeleteDir(String folder){
		try {
			
			File tempFile=new File(folder);
			if(tempFile.exists())
				deleteFile(folder);
			
			tempFile.mkdirs();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>createWithDeleteDir "+folder+" :"+e.getMessage());
		}
		return false;
	}
	
	public static Boolean deleteFile(String filePath){
		try {
			
			File file=new File(filePath);
			if(!file.exists())
				return true;
			
			File files[] = file.listFiles();  
	        if (files != null)  
	            for (File f : files) {  
	                if (f.isDirectory()) { // �ж��Ƿ�Ϊ�ļ���  
	                	deleteFile(f.getAbsolutePath());  
	                    try {  
	                        f.delete();  
	                    } catch (Exception e) {  
	                    }  
	                } else {  
	                    if (f.exists()) { // �ж��Ƿ����  
	                        try {  
	                            f.delete();  
	                        } catch (Exception e) {  
	                        }  
	                    }  
	                }  
	            }  
	        
	        file.delete();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>deleteFile "+filePath+" :"+e.getMessage());
		}
		return false;
	}
	
	public static Boolean existFile(String filePath){
		
		try {
			
			File tempFile=new File(filePath);
			return tempFile.exists();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>existFile "+filePath+" :"+e.getMessage());
		}
		return false;
	}

	public static Boolean copyFile(String oldPath, String newPath) {   
		try {   
			int bytesum = 0;   
			int byteread = 0;   
			String tempPath=newPath+".tmp";
			File tempfile = new File(tempPath);
			if(tempfile.exists())
				tempfile.delete();
			tempfile.createNewFile();
			InputStream inStream = new FileInputStream(oldPath);
			FileOutputStream fs = new FileOutputStream(tempPath);   
			byte[] buffer = new byte[1444];   
			int length;   
			while ( (byteread = inStream.read(buffer)) != -1) {   
				bytesum += byteread;
				System.out.println(bytesum);   
				fs.write(buffer, 0, byteread);   
			}   
			inStream.close();   
			fs.close();
			tempfile = new File(tempPath);
			tempfile.renameTo(new File(newPath));
			return true;
		}   
		catch (Exception e) {    
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>copyFile "+oldPath+"("+newPath+")"+" :"+e.getMessage());
		} 
		return false;
	} 

	public static Boolean copyFolder(String oldFolder, String newFolder) throws Exception{
		try {   

			File folderFile=new File(oldFolder);
			if(!folderFile.exists()||!folderFile.isDirectory())
				throw new PathAccessException(oldFolder);
			
			FileOperator.createDir(newFolder);
			
			for (File file : folderFile.listFiles()) {
				if(file.isFile())
					copyFile(file.getAbsolutePath(), newFolder+File.separator+file.getName());
				if(file.isDirectory())
					copyFolder(file.getAbsolutePath(), newFolder+File.separator+file.getName()+File.separator);
			}
			
			return true;
		}   
		catch (Exception e) {    
			e.printStackTrace();
			LogOperator.WriteLogfortxt("FileOperator==>copyFolder "+oldFolder+"("+newFolder+")"+" :"+e.getMessage());
			throw e;
		}
	} 
	
	public static File[] getAllSubFolder(String rootPath) throws Exception{
		try {
			
			File folderFile=new File(rootPath);
			if(!folderFile.exists()||!folderFile.isDirectory())
				throw new PathAccessException(rootPath);
			
			return folderFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					// TODO Auto-generated method stub
					if(file.isDirectory()){
						return true;
					}
					return false;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			LogOperator.WriteLogfortxt("FileOperator==>getAllSubFolder "+rootPath+" :"+e.getMessage());
			throw e;
		}

	}
	
	public static File[] getAllSubFolder(String rootPath,String prefix) throws Exception{
		try {
			
			final String prefixFilter=prefix.toLowerCase();
			File folderFile=new File(rootPath);
			if(!folderFile.exists()||!folderFile.isDirectory())
				throw new PathAccessException(rootPath);
			
			return folderFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					// TODO Auto-generated method stub
					if(file.isDirectory()&&file.getName().toLowerCase().matches(prefixFilter+"(\\(\\d+\\))*")){
						return true;
					}
					return false;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			LogOperator.WriteLogfortxt("FileOperator==>getAllSubFolder "+rootPath+" :"+e.getMessage());
			throw e;
		}
	}
	
	public static String CheckFileName(String rootPath,String name) throws Exception{
		File[] sameNameFiles=FileOperator.getAllSubFolder(rootPath, name);
		if(sameNameFiles==null||sameNameFiles.length==0)
			return name;

		Integer newIndex=1;
		Integer index=1;
		for (File file : sameNameFiles) {
			Integer startIndex=file.getName().lastIndexOf("(");
			Integer endIndex=file.getName().lastIndexOf(")");
			if(startIndex<0||endIndex<0)
				continue;
			index=Integer.valueOf(file.getName().substring(startIndex+1,endIndex));
			if(index>=newIndex)
				newIndex=index+1;
		}

		return name+"("+newIndex+")";
	}
}
