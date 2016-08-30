package com.mfusion.player.common.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mfusion.player.common.Enum.PlayMode;



public class Playlist implements Cloneable  {
	public int ID;
	public int Index;
    public PlayMode Mode=PlayMode.sequence;
    public List<MediaFile> Medias = new ArrayList<MediaFile>();
    public Date StartTime;
    public Date EndTime;
    public int Duration = 0;
    @Override  
    public Object clone() throws CloneNotSupportedException  
    {  
    	Playlist pl = new Playlist();
        // �����õĶ���teacherҲclone��  
    	pl.Duration=Duration;
		pl.ID=ID;
		pl.Index=Index;
		pl.Medias=new ArrayList<MediaFile>(Medias);
		pl.Mode=Mode;
		pl.StartTime=StartTime;
		pl.EndTime=EndTime;
        return pl;  
    }  
}
