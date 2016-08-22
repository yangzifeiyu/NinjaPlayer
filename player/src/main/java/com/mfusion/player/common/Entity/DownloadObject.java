package com.mfusion.player.common.Entity;

import java.util.Date;

import com.mfusion.player.common.Enum.*;
import android.os.Parcel;
import android.os.Parcelable;

public class DownloadObject implements Parcelable {
    public String FileName;
    public String ExtName;
    //Դ�ļ���ȫ·��
    public String SourceFilePath;
    //Resize���ͼƬȫ·��
    public String ResizedImagePath;
    public int Width;
    public int Height;
    public FileType FileType;
    public String FileMD5;
    public double FileLength;
    public long LoadingLength;
    public long MediaLength;
    public int DownloadTimes;
    public String FileDir;
    public String ServerPath;
    public String FileSource;
    public int DownloadStatus=-1;
    public Date ModifyTime; 
    
    public DownloadObject(){}
    
    public DownloadObject(Parcel pl){ 
    	FileName = pl.readString(); 
    	ExtName = pl.readString();
    	SourceFilePath = pl.readString();
    	ResizedImagePath = pl.readString();
    	Width = pl.readInt(); 
    	Height = pl.readInt(); 
    	FileType = FileType.fromString(pl.readString()); 
    	FileMD5 = pl.readString();
    	FileLength=pl.readDouble();
    	LoadingLength=pl.readLong();
    	DownloadTimes = pl.readInt(); 
    	FileDir = pl.readString();
    	ServerPath = pl.readString();
    	FileSource = pl.readString();
    	DownloadStatus = pl.readInt(); 
    	ModifyTime=(Date)pl.readSerializable();
	} 
    
    @Override 
	public int describeContents() { 
		return 0; 
	} 

	@Override 
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(FileName); 
		dest.writeString(ExtName); 
		dest.writeString(SourceFilePath); 
		dest.writeString(ResizedImagePath); 
		dest.writeInt(Width);
		dest.writeInt(Height);
		dest.writeString(FileType.getValue()); 
		dest.writeString(FileMD5); 
		dest.writeDouble(FileLength);
		dest.writeLong(LoadingLength);
		dest.writeInt(DownloadTimes);
		dest.writeString(FileDir); 
		dest.writeString(ServerPath); 
		dest.writeString(FileSource); 
		dest.writeInt(DownloadStatus);
		dest.writeSerializable(ModifyTime);
	} 

	public static final Creator<DownloadObject> CREATOR = new Creator<DownloadObject>() {

		@Override  
		public DownloadObject createFromParcel(Parcel source) {  
			return new DownloadObject(source); 
		}  

		@Override  
		public DownloadObject[] newArray( int size) {  
			return new DownloadObject[size]; 
		}  

	};  

}
