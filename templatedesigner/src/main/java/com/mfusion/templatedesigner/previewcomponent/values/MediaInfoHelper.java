package com.mfusion.templatedesigner.previewcomponent.values;

import android.media.MediaMetadataRetriever;

import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;

public class MediaInfoHelper {
	public static ScheduleMediaEntity getMediaInfo(String mediaPath){
		ScheduleMediaEntity mediaItem=null;
		try {
			mediaItem=new ScheduleMediaEntity();
			mediaItem.mediaPath=mediaPath;
			mediaItem.mediaName=mediaPath.substring(mediaPath.lastIndexOf("/")+1);
			mediaItem.mediaType=getMeidaType(mediaPath);
			if(mediaItem.mediaType==ScheduleMediaType.Video||mediaItem.mediaType==ScheduleMediaType.Sound)
				mediaItem.setIntDuration(getMeidaDuration(mediaPath));
			else
				mediaItem.setIntDuration(5);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mediaItem;
	}
	
	public static ScheduleMediaType getMeidaType(String mediaPath){
		int splitIndex=mediaPath.lastIndexOf(".");
		if(splitIndex<0){
			return ScheduleMediaType.Unknow;
		}
		
		String extName=mediaPath.substring(splitIndex+1);
		for (String extString : PropertyValues.getAudioExtList()) {
			if(extString.equals(extName))
				return ScheduleMediaType.Sound;
		}
		for (String extString : PropertyValues.getVideoExtList()) {
			if(extString.equals(extName))
				return ScheduleMediaType.Video;
		}
		for (String extString : PropertyValues.getImageExtList()) {
			if(extString.equals(extName))
				return ScheduleMediaType.Image;
		}
		
		return ScheduleMediaType.Unknow;
	}

	static MediaMetadataRetriever retriever = new MediaMetadataRetriever(); 
	public static Integer getMeidaDuration(String mediaPath){
		try {
			
			retriever.setDataSource(mediaPath); 
			// ȡ����Ƶ�ĳ���(��λΪ����) 
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); 
			// ȡ����Ƶ�ĳ���(��λΪ��)  
			return Integer.valueOf(time) / 1000; 
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 5;
	}
	
	public static String ConvertSecondsToStr(int seconds) {
		int mins=seconds/60;
		seconds=seconds%60;
		int hours=mins/60;
		mins=mins%60;
		return (hours<10?("0"+hours):hours)+":"+(mins<10?("0"+mins):mins)+":"+(seconds<10?("0"+seconds):seconds);
	}
}
