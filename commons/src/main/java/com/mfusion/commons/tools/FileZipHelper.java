package com.mfusion.commons.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.mfusion.commons.entity.exception.PathAccessException;
/**
 * Created by guoyu on 7/14/2016.
 */
public class FileZipHelper {

	public static Boolean compressionFolder(String zipFileFath,String inputPath) throws Exception{
		try {
			
			File inputFile=new File(inputPath);
			if(!inputFile.exists())
				return false;
			
			File zipFile=new File(zipFileFath);
			if(!zipFile.getParentFile().exists())
				zipFile.getParentFile().mkdirs();
			if(zipFile.exists())
				zipFile.delete();

			zip(zipFileFath, inputFile);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	private static void zip(String zipFileName, File inputFile) throws Exception {  

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(  
                zipFileName));  
        BufferedOutputStream bo = new BufferedOutputStream(out);  
        zip(out, inputFile, inputFile.getName(), bo);  
        bo.close();  
        out.close();
    }  
  
    private static void zip(ZipOutputStream out, File f, String base,  
            BufferedOutputStream bo) throws Exception {
        if (f.isDirectory()) {  
            File[] fl = f.listFiles();  
            if (fl.length == 0) {  
                out.putNextEntry(new ZipEntry(base + "/"));
                System.out.println(base + "/");  
            }  
            for (int i = 0; i < fl.length; i++) {  
                zip(out, fl[i], base + "/" + fl[i].getName(), bo);
            }  
             
        } else {  
            out.putNextEntry(new ZipEntry(base));
            System.out.println(base);  
            FileInputStream in = new FileInputStream(f);  
            BufferedInputStream bi = new BufferedInputStream(in);  
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            } 
            bi.close();  
            in.close();
            out.closeEntry();
        }  
    } 
	
	public static Boolean deCompressionFolder(File file,String outputPath){
		
		ZipFile zipFile = null;
		try {
		   zipFile = new ZipFile(file);
		   Enumeration e = zipFile.entries();
		   ZipEntry zipEntry = null;
		   File dest = new File(outputPath);
		   dest.mkdirs();
		   while (e.hasMoreElements()) {
			   zipEntry = (ZipEntry) e.nextElement();
			   String entryName = zipEntry.getName();
			   InputStream in = null;
			   FileOutputStream out = null;
			   try {
				   if (zipEntry.isDirectory()) {
					   String name = zipEntry.getName();
					   name = name.substring(0, name.length() - 1);
					   File f = new File(outputPath + File.separator + name);
					   f.mkdirs();
				   } else {
					   int index = entryName.lastIndexOf("\\");
					   if (index != -1) {
						   File df = new File(outputPath + File.separator+ entryName.substring(0, index));
						   df.mkdirs();
					   }
					   index = entryName.lastIndexOf("/");
					   if (index != -1) {
						   File df = new File(outputPath + File.separator+ entryName.substring(0, index));
						   df.mkdirs();
					   }
					   File f = new File(outputPath + File.separator + zipEntry.getName());
					   // f.createNewFile();
					   in = zipFile.getInputStream(zipEntry);
					   out = new FileOutputStream(f);
					   int c;
					   byte[] by = new byte[1024];
					   while ((c = in.read(by)) != -1) {
						   out.write(by, 0, c);
					   }
					   out.flush();
				   }
			   } catch (IOException ex) {
				   ex.printStackTrace();
				   throw ex;
			   } 
			   finally {
				   if (in != null) {
					   try {
						   in.close();
					   } catch (IOException ex) {
					   }
				   }
				   if (out != null) {
					   try {
						   out.close();
					   } catch (IOException ex) {
					   }
				   }
			   }
		   }
		   
		   return true;
	   } catch (IOException ex) {
		   ex.printStackTrace();
		   return false;
	   } finally {
		   if (zipFile != null) {
			   try {
				   zipFile.close();
		    	} catch (IOException ex) {
		    	}
			}
		}
	}

}
