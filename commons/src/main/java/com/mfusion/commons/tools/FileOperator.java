package com.mfusion.commons.tools;

import com.mfusion.commons.entity.exception.PathAccessException;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSortType;
import com.mfusion.commontools.R;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

	public static void orderByName(List<VisualTemplate> fileList, final FileSortType sortType){

		Collections.sort(fileList, new Comparator<VisualTemplate>() {
			@Override
			public int compare(VisualTemplate o1, VisualTemplate o2) {
				int result= o1.id.compareTo(o2.id);

				return sortType==FileSortType.NameAsce?result:-(result);
			}
		});
	}

	//按日期排序
	public static void orderByDate(List<VisualTemplate> fileList, final FileSortType sortType) {
		Collections.sort(fileList,new Comparator< VisualTemplate>(){
			public int compare(VisualTemplate temp1, VisualTemplate temp2) {
				int result=0;
				long diff = temp1.lastModifyTime - temp2.lastModifyTime;
				if (diff > 0)
					result= 1;
				else if (diff == 0)
					result= 0;
				else
					result= -1;

				return sortType==FileSortType.TimeAsce?result:-(result);
			}
		});
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


	static HashMap<String, Integer> image_list=null;
	public static int convertTypeToImage(String type) {
		if(image_list==null){
			image_list = new HashMap<String, Integer>();

			image_list.put("Unkown", R.drawable.filedialog_root);
			image_list.put("/", R.drawable.filedialog_root);
			image_list.put("...", R.drawable.filedialog_folder_up);
			image_list.put(".", R.drawable.filedialog_folder);
			image_list.put("Video", R.drawable.filedialog_wavfile);
			image_list.put("Sound", R.drawable.filedialog_audio);
			image_list.put("Image", R.drawable.filedialog_image);
		}
		if(image_list.containsKey(type))
			return image_list.get(type);

		return image_list.get("Unkown");
	}


	static List<String> audio_ext_list=null;
	public static List<String> getAudioExtList() {

		if(audio_ext_list==null){
			audio_ext_list = new ArrayList<String>();

			audio_ext_list.add("mp3");
			audio_ext_list.add("m4a");
			audio_ext_list.add("wav");
			audio_ext_list.add("amr");
			audio_ext_list.add("awb");
			audio_ext_list.add("wma");
			audio_ext_list.add("mid");
			audio_ext_list.add("xmf");
			audio_ext_list.add("rtttl");
			audio_ext_list.add("smf");
			audio_ext_list.add("imy");
			audio_ext_list.add("m3u");
			audio_ext_list.add("pls");
			audio_ext_list.add("ogg");
			audio_ext_list.add("wpl");
		}

		return audio_ext_list;
	}

	static List<String> image_ext_list=null;
	public static List<String> getImageExtList() {

		if(image_ext_list==null){
			image_ext_list = new ArrayList<String>();

			image_ext_list.add("jpg");
			image_ext_list.add("jpeg");
			image_ext_list.add("gif");
			image_ext_list.add("png");
			image_ext_list.add("bmp");
			image_ext_list.add("wbmp");
		}

		return image_ext_list;
	}

	static List<String> video_ext_list=null;
	public static List<String> getVideoExtList() {

		if(video_ext_list==null){
			video_ext_list = new ArrayList<String>();

			video_ext_list.add("mp4");
			video_ext_list.add("m4v");
			video_ext_list.add("3gp");
			video_ext_list.add("3gpp");
			video_ext_list.add("3g2");
			video_ext_list.add("3gpp2");
			video_ext_list.add("wmv");
		}

		return video_ext_list;
	}

	public static String getMeidaType(String mediaPath){
		int splitIndex=mediaPath.lastIndexOf(".");
		if(splitIndex<0){
			return "Unkown";
		}

		String extName=mediaPath.substring(splitIndex+1);
		for (String extString : getAudioExtList()) {
			if(extString.equals(extName))
				return "Sound";
		}
		for (String extString : getVideoExtList()) {
			if(extString.equals(extName))
				return "Video";
		}
		for (String extString : getImageExtList()) {
			if(extString.equals(extName))
				return "Image";
		}

		return extName;
	}
}
