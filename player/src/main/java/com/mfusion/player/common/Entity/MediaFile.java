package com.mfusion.player.common.Entity;

import com.mfusion.player.common.Enum.FileType;


public class MediaFile implements Cloneable {
	public int MediaId;
	public String ExtName;
	public String FilePath;
	public String FileName;
	public String FileMD5;
	public String StartDate;
	public String EndDate;
	public String Remark;
	public int Duration; // Seconds
	public String Effect;
	public String MediaSource;
	public FileType Type;
	public long mediaLength;
	@Override  
	public Object clone() throws CloneNotSupportedException  
	{  
		MediaFile media = new MediaFile();
		// �����õĶ���teacherҲclone��  
		media.MediaId=MediaId;
		media.ExtName=ExtName;
		media.FilePath=FilePath;
		media.FileName=FileName;
		media.FileMD5=FileMD5;
		media.StartDate=StartDate;
		media.EndDate=EndDate;
		media.Remark=Remark;
		media.Duration=Duration;
		media.Effect=Effect;
		media.MediaSource=MediaSource;
		media.Type=Type;
		media.mediaLength=mediaLength;
		return media;  
	}  
	
}
