package com.mfusion.templatedesigner.previewcomponent.entity;

import android.R.integer;

import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaType;

public class ScheduleMediaEntity {

	public String mediaName;
	public ScheduleMediaType mediaType;
	public String mediaSource="Local";
	public String mediaPath;
	public int duration;
	public String durationString;
	public String effect="none";

	public void setIntDuration(int duration){
		this.duration=duration;
		this.durationString=ConvertSecondsToStr(duration);
	}
	
	public void setStringDuration(String durationStr){
		try {
			String[] times=durationStr.split(":");
			this.duration=(Integer.parseInt(times[0])*60+Integer.parseInt(times[1]))*60+Integer.parseInt(times[2]);
			this.durationString=durationStr;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static String ConvertSecondsToStr(int seconds) {
		int mins=seconds/60;
		seconds=seconds%60;
		int hours=mins/60;
		mins=mins%60;
		return (hours<10?("0"+hours):hours)+":"+(mins<10?("0"+mins):mins)+":"+(seconds<10?("0"+seconds):seconds);
	}
}
